package com.ovasta.sellers.ui.platform

import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIPasteboard

actual fun openDialer(phoneNumber: String) {
    val url = NSURL(string = "tel:$phoneNumber")
    UIApplication.sharedApplication.openURL(url)
}

actual fun openWhatsApp(phoneNumber: String) {
    val cleanNumber = phoneNumber.replace("+", "")
    val url = NSURL(string = "https://wa.me/$cleanNumber")
    UIApplication.sharedApplication.openURL(url)
}

actual fun copyToClipboard(text: String) {
    UIPasteboard.generalPasteboard.string = text
}
