package com.zoe.tvlauncher.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.zoe.tvlauncher.data.model.WallpaperTheme

private val DarkColorScheme = darkColorScheme(
    primary = CyanAccent,
    secondary = VioletAccent,
    background = DeepDark,
    surface = CardDark,
    onPrimary = DeepDark,
    onSecondary = PureWhite,
    onBackground = PureWhite,
    onSurface = PureWhite
)

private val LightColorScheme = lightColorScheme(
    primary = CyanAccent,
    secondary = VioletAccent,
    background = PureWhite,
    surface = PureWhite,
    onPrimary = PureWhite,
    onSecondary = PureWhite,
    onBackground = DeepDark,
    onSurface = DeepDark
)

@Composable
fun ZoeTvTheme(
    wallpaperTheme: WallpaperTheme = WallpaperTheme.DEEP_OBSIDIAN,
    content: @Composable () -> Unit
) {
    val colorScheme = if (wallpaperTheme.isDark) {
        DarkColorScheme.copy(
            primary = wallpaperTheme.primaryAccent,
            secondary = wallpaperTheme.secondaryAccent
        )
    } else {
        LightColorScheme.copy(
            primary = wallpaperTheme.primaryAccent,
            secondary = wallpaperTheme.secondaryAccent
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ZoeTypography,
        content = content
    )
}
