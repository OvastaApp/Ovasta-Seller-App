package com.ovasta.sellers.data.platform

import android.content.Context
import java.util.UUID

actual class DeviceInfoProvider(private val context: Context) {
    actual fun getDeviceId(): String {
        // First check if we have a stored device ID
        val prefs = context.getSharedPreferences("device_prefs", Context.MODE_PRIVATE)
        var deviceId = prefs.getString("device_id", null)
        
        if (deviceId == null) {
            deviceId = generateDeviceId()
            prefs.edit().putString("device_id", deviceId).apply()
        }
        
        return deviceId
    }

    actual fun generateDeviceId(): String = UUID.randomUUID().toString()
}
