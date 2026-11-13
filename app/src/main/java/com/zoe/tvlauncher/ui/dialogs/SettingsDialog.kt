package com.zoe.tvlauncher.ui.dialogs

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.zoe.tvlauncher.data.model.AppInfo
import com.zoe.tvlauncher.data.model.WallpaperTheme
import com.zoe.tvlauncher.ui.components.tvFocusable

@Composable
fun SettingsDialog(
    currentTheme: WallpaperTheme,
    is24Hour: Boolean,
    hiddenApps: List<AppInfo>,
    onSelectTheme: (WallpaperTheme) -> Unit,
    onToggle24Hour: (Boolean) -> Unit,
    onUnhideApp: (AppInfo) -> Unit,
    onUnhideAll: () -> Unit,
    onResetFavorites: () -> Unit,
    onDismiss: () -> Unit
) {
    val dialogBg = if (currentTheme.isDark) Color(0xFF11141A) else Color(0xFFF4F6F9)
    val cardBg = if (currentTheme.isDark) Color(0x24FFFFFF) else Color(0x14000000)
    val textColor = if (currentTheme.isDark) Color.White else Color(0xFF1E222A)
    val subtleColor = if (currentTheme.isDark) Color(0x99FFFFFF) else Color(0x991E222A)

    var selectedTab by remember { mutableStateOf(0) } // 0: Appearance, 1: Preferences, 2: Hidden Apps, 3: About

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = dialogBg,
            shadowElevation = 24.dp,
            modifier = Modifier
                .width(720.dp)
                .height(480.dp)
                .clip(RoundedCornerShape(24.dp))
        ) {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                // Left Navigation Sidebar
                Column(
                    modifier = Modifier
                        .width(220.dp)
                        .fillMaxHeight()
                        .background(if (currentTheme.isDark) Color(0x1FFFFFFF) else Color(0x0A000000))
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "SETTINGS",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            fontSize = 13.sp
                        ),
                        color = currentTheme.primaryAccent,
                        modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                    )

                    SettingsNavButton(
                        title = "Appearance",
                        icon = Icons.Default.Palette,
                        isSelected = selectedTab == 0,
                        currentTheme = currentTheme,
                        onClick = { selectedTab = 0 }
                    )

                    SettingsNavButton(
                        title = "Clock & Display",
                        icon = Icons.Default.Schedule,
                        isSelected = selectedTab == 1,
                        currentTheme = currentTheme,
                        onClick = { selectedTab = 1 }
                    )

                    SettingsNavButton(
                        title = "Hidden Apps (${hiddenApps.size})",
                        icon = Icons.Default.VisibilityOff,
                        isSelected = selectedTab == 2,
                        currentTheme = currentTheme,
                        onClick = { selectedTab = 2 }
                    )

                    SettingsNavButton(
                        title = "About System",
                        icon = Icons.Default.Info,
                        isSelected = selectedTab == 3,
                        currentTheme = currentTheme,
                        onClick = { selectedTab = 3 }
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    SettingsNavButton(
                        title = "Close",
                        icon = Icons.Default.Close,
                        isSelected = false,
                        currentTheme = currentTheme,
                        onClick = onDismiss
                    )
                }

                // Right Content Panel
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(28.dp)
                ) {
                    when (selectedTab) {
                        0 -> AppearanceSettingsTab(
                            currentTheme = currentTheme,
                            onSelectTheme = onSelectTheme
                        )
                        1 -> PreferencesSettingsTab(
                            currentTheme = currentTheme,
                            is24Hour = is24Hour,
                            onToggle24Hour = onToggle24Hour,
                            onResetFavorites = onResetFavorites
                        )
                        2 -> HiddenAppsSettingsTab(
                            currentTheme = currentTheme,
                            hiddenApps = hiddenApps,
                            onUnhideApp = onUnhideApp,
                            onUnhideAll = onUnhideAll
                        )
                        3 -> AboutSettingsTab(
                            currentTheme = currentTheme
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsNavButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    currentTheme: WallpaperTheme,
    onClick: () -> Unit
) {
    val bg = if (isSelected) currentTheme.primaryAccent else Color.Transparent
    val contentColor = if (isSelected) Color.Black else if (currentTheme.isDark) Color.White else Color(0xFF1E222A)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .tvFocusable(
                shape = RoundedCornerShape(12.dp),
                scaleFactor = 1.05f,
                focusedBorderColor = currentTheme.primaryAccent,
                onClick = onClick
            )
            .background(bg, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            color = contentColor
        )
    }
}

@Composable
private fun AppearanceSettingsTab(
    currentTheme: WallpaperTheme,
    onSelectTheme: (WallpaperTheme) -> Unit
) {
    val textColor = if (currentTheme.isDark) Color.White else Color(0xFF1E222A)
    val subtleColor = if (currentTheme.isDark) Color(0x99FFFFFF) else Color(0x991E222A)

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Wallpaper & Theme",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = textColor
        )
        Text(
            text = "Select a refined atmospheric background for your TV screen",
            style = MaterialTheme.typography.bodyMedium,
            color = subtleColor
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(WallpaperTheme.values()) { theme ->
                val isSelected = theme == currentTheme
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .tvFocusable(
                            shape = RoundedCornerShape(16.dp),
                            scaleFactor = 1.03f,
                            focusedBorderColor = theme.primaryAccent,
                            onClick = { onSelectTheme(theme) }
                        )
                        .background(
                            if (isSelected) Color(0x3DFFFFFF) else Color(0x1FFFFFFF),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Preview color circle
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(theme.primaryAccent, CircleShape)
                            .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Active",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = theme.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = textColor
                        )
                        Text(
                            text = theme.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = subtleColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PreferencesSettingsTab(
    currentTheme: WallpaperTheme,
    is24Hour: Boolean,
    onToggle24Hour: (Boolean) -> Unit,
    onResetFavorites: () -> Unit
) {
    val textColor = if (currentTheme.isDark) Color.White else Color(0xFF1E222A)
    val subtleColor = if (currentTheme.isDark) Color(0x99FFFFFF) else Color(0x991E222A)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Clock & Launcher Preferences",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = textColor
        )

        // 24-Hour Time Format Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .tvFocusable(
                    shape = RoundedCornerShape(14.dp),
                    focusedBorderColor = currentTheme.primaryAccent,
                    onClick = { onToggle24Hour(!is24Hour) }
                )
                .background(Color(0x1FFFFFFF), RoundedCornerShape(14.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "24-Hour Time Format",
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor
                )
                Text(
                    text = if (is24Hour) "Using 24-hour clock (e.g., 20:45)" else "Using 12-hour clock (e.g., 8:45 PM)",
                    style = MaterialTheme.typography.bodySmall,
                    color = subtleColor
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (is24Hour) currentTheme.primaryAccent else Color(0x33FFFFFF))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (is24Hour) "24H ON" else "12H ON",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (is24Hour) Color.Black else textColor
                )
            }
        }

        // Reset Favorites
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .tvFocusable(
                    shape = RoundedCornerShape(14.dp),
                    focusedBorderColor = currentTheme.primaryAccent,
                    onClick = onResetFavorites
                )
                .background(Color(0x1FFFFFFF), RoundedCornerShape(14.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Reset Favorite Apps",
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor
                )
                Text(
                    text = "Clear all pinned shortcuts from the Favorites row",
                    style = MaterialTheme.typography.bodySmall,
                    color = subtleColor
                )
            }

            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Reset",
                tint = currentTheme.primaryAccent
            )
        }
    }
}

@Composable
private fun HiddenAppsSettingsTab(
    currentTheme: WallpaperTheme,
    hiddenApps: List<AppInfo>,
    onUnhideApp: (AppInfo) -> Unit,
    onUnhideAll: () -> Unit
) {
    val textColor = if (currentTheme.isDark) Color.White else Color(0xFF1E222A)
    val subtleColor = if (currentTheme.isDark) Color(0x99FFFFFF) else Color(0x991E222A)

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Hidden Applications",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = textColor
                )
                Text(
                    text = "${hiddenApps.size} apps hidden from launcher drawer",
                    style = MaterialTheme.typography.bodySmall,
                    color = subtleColor
                )
            }

            if (hiddenApps.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .tvFocusable(
                            shape = RoundedCornerShape(12.dp),
                            focusedBorderColor = currentTheme.primaryAccent,
                            onClick = onUnhideAll
                        )
                        .background(Color(0x33FFFFFF), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Unhide All",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = currentTheme.primaryAccent
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (hiddenApps.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No apps are hidden. You can hide any app by pressing Menu on its icon.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = subtleColor
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(hiddenApps, key = { it.componentKey }) { app ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .tvFocusable(
                                shape = RoundedCornerShape(12.dp),
                                focusedBorderColor = currentTheme.primaryAccent,
                                onClick = { onUnhideApp(app) }
                            )
                            .background(Color(0x1AFFFFFF), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = app.label,
                            style = MaterialTheme.typography.bodyLarge,
                            color = textColor
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = "Unhide",
                                tint = currentTheme.primaryAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Unhide",
                                style = MaterialTheme.typography.labelSmall,
                                color = currentTheme.primaryAccent
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AboutSettingsTab(
    currentTheme: WallpaperTheme
) {
    val textColor = if (currentTheme.isDark) Color.White else Color(0xFF1E222A)
    val subtleColor = if (currentTheme.isDark) Color(0x99FFFFFF) else Color(0x991E222A)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "About Zoe TV Launcher",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = textColor
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x1FFFFFFF), RoundedCornerShape(16.dp))
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            InfoRow("Launcher Version", "1.0.0 (Release)", textColor, subtleColor)
            InfoRow("Target Platform", "Android TV & Google TV", textColor, subtleColor)
            InfoRow("Device Model", "${Build.MANUFACTURER} ${Build.MODEL}", textColor, subtleColor)
            InfoRow("Android Version", "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})", textColor, subtleColor)
            InfoRow("Aesthetic", "Minimalist Smart TV System-UI", textColor, subtleColor)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, textColor: Color, subtleColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = subtleColor)
        Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = textColor)
    }
}
