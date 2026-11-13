package com.zoe.tvlauncher.ui.dialogs

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.zoe.tvlauncher.data.model.AppInfo
import com.zoe.tvlauncher.data.model.WallpaperTheme
import com.zoe.tvlauncher.ui.components.tvFocusable

@Composable
fun AppActionsDialog(
    appInfo: AppInfo,
    wallpaperTheme: WallpaperTheme,
    onLaunch: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleHide: () -> Unit,
    onAppInfo: () -> Unit,
    onUninstall: () -> Unit,
    onDismiss: () -> Unit
) {
    val dialogBg = if (wallpaperTheme.isDark) Color(0xFF14171E) else Color(0xFFF0F3F6)
    val textColor = if (wallpaperTheme.isDark) Color.White else Color(0xFF1E222A)
    val subtleColor = if (wallpaperTheme.isDark) Color(0x99FFFFFF) else Color(0x991E222A)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = dialogBg,
            shadowElevation = 16.dp,
            modifier = Modifier
                .width(420.dp)
                .clip(RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header: App icon + Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (appInfo.iconBitmap != null) {
                        Image(
                            bitmap = appInfo.iconBitmap.asImageBitmap(),
                            contentDescription = appInfo.label,
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x33FFFFFF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Android,
                                contentDescription = null,
                                tint = wallpaperTheme.primaryAccent,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = appInfo.label,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            ),
                            color = textColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = appInfo.packageName,
                            style = MaterialTheme.typography.bodySmall,
                            color = subtleColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions list
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ActionItemButton(
                        title = "Open Application",
                        icon = Icons.Default.PlayArrow,
                        accentColor = wallpaperTheme.primaryAccent,
                        wallpaperTheme = wallpaperTheme,
                        onClick = onLaunch
                    )

                    ActionItemButton(
                        title = if (appInfo.isFavorite) "Remove from Favorites" else "Pin to Favorites",
                        icon = if (appInfo.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        accentColor = wallpaperTheme.primaryAccent,
                        wallpaperTheme = wallpaperTheme,
                        onClick = {
                            onToggleFavorite()
                            onDismiss()
                        }
                    )

                    ActionItemButton(
                        title = "Hide Application",
                        icon = Icons.Default.VisibilityOff,
                        accentColor = wallpaperTheme.secondaryAccent,
                        wallpaperTheme = wallpaperTheme,
                        onClick = {
                            onToggleHide()
                            onDismiss()
                        }
                    )

                    ActionItemButton(
                        title = "App Info & Permissions",
                        icon = Icons.Default.Info,
                        accentColor = wallpaperTheme.primaryAccent,
                        wallpaperTheme = wallpaperTheme,
                        onClick = onAppInfo
                    )

                    if (!appInfo.isSystemApp) {
                        ActionItemButton(
                            title = "Uninstall",
                            icon = Icons.Default.Delete,
                            accentColor = Color(0xFFFF5252),
                            wallpaperTheme = wallpaperTheme,
                            onClick = onUninstall
                        )
                    }

                    ActionItemButton(
                        title = "Cancel",
                        icon = Icons.Default.Close,
                        accentColor = subtleColor,
                        wallpaperTheme = wallpaperTheme,
                        onClick = onDismiss
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionItemButton(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    wallpaperTheme: WallpaperTheme,
    onClick: () -> Unit
) {
    val buttonBg = if (wallpaperTheme.isDark) Color(0x24FFFFFF) else Color(0x14000000)
    val textColor = if (wallpaperTheme.isDark) Color.White else Color(0xFF1E222A)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .tvFocusable(
                shape = RoundedCornerShape(12.dp),
                scaleFactor = 1.04f,
                focusedBorderColor = accentColor,
                onClick = onClick
            )
            .background(buttonBg, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium
            ),
            color = textColor
        )
    }
}
