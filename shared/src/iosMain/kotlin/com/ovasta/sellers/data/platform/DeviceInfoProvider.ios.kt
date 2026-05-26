package com.ovasta.sellers.data.platform

import platform.Foundation.NSUserDefaults
import platform.Foundation.NSUUID
import platform.UIKit.UIDevice

actual class DeviceInfoProvider {
    private val userDefaults = NSUserDefaults.standardUserDefaults
    private val deviceIdKey = "device_id"
    
    actual fun getDeviceId(): String {
        val savedId = userDefaults.stringForKey(deviceIdKey)
        return if (savedId != null && savedId.isNotEmpty()) {
            savedId
        } else {
            generateDeviceId().also { newId ->
                userDefaults.setObject(newId, forKey = deviceIdKey)
                userDefaults.synchronize()
            }
        }
    }
    
    actual fun generateDeviceId(): String {
        // Try to use identifierForVendor (persists until app uninstall)
        val vendorId = UIDevice.currentDevice.identifierForVendor?.UUIDString
        return vendorId ?: NSUUID().UUIDString()
    }
}
