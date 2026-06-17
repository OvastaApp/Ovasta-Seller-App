package com.ovasta.sellers.data.platform

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNAuthorizationStatusDenied
import platform.UserNotifications.UNAuthorizationStatusNotDetermined
import platform.UserNotifications.UNAuthorizationStatusProvisional
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalForeignApi::class)
actual class PermissionManager {

    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()

    actual suspend fun requestNotificationPermission(): Boolean = suspendCoroutine { continuation ->
        notificationCenter.getNotificationSettingsWithCompletionHandler { settings ->
            val status = settings?.authorizationStatus
            when (status) {
                UNAuthorizationStatusAuthorized,
                UNAuthorizationStatusProvisional -> {
                    UIApplication.sharedApplication.registerForRemoteNotifications()
                    continuation.resume(true)
                }
                UNAuthorizationStatusDenied -> {
                    continuation.resume(false)
                }
                UNAuthorizationStatusNotDetermined -> {
                    val options = UNAuthorizationOptionAlert or
                                 UNAuthorizationOptionSound or
                                 UNAuthorizationOptionBadge

                    notificationCenter.requestAuthorizationWithOptions(
                        options = options
                    ) { granted, error ->
                        if (error != null) {
                            println("PermissionManager: error= ${error.localizedDescription}")
                            continuation.resume(false)
                        } else {
                            if (granted) {
                                UIApplication.sharedApplication.registerForRemoteNotifications()
                            }
                            continuation.resume(granted)
                        }
                    }
                }
                else -> {
                    continuation.resume(false)
                }
            }
        }
    }

    actual suspend fun isNotificationPermissionGranted(): Boolean = suspendCoroutine { continuation ->
        notificationCenter.getNotificationSettingsWithCompletionHandler { settings ->
            val isGranted = settings?.authorizationStatus == UNAuthorizationStatusAuthorized ||
                           settings?.authorizationStatus == UNAuthorizationStatusProvisional
            continuation.resume(isGranted)
        }
    }

    actual fun openAppSettings() {
        val settingsUrl = NSURL.URLWithString(UIApplicationOpenSettingsURLString)
        settingsUrl?.let {
            if (UIApplication.sharedApplication.canOpenURL(it)) {
                UIApplication.sharedApplication.openURL(it)
            }
        }
    }
}
