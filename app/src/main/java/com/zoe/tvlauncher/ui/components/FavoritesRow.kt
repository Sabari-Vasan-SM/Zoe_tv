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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zoe.tvlauncher.data.model.AppInfo
import com.zoe.tvlauncher.data.model.WallpaperTheme

@Composable
fun FavoritesRow(
    favoriteApps: List<AppInfo>,
    wallpaperTheme: WallpaperTheme,
    onAppClick: (AppInfo) -> Unit,
    onAppLongClick: (AppInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    val textColor = if (wallpaperTheme.isDark) Color.White else Color(0xFF1E222A)
    val subtleColor = if (wallpaperTheme.isDark) Color(0x99FFFFFF) else Color(0x991E222A)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        // Row Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = wallpaperTheme.primaryAccent,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "FAVORITES",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    fontSize = 13.sp
                ),
                color = textColor
            )
            if (favoriteApps.isNotEmpty()) {
                Text(
                    text = "•  ${favoriteApps.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = subtleColor
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (favoriteApps.isEmpty()) {
            // Elegant hint for user
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp, vertical = 8.dp)
                    .background(
                        if (wallpaperTheme.isDark) Color(0x1AFFFFFF) else Color(0x0F000000),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "No pinned apps yet. Press Menu or hold OK on any app below to pin it here.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = subtleColor
                )
            }
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 48.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(favoriteApps, key = { it.componentKey }) { app ->
                    AppCard(
                        appInfo = app,
                        wallpaperTheme = wallpaperTheme,
                        onClick = { onAppClick(app) },
                        onLongClick = { onAppLongClick(app) }
                    )
                }
            }
        }
    }
}
