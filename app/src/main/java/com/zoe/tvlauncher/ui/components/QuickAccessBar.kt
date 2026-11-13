package com.zoe.tvlauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zoe.tvlauncher.data.model.WallpaperTheme

data class QuickAccessItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun QuickAccessBar(
    wallpaperTheme: WallpaperTheme,
    onOpenSettings: () -> Unit,
    onOpenWifi: () -> Unit,
    onOpenBluetooth: () -> Unit,
    onOpenDisplay: () -> Unit,
    onOpenSound: () -> Unit,
    onOpenFiles: () -> Unit,
    onOpenThemeSettings: () -> Unit,
    onOpenHiddenApps: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        QuickAccessItem("settings", "System Settings", Icons.Default.Settings, onOpenSettings),
        QuickAccessItem("wifi", "Network", Icons.Default.Wifi, onOpenWifi),
        QuickAccessItem("bluetooth", "Bluetooth", Icons.Default.Bluetooth, onOpenBluetooth),
        QuickAccessItem("display", "Display", Icons.Default.Tv, onOpenDisplay),
        QuickAccessItem("sound", "Audio", Icons.Default.VolumeUp, onOpenSound),
        QuickAccessItem("files", "Files", Icons.Default.Folder, onOpenFiles),
        QuickAccessItem("appearance", "Themes", Icons.Default.Palette, onOpenThemeSettings),
        QuickAccessItem("hidden", "Hidden Apps", Icons.Default.VisibilityOff, onOpenHiddenApps)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(items, key = { it.id }) { item ->
                QuickAccessPill(
                    item = item,
                    wallpaperTheme = wallpaperTheme
                )
            }
        }
    }
}

@Composable
private fun QuickAccessPill(
    item: QuickAccessItem,
    wallpaperTheme: WallpaperTheme
) {
    val textColor = if (wallpaperTheme.isDark) Color(0xEEFFFFFF) else Color(0xEE1E222A)
    val pillBg = if (wallpaperTheme.isDark) Color(0x24FFFFFF) else Color(0x1A000000)

    Row(
        modifier = Modifier
            .tvFocusable(
                shape = RoundedCornerShape(24.dp),
                scaleFactor = 1.08f,
                focusedBorderColor = wallpaperTheme.primaryAccent,
                onClick = item.onClick
            )
            .background(pillBg, RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            tint = wallpaperTheme.primaryAccent,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.2.sp
            ),
            color = textColor
        )
    }
}
