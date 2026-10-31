package com.ovasta.sellers.ui.platform

import com.ovasta.sellers.shared.resources.Res
import com.ovasta.sellers.shared.resources.phone_number_copied
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIPasteboard
import platform.UIKit.UIWindow
import platform.UIKit.UIScreen
import platform.UIKit.UILabel
import platform.UIKit.UIColor
import platform.UIKit.UIView
import platform.UIKit.UIViewAnimationOptions
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSDate
import platform.Foundation.NSThread
import platform.Foundation.dateWithTimeIntervalSinceNow
import platform.Foundation.performSelectorOnMainThread
import platform.darwin.DISPATCH_TIME_NOW
import platform.darwin.NSEC_PER_SEC
import platform.darwin.dispatch_after
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_time
import platform.UIKit.*


private fun getTopWindow(): UIWindow? {
    return UIApplication.sharedApplication.windows.lastOrNull() as? UIWindow
}

actual fun openDialer(phoneNumber: String) {
    // Map any Unicode digit (e.g. Arabic-Indic ٠١٢) to ASCII, tel: URLs only accept 0-9
    val sanitized = phoneNumber.mapNotNull { c ->
        when {
            c.isDigit() -> c.digitToInt().digitToChar()
            c == '+' -> c
            else -> null
        }
    }.joinToString("")
    if (sanitized.isEmpty()) return
    val url = NSURL.URLWithString("tel:$sanitized") ?: return
    UIApplication.sharedApplication.openURL(url, options = emptyMap<Any?, Any>()) { success ->
        // Devices that can't place calls (Simulator, iPad, Wi-Fi only) fail silently, copy the number instead
        if (!success) {
            copyToClipboard(sanitized)
            MainScope().launch { showToast(getString(Res.string.phone_number_copied)) }
        }
    }
}

actual fun openWhatsApp(phoneNumber: String) {
    val cleanNumber = phoneNumber.replace("+", "").filter { it.isDigit() }
    if (cleanNumber.isEmpty()) return
    val url = NSURL.URLWithString("https://wa.me/$cleanNumber") ?: return
    UIApplication.sharedApplication.openURL(url, options = emptyMap<Any?, Any>(), completionHandler = null)
}

actual fun copyToClipboard(text: String) {
    UIPasteboard.generalPasteboard.string = text
}

actual fun showToast(message: String) {
    val alert = UIAlertController.alertControllerWithTitle(
        title = null,
        message = message,
        preferredStyle = UIAlertControllerStyleAlert
    )

    val window = UIApplication.sharedApplication.keyWindow
    val rootViewController = window?.rootViewController

    rootViewController?.presentViewController(
        alert,
        animated = true,
        completion = null
    )

    dispatch_after(
        dispatch_time(DISPATCH_TIME_NOW, (2000).toLong()),
        dispatch_get_main_queue()
    ) {
        alert.dismissViewControllerAnimated(true, completion = null)
    }
}
