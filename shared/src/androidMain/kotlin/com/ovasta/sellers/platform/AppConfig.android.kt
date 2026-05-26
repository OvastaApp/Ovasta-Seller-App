package com.ovasta.sellers.platform

import android.content.pm.ApplicationInfo

actual val isDebug: Boolean by lazy {
    try {
        val ctx = Class.forName("android.app.ActivityThread")
            .getMethod("currentApplication").invoke(null) as? android.content.Context
        ctx?.applicationInfo?.flags?.and(ApplicationInfo.FLAG_DEBUGGABLE) != 0
    } catch (_: Exception) {
        false
    }
}
