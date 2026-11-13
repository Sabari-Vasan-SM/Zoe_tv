package com.zoe.tvlauncher.data.model

import android.graphics.Bitmap

data class AppInfo(
    val packageName: String,
    val activityName: String,
    val label: String,
    val isFavorite: Boolean = false,
    val isHidden: Boolean = false,
    val isSystemApp: Boolean = false,
    val installTime: Long = 0L,
    val iconBitmap: Bitmap? = null
) {
    val componentKey: String
        get() = "$packageName/$activityName"
}
