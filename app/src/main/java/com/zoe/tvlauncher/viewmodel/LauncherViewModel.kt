package com.zoe.tvlauncher.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zoe.tvlauncher.data.manager.AppsManager
import com.zoe.tvlauncher.data.manager.SystemStatus
import com.zoe.tvlauncher.data.manager.SystemStatusMonitor
import com.zoe.tvlauncher.data.model.AppInfo
import com.zoe.tvlauncher.data.model.WallpaperTheme
import com.zoe.tvlauncher.data.preferences.LauncherPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

enum class LauncherSection {
    STATUS_BAR,
    QUICK_ACCESS,
    FAVORITES,
    ALL_APPS
}

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = LauncherPreferences(application)
    private val appsManager = AppsManager(application, preferences, viewModelScope)
    private val statusMonitor = SystemStatusMonitor(application, viewModelScope)

    val currentTheme: StateFlow<WallpaperTheme> = preferences.currentTheme
    val is24HourFormat: StateFlow<Boolean> = preferences.is24HourFormat
    val showBattery: StateFlow<Boolean> = preferences.showBattery
    val systemStatus: StateFlow<SystemStatus> = statusMonitor.status
    val isLoadingApps: StateFlow<Boolean> = appsManager.isLoading

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedAppForActions = MutableStateFlow<AppInfo?>(null)
    val selectedAppForActions: StateFlow<AppInfo?> = _selectedAppForActions.asStateFlow()

    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    private val _isManagingHiddenApps = MutableStateFlow(false)
    val isManagingHiddenApps: StateFlow<Boolean> = _isManagingHiddenApps.asStateFlow()

    // Observable combined lists:
    // Pinned favorites: apps that are marked as favorite and NOT hidden
    val favoriteApps: StateFlow<List<AppInfo>> = combine(
        appsManager.installedApps,
        preferences.favorites,
        preferences.hiddenApps
    ) { apps, favs, hidden ->
        apps.filter { favs.contains(it.packageName) && !hidden.contains(it.packageName) }
            .map { it.copy(isFavorite = true, isHidden = false) }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // All Visible Apps: apps not hidden, filtered by search query if any
    val visibleApps: StateFlow<List<AppInfo>> = combine(
        appsManager.installedApps,
        preferences.favorites,
        preferences.hiddenApps,
        _searchQuery
    ) { apps, favs, hidden, query ->
        apps.filter { !hidden.contains(it.packageName) }
            .filter { query.isBlank() || it.label.contains(query, ignoreCase = true) }
            .map { it.copy(isFavorite = favs.contains(it.packageName), isHidden = false) }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Hidden apps for management
    val hiddenApps: StateFlow<List<AppInfo>> = combine(
        appsManager.installedApps,
        preferences.hiddenApps
    ) { apps, hidden ->
        apps.filter { hidden.contains(it.packageName) }
            .map { it.copy(isHidden = true) }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        statusMonitor.start(preferences.is24HourFormat.value)
        appsManager.start()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun launchApp(appInfo: AppInfo) {
        appsManager.launchApp(appInfo)
    }

    fun toggleFavorite(appInfo: AppInfo) {
        preferences.toggleFavorite(appInfo.packageName)
    }

    fun toggleHideApp(appInfo: AppInfo) {
        preferences.toggleHideApp(appInfo.packageName)
    }

    fun unhideAll() {
        preferences.unhideAll()
    }

    fun openAppInfo(appInfo: AppInfo) {
        appsManager.openAppInfo(appInfo.packageName)
        closeAppActions()
    }

    fun uninstallApp(appInfo: AppInfo) {
        appsManager.uninstallApp(appInfo.packageName)
        closeAppActions()
    }

    fun openAppActions(appInfo: AppInfo) {
        _selectedAppForActions.value = appInfo
    }

    fun closeAppActions() {
        _selectedAppForActions.value = null
    }

    fun openLauncherSettings() {
        _isSettingsOpen.value = true
    }

    fun closeLauncherSettings() {
        _isSettingsOpen.value = false
    }

    fun openManageHiddenApps() {
        _isManagingHiddenApps.value = true
    }

    fun closeManageHiddenApps() {
        _isManagingHiddenApps.value = false
    }

    fun setTheme(theme: WallpaperTheme) {
        preferences.setTheme(theme)
    }

    fun set24HourFormat(is24: Boolean) {
        preferences.set24HourFormat(is24)
        statusMonitor.setTimeFormat(is24)
    }

    fun setShowBattery(show: Boolean) {
        preferences.setShowBattery(show)
    }

    fun resetFavorites() {
        preferences.resetFavorites()
    }

    fun refreshApps() {
        appsManager.refreshApps()
    }

    // Quick access system shortcuts
    fun openSettings() = appsManager.openSettings()
    fun openWifi() = appsManager.openWifiSettings()
    fun openBluetooth() = appsManager.openBluetoothSettings()
    fun openDisplay() = appsManager.openDisplaySettings()
    fun openSound() = appsManager.openSoundSettings()
    fun openFileManager() = appsManager.openFileManager()

    override fun onCleared() {
        super.onCleared()
        statusMonitor.stop()
        appsManager.stop()
    }
}
