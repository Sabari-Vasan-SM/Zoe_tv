package com.zoe.tvlauncher.data.manager

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.LruCache
import com.zoe.tvlauncher.data.model.AppInfo
import com.zoe.tvlauncher.data.preferences.LauncherPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppsManager(
    private val context: Context,
    private val preferences: LauncherPreferences,
    private val scope: CoroutineScope
) {
    private val packageManager: PackageManager = context.packageManager

    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // In-memory icon cache for smooth 60fps scrolling
    private val iconCache = object : LruCache<String, Bitmap>(100) {
        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            return bitmap.byteCount / 1024
        }
    }

    private val packageChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context?, intent: Intent?) {
            // Instantly refresh apps on install/uninstall/replace
            refreshApps()
        }
    }

    fun start() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addDataScheme("package")
        }
        context.registerReceiver(packageChangeReceiver, filter)
        refreshApps()
    }

    fun stop() {
        try {
            context.unregisterReceiver(packageChangeReceiver)
        } catch (_: Exception) {}
    }

    fun refreshApps() {
        scope.launch(Dispatchers.IO) {
            _isLoading.value = true
            val apps = queryAllApps()
            _installedApps.value = apps
            _isLoading.value = false
        }
    }

    private suspend fun queryAllApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val appMap = mutableMapOf<String, AppInfo>()
        val myPackage = context.packageName

        // 1. Query Leanback Launcher apps (native TV apps)
        val leanbackIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
        }
        val leanbackResolveInfos: List<ResolveInfo> = try {
            packageManager.queryIntentActivities(
                leanbackIntent,
                PackageManager.MATCH_ALL
            )
        } catch (_: Exception) {
            emptyList()
        }

        for (resolveInfo in leanbackResolveInfos) {
            val pkg = resolveInfo.activityInfo.packageName
            if (pkg == myPackage) continue

            val label = resolveInfo.loadLabel(packageManager).toString()
            val activityName = resolveInfo.activityInfo.name
            val isSystem = (resolveInfo.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            val icon = getCachedOrLoadIcon(resolveInfo)

            val appInfo = AppInfo(
                packageName = pkg,
                activityName = activityName,
                label = label,
                isFavorite = preferences.isFavorite(pkg),
                isHidden = preferences.isHidden(pkg),
                isSystemApp = isSystem,
                iconBitmap = icon
            )
            appMap[pkg] = appInfo
        }

        // 2. Query Standard Launcher apps (sideloaded or non-leanback apps)
        val standardIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val standardResolveInfos: List<ResolveInfo> = try {
            packageManager.queryIntentActivities(
                standardIntent,
                PackageManager.MATCH_ALL
            )
        } catch (_: Exception) {
            emptyList()
        }

        for (resolveInfo in standardResolveInfos) {
            val pkg = resolveInfo.activityInfo.packageName
            if (pkg == myPackage || appMap.containsKey(pkg)) continue

            val label = resolveInfo.loadLabel(packageManager).toString()
            val activityName = resolveInfo.activityInfo.name
            val isSystem = (resolveInfo.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            val icon = getCachedOrLoadIcon(resolveInfo)

            val appInfo = AppInfo(
                packageName = pkg,
                activityName = activityName,
                label = label,
                isFavorite = preferences.isFavorite(pkg),
                isHidden = preferences.isHidden(pkg),
                isSystemApp = isSystem,
                iconBitmap = icon
            )
            appMap[pkg] = appInfo
        }

        // Return sorted alphabetically by label
        appMap.values.sortedBy { it.label.lowercase() }
    }

    private fun getCachedOrLoadIcon(resolveInfo: ResolveInfo): Bitmap? {
        val key = "${resolveInfo.activityInfo.packageName}/${resolveInfo.activityInfo.name}"
        iconCache.get(key)?.let { return it }

        try {
            // First check if there's a TV banner
            val bannerDrawable = resolveInfo.activityInfo.loadBanner(packageManager)
            val drawable = bannerDrawable ?: resolveInfo.loadIcon(packageManager)
            val bitmap = drawableToBitmap(drawable)
            if (bitmap != null) {
                iconCache.put(key, bitmap)
            }
            return bitmap
        } catch (e: Exception) {
            return null
        }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap? {
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            return drawable.bitmap
        }

        val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth.coerceAtMost(192) else 128
        val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight.coerceAtMost(192) else 128

        return try {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: Exception) {
            null
        }
    }

    fun launchApp(appInfo: AppInfo): Boolean {
        preferences.setLastFocusedPackage(appInfo.packageName)
        return try {
            // Try specific component first
            val intent = Intent(Intent.ACTION_MAIN).apply {
                component = ComponentName(appInfo.packageName, appInfo.activityName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            // Fallback to leanback launcher or regular launcher intent
            try {
                val launchIntent = packageManager.getLeanbackLaunchIntentForPackage(appInfo.packageName)
                    ?: packageManager.getLaunchIntentForPackage(appInfo.packageName)
                if (launchIntent != null) {
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                    context.startActivity(launchIntent)
                    true
                } else false
            } catch (_: Exception) {
                false
            }
        }
    }

    fun openAppInfo(packageName: String) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:$packageName")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    fun uninstallApp(packageName: String) {
        try {
            val intent = Intent(Intent.ACTION_DELETE).apply {
                data = Uri.parse("package:$packageName")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    fun openSettings() {
        launchAction(Settings.ACTION_SETTINGS)
    }

    fun openWifiSettings() {
        launchAction(Settings.ACTION_WIFI_SETTINGS, Settings.ACTION_WIRELESS_SETTINGS)
    }

    fun openBluetoothSettings() {
        launchAction(Settings.ACTION_BLUETOOTH_SETTINGS)
    }

    fun openDisplaySettings() {
        launchAction(Settings.ACTION_DISPLAY_SETTINGS)
    }

    fun openSoundSettings() {
        launchAction(Settings.ACTION_SOUND_SETTINGS)
    }

    fun openFileManager() {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(Uri.parse("content://media/external/file"), "*/*")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            launchAction(Settings.ACTION_INTERNAL_STORAGE_SETTINGS, Settings.ACTION_SETTINGS)
        }
    }

    private fun launchAction(vararg actions: String) {
        for (action in actions) {
            try {
                val intent = Intent(action).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                return
            } catch (_: Exception) {}
        }
    }
}
