package com.zoe.tvlauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zoe.tvlauncher.data.manager.NetworkType
import com.zoe.tvlauncher.data.manager.SystemStatus
import com.zoe.tvlauncher.data.model.WallpaperTheme

@Composable
fun TopStatusBar(
    systemStatus: SystemStatus,
    wallpaperTheme: WallpaperTheme,
    onOpenSettings: () -> Unit,
    onRefreshApps: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textColor = if (wallpaperTheme.isDark) Color.White else Color(0xFF1E222A)
    val subtleColor = if (wallpaperTheme.isDark) Color(0x99FFFFFF) else Color(0x991E222A)
    val pillBg = if (wallpaperTheme.isDark) Color(0x2BFFFFFF) else Color(0x1F000000)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Prominent, elegant Clock and Date
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = systemStatus.timeFormatted.ifEmpty { "12:00" },
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = (-1).sp
                ),
                color = textColor
            )

            if (systemStatus.timePeriod.isNotEmpty()) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = systemStatus.timePeriod,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = wallpaperTheme.primaryAccent,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            Spacer(modifier = Modifier.width(24.dp))

            // Date and Day
            Column(
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = systemStatus.dayOfWeek.ifEmpty { "Friday" },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = textColor
                )
                Text(
                    text = systemStatus.dateFormatted.ifEmpty { "November 13, 2026" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = subtleColor
                )
            }
        }

        // Right: System status pills (Network, Battery, Actions)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Network indicator pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(pillBg)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (systemStatus.networkType) {
                    NetworkType.ETHERNET -> {
                        Icon(
                            imageVector = Icons.Default.Lan,
                            contentDescription = "Ethernet",
                            tint = wallpaperTheme.primaryAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Ethernet",
                            style = MaterialTheme.typography.labelSmall,
                            color = textColor
                        )
                    }
                    NetworkType.WIFI -> {
                        val wifiIcon = if (systemStatus.isNetworkConnected) {
                            Icons.Default.Wifi
                        } else {
                            Icons.Default.WifiOff
                        }
                        Icon(
                            imageVector = wifiIcon,
                            contentDescription = "Wi-Fi",
                            tint = if (systemStatus.isNetworkConnected) wallpaperTheme.primaryAccent else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                        val ssidLabel = systemStatus.wifiSsid ?: if (systemStatus.isNetworkConnected) "Connected" else "Offline"
                        Text(
                            text = ssidLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = textColor
                        )
                    }
                    else -> {
                        Icon(
                            imageVector = Icons.Default.WifiOff,
                            contentDescription = "Offline",
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Offline",
                            style = MaterialTheme.typography.labelSmall,
                            color = subtleColor
                        )
                    }
                }
            }

            // Battery indicator pill (if available on device)
            if (systemStatus.hasBattery) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(pillBg)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val batteryIcon = when {
                        systemStatus.isCharging -> Icons.Default.BatteryChargingFull
                        systemStatus.batteryPercent <= 15 -> Icons.Default.BatteryAlert
                        else -> Icons.Default.BatteryFull
                    }
                    val batteryColor = when {
                        systemStatus.isCharging -> Color(0xFF00E676)
                        systemStatus.batteryPercent <= 15 -> Color(0xFFFF5252)
                        else -> textColor
                    }
                    Icon(
                        imageVector = batteryIcon,
                        contentDescription = "Battery",
                        tint = batteryColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "${systemStatus.batteryPercent}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor
                    )
                }
            }

            // Quick Refresh Button
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .tvFocusable(
                        shape = CircleShape,
                        focusedBorderColor = wallpaperTheme.primaryAccent,
                        onClick = onRefreshApps
                    )
                    .background(pillBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh Apps",
                    tint = textColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Settings Button
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .tvFocusable(
                        shape = CircleShape,
                        focusedBorderColor = wallpaperTheme.primaryAccent,
                        onClick = onOpenSettings
                    )
                    .background(pillBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Launcher Settings",
                    tint = textColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
