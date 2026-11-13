package com.zoe.tvlauncher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zoe.tvlauncher.ui.components.AllAppsSection
import com.zoe.tvlauncher.ui.components.FavoritesRow
import com.zoe.tvlauncher.ui.components.QuickAccessBar
import com.zoe.tvlauncher.ui.components.TopStatusBar
import com.zoe.tvlauncher.ui.dialogs.AppActionsDialog
import com.zoe.tvlauncher.ui.dialogs.SettingsDialog
import com.zoe.tvlauncher.viewmodel.LauncherViewModel

@Composable
fun HomeScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val currentTheme by viewModel.currentTheme.collectAsState()
    val systemStatus by viewModel.systemStatus.collectAsState()
    val favoriteApps by viewModel.favoriteApps.collectAsState()
    val visibleApps by viewModel.visibleApps.collectAsState()
    val hiddenApps by viewModel.hiddenApps.collectAsState()
    val isLoadingApps by viewModel.isLoadingApps.collectAsState()
    val is24Hour by viewModel.is24HourFormat.collectAsState()

    val selectedAppForActions by viewModel.selectedAppForActions.collectAsState()
    val isSettingsOpen by viewModel.isSettingsOpen.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(currentTheme.gradientBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Top Status Section
            TopStatusBar(
                systemStatus = systemStatus,
                wallpaperTheme = currentTheme,
                onOpenSettings = { viewModel.openLauncherSettings() },
                onRefreshApps = { viewModel.refreshApps() }
            )

            // 2. Quick Access Row
            QuickAccessBar(
                wallpaperTheme = currentTheme,
                onOpenSettings = { viewModel.openSettings() },
                onOpenWifi = { viewModel.openWifi() },
                onOpenBluetooth = { viewModel.openBluetooth() },
                onOpenDisplay = { viewModel.openDisplay() },
                onOpenSound = { viewModel.openSound() },
                onOpenFiles = { viewModel.openFileManager() },
                onOpenThemeSettings = { viewModel.openLauncherSettings() },
                onOpenHiddenApps = { viewModel.openLauncherSettings() }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Pinned Favorites Row
            FavoritesRow(
                favoriteApps = favoriteApps,
                wallpaperTheme = currentTheme,
                onAppClick = { app -> viewModel.launchApp(app) },
                onAppLongClick = { app -> viewModel.openAppActions(app) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 4. All Applications Section
            AllAppsSection(
                apps = visibleApps,
                isLoading = isLoadingApps,
                wallpaperTheme = currentTheme,
                onAppClick = { app -> viewModel.launchApp(app) },
                onAppLongClick = { app -> viewModel.openAppActions(app) },
                modifier = Modifier.weight(1f, fill = false)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Action Modal for App Tile Long-Press / Menu Button
        selectedAppForActions?.let { app ->
            AppActionsDialog(
                appInfo = app,
                wallpaperTheme = currentTheme,
                onLaunch = {
                    viewModel.launchApp(app)
                    viewModel.closeAppActions()
                },
                onToggleFavorite = { viewModel.toggleFavorite(app) },
                onToggleHide = { viewModel.toggleHideApp(app) },
                onAppInfo = { viewModel.openAppInfo(app) },
                onUninstall = { viewModel.uninstallApp(app) },
                onDismiss = { viewModel.closeAppActions() }
            )
        }

        // Launcher Settings Modal
        if (isSettingsOpen) {
            SettingsDialog(
                currentTheme = currentTheme,
                is24Hour = is24Hour,
                hiddenApps = hiddenApps,
                onSelectTheme = { theme -> viewModel.setTheme(theme) },
                onToggle24Hour = { is24 -> viewModel.set24HourFormat(is24) },
                onUnhideApp = { app -> viewModel.toggleHideApp(app) },
                onUnhideAll = { viewModel.unhideAll() },
                onResetFavorites = { viewModel.resetFavorites() },
                onDismiss = { viewModel.closeLauncherSettings() }
            )
        }
    }
}
