package com.ovasta.sellers.platform

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyle
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.Foundation.NSURL
import platform.Foundation.NSBundle
import platform.UIKit.UIScreen
import platform.CoreLocation.CLGeocoder
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

actual fun showPlatformToast(message: String) {
    val alert = UIAlertController.alertControllerWithTitle(
        title = null,
        message = message,
        preferredStyle = UIAlertControllerStyle.UIAlertControllerStyleAlert
    )
    alert.addAction(
        platform.UIKit.UIAlertAction.actionWithTitle(
            title = "OK",
            style = platform.UIKit.UIAlertActionStyle.UIAlertActionStyleDefault,
            handler = null
        )
    )
    val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
    rootViewController?.presentViewController(alert, animated = true, completion = null)
}

actual fun getPlatformContext(): PlatformContext {
    return PlatformContext()
}

actual fun sdp(value: Int): Dp {
    val screenWidth = UIScreen.mainScreen.bounds.size.width
    val designWidth = 375.0
    return (value.toDouble() * screenWidth / designWidth).dp
}

actual fun ssp(value: Int): TextUnit {
    val screenWidth = UIScreen.mainScreen.bounds.size.width
    val designWidth = 375.0
    return (value.toDouble() * screenWidth / designWidth).sp
}

actual fun openPhoneDialer(phoneNumber: String) {
    val url = NSURL.URLWithString("tel:$phoneNumber") ?: return
    UIApplication.sharedApplication.openURL(url)
}

actual fun openMapNavigation(latitude: Double, longitude: Double) {
    val url = NSURL.URLWithString("http://maps.apple.com/?q=$latitude,$longitude") ?: return
    UIApplication.sharedApplication.openURL(url)
}

actual fun openWhatsApp(phoneNumber: String) {
    val cleaned = phoneNumber.replace(Regex("[^\\d]"), "")
    val url = NSURL.URLWithString("https://wa.me/$cleaned") ?: return
    UIApplication.sharedApplication.openURL(url)
}

actual fun vibrateDevice() {
    // iOS haptic feedback via AudioServices or CHHapticEngine
    platform.UIKit.UIImpactFeedbackGenerator(
        style = platform.UIKit.UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium
    ).impactOccurred()
}

actual fun geocodeAddress(address: String, callback: (Double?, Double?) -> Unit) {
    val geocoder = CLGeocoder()
    geocoder.geocodeAddressString(address) { placemarks, error ->
        if (error == null && placemarks?.isNotEmpty() == true) {
            val location = placemarks!!.first()!!.location
            callback(location?.coordinate?.latitude, location?.coordinate?.longitude)
        } else {
            callback(null, null)
        }
    }
}
