package com.zoe.tvlauncher

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.zoe.tvlauncher.ui.screens.HomeScreen
import com.zoe.tvlauncher.ui.theme.ZoeTvTheme
import com.zoe.tvlauncher.viewmodel.LauncherViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Handle remote BACK key:
        // If modals/dialogs are open, close them. If on the main launcher, keep user safely in launcher.
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewModel.selectedAppForActions.value != null) {
                    viewModel.closeAppActions()
                } else if (viewModel.isSettingsOpen.value) {
                    viewModel.closeLauncherSettings()
                } else {
                    // Do nothing - Android TV launchers stay on home screen when back is pressed
                }
            }
        })

        setContent {
            val currentTheme by viewModel.currentTheme.collectAsState()

            ZoeTvTheme(wallpaperTheme = currentTheme) {
                HomeScreen(viewModel = viewModel)
            }
        }

        // Setup immersive mode after decor view is created by setContent
        setupTvImmersiveMode()
    }

    override fun onResume() {
        super.onResume()
        setupTvImmersiveMode()
        viewModel.refreshApps()
    }

    private fun setupTvImmersiveMode() {
        try {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val controller = WindowCompat.getInsetsController(window, window.decorView)
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller.hide(WindowInsetsCompat.Type.systemBars())
        } catch (_: Exception) {}
    }
}
