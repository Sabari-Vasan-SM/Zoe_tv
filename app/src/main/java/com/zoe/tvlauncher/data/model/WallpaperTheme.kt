package com.zoe.tvlauncher.data.model

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

enum class WallpaperTheme(
    val title: String,
    val description: String,
    val isDark: Boolean,
    val primaryAccent: Color,
    val secondaryAccent: Color,
    val backgroundColors: List<Color>,
    val cardBackground: Color,
    val cardBorderFocused: Color
) {
    DEEP_OBSIDIAN(
        title = "Deep Obsidian",
        description = "Minimal dark with subtle cyan luminescence",
        isDark = true,
        primaryAccent = Color(0xFF00E5FF),
        secondaryAccent = Color(0xFF7C4DFF),
        backgroundColors = listOf(Color(0xFF0A0C10), Color(0xFF10141D), Color(0xFF07080B)),
        cardBackground = Color(0x33FFFFFF),
        cardBorderFocused = Color(0xFF00E5FF)
    ),
    MIDNIGHT_NEBULA(
        title = "Midnight Nebula",
        description = "Deep indigo cosmic gradients",
        isDark = true,
        primaryAccent = Color(0xFF8C52FF),
        secondaryAccent = Color(0xFF00D2FF),
        backgroundColors = listOf(Color(0xFF0B0A1A), Color(0xFF14132B), Color(0xFF070612)),
        cardBackground = Color(0x2EFFFFFF),
        cardBorderFocused = Color(0xFFB388FF)
    ),
    AURORA_BOREALIS(
        title = "Aurora Borealis",
        description = "Atmospheric violet and emerald glow",
        isDark = true,
        primaryAccent = Color(0xFF00E676),
        secondaryAccent = Color(0xFF651FFF),
        backgroundColors = listOf(Color(0xFF081211), Color(0xFF0F2321), Color(0xFF050B0A)),
        cardBackground = Color(0x2EFFFFFF),
        cardBorderFocused = Color(0xFF69F0AE)
    ),
    SUNSET_EMBER(
        title = "Sunset Ember",
        description = "Warm twilight charcoal with crimson copper",
        isDark = true,
        primaryAccent = Color(0xFFFF5252),
        secondaryAccent = Color(0xFFFF9100),
        backgroundColors = listOf(Color(0xFF140B0B), Color(0xFF221111), Color(0xFF0A0505)),
        cardBackground = Color(0x2EFFFFFF),
        cardBorderFocused = Color(0xFFFF7043)
    ),
    EMERALD_FOREST(
        title = "Emerald Forest",
        description = "Deep pine and Nordic slate mist",
        isDark = true,
        primaryAccent = Color(0xFF26A69A),
        secondaryAccent = Color(0xFF80CBC4),
        backgroundColors = listOf(Color(0xFF0A120E), Color(0xFF111E18), Color(0xFF070B09)),
        cardBackground = Color(0x2EFFFFFF),
        cardBorderFocused = Color(0xFF4DB6AC)
    ),
    STUDIO_LIGHT(
        title = "Studio Minimal",
        description = "Clean high-contrast light theme",
        isDark = false,
        primaryAccent = Color(0xFF006688),
        secondaryAccent = Color(0xFF5E35B1),
        backgroundColors = listOf(Color(0xFFE8ECEF), Color(0xFFF2F5F8), Color(0xFFDCE2E6)),
        cardBackground = Color(0xFFFFFFFF),
        cardBorderFocused = Color(0xFF007799)
    );

    val gradientBrush: Brush
        get() = Brush.verticalGradient(
            colors = backgroundColors
        )
}
