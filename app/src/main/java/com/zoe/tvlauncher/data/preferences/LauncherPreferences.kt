package com.zoe.tvlauncher.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.zoe.tvlauncher.data.model.WallpaperTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LauncherPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("zoe_tv_launcher_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_FAVORITES = "pref_favorites"
        private const val KEY_HIDDEN_APPS = "pref_hidden_apps"
        private const val KEY_WALLPAPER_THEME = "pref_wallpaper_theme"
        private const val KEY_IS_24_HOUR = "pref_is_24_hour"
        private const val KEY_SHOW_BATTERY = "pref_show_battery"
        private const val KEY_LAST_FOCUSED = "pref_last_focused"
    }

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    private val _hiddenApps = MutableStateFlow<Set<String>>(emptySet())
    val hiddenApps: StateFlow<Set<String>> = _hiddenApps.asStateFlow()

    private val _currentTheme = MutableStateFlow(WallpaperTheme.DEEP_OBSIDIAN)
    val currentTheme: StateFlow<WallpaperTheme> = _currentTheme.asStateFlow()

    private val _is24HourFormat = MutableStateFlow(false)
    val is24HourFormat: StateFlow<Boolean> = _is24HourFormat.asStateFlow()

    private val _showBattery = MutableStateFlow(true)
    val showBattery: StateFlow<Boolean> = _showBattery.asStateFlow()

    private val _lastFocusedPackage = MutableStateFlow<String?>(null)
    val lastFocusedPackage: StateFlow<String?> = _lastFocusedPackage.asStateFlow()

    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        val favSet = prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
        _favorites.value = favSet.toSet()

        val hiddenSet = prefs.getStringSet(KEY_HIDDEN_APPS, emptySet()) ?: emptySet()
        _hiddenApps.value = hiddenSet.toSet()

        val themeName = prefs.getString(KEY_WALLPAPER_THEME, WallpaperTheme.DEEP_OBSIDIAN.name)
        _currentTheme.value = try {
            WallpaperTheme.valueOf(themeName ?: WallpaperTheme.DEEP_OBSIDIAN.name)
        } catch (e: Exception) {
            WallpaperTheme.DEEP_OBSIDIAN
        }

        _is24HourFormat.value = prefs.getBoolean(KEY_IS_24_HOUR, false)
        _showBattery.value = prefs.getBoolean(KEY_SHOW_BATTERY, true)
        _lastFocusedPackage.value = prefs.getString(KEY_LAST_FOCUSED, null)
    }

    fun toggleFavorite(packageName: String): Boolean {
        val current = _favorites.value.toMutableSet()
        val willBeFavorite = !current.contains(packageName)
        if (willBeFavorite) {
            current.add(packageName)
        } else {
            current.remove(packageName)
        }
        _favorites.value = current
        prefs.edit().putStringSet(KEY_FAVORITES, current).apply()
        return willBeFavorite
    }

    fun isFavorite(packageName: String): Boolean = _favorites.value.contains(packageName)

    fun toggleHideApp(packageName: String): Boolean {
        val current = _hiddenApps.value.toMutableSet()
        val willBeHidden = !current.contains(packageName)
        if (willBeHidden) {
            current.add(packageName)
            // If hiding, also remove from favorites
            if (_favorites.value.contains(packageName)) {
                val favs = _favorites.value.toMutableSet()
                favs.remove(packageName)
                _favorites.value = favs
                prefs.edit().putStringSet(KEY_FAVORITES, favs).apply()
            }
        } else {
            current.remove(packageName)
        }
        _hiddenApps.value = current
        prefs.edit().putStringSet(KEY_HIDDEN_APPS, current).apply()
        return willBeHidden
    }

    fun isHidden(packageName: String): Boolean = _hiddenApps.value.contains(packageName)

    fun unhideAll() {
        _hiddenApps.value = emptySet()
        prefs.edit().remove(KEY_HIDDEN_APPS).apply()
    }

    fun setTheme(theme: WallpaperTheme) {
        _currentTheme.value = theme
        prefs.edit().putString(KEY_WALLPAPER_THEME, theme.name).apply()
    }

    fun set24HourFormat(is24: Boolean) {
        _is24HourFormat.value = is24
        prefs.edit().putBoolean(KEY_IS_24_HOUR, is24).apply()
    }

    fun setShowBattery(show: Boolean) {
        _showBattery.value = show
        prefs.edit().putBoolean(KEY_SHOW_BATTERY, show).apply()
    }

    fun setLastFocusedPackage(pkg: String) {
        _lastFocusedPackage.value = pkg
        prefs.edit().putString(KEY_LAST_FOCUSED, pkg).apply()
    }

    fun resetFavorites() {
        _favorites.value = emptySet()
        prefs.edit().remove(KEY_FAVORITES).apply()
    }
}
