package com.zoe.tvlauncher

import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import com.zoe.tvlauncher.ui.screens.HomeScreen
import com.zoe.tvlauncher.ui.theme.ZoeTvTheme
import com.zoe.tvlauncher.viewmodel.LauncherViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make fullscreen immersive for TV
        setupTvImmersiveMode()

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
    }

    override fun onResume() {
        super.onResume()
        setupTvImmersiveMode()
        viewModel.refreshApps()
    }

    private fun setupTvImmersiveMode() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                    )
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            // Opening settings or context
            if (viewModel.isSettingsOpen.value) {
                viewModel.closeLauncherSettings()
            } else {
                viewModel.openLauncherSettings()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
