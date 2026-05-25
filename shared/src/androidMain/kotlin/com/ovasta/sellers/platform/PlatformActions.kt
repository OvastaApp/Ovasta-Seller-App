package com.ovasta.sellers.platform

import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.java.KoinJavaComponent.get
import java.util.Locale

actual fun showPlatformToast(message: String) {
    Toast.makeText(get<Context>(Context::class.java), message, Toast.LENGTH_SHORT).show()
}

actual fun getPlatformContext(): PlatformContext {
    val context = get<Context>(Context::class.java)
    return PlatformContext(context)
}

actual fun sdp(value: Int): Dp {
    return value.dp
}

actual fun ssp(value: Int): TextUnit {
    return value.sp
}

actual fun openPhoneDialer(phoneNumber: String) {
    val context = get<Context>(Context::class.java)
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$phoneNumber")
    }
    context.startActivity(intent)
}

actual fun openMapNavigation(latitude: Double, longitude: Double) {
    val context = get<Context>(Context::class.java)
    val uri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(intent)
}

actual fun openWhatsApp(phoneNumber: String) {
    val context = get<Context>(Context::class.java)
    val cleanedNumber = phoneNumber.replace(Regex("[^\\d]"), "")
    try {
        val uri = Uri.parse("https://wa.me/$cleanedNumber")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    } catch (e: Exception) {
        try {
            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$cleanedNumber")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        } catch (e: Exception) {
            showPlatformToast("WhatsApp not installed")
        }
    }
}

actual fun vibrateDevice() {
    val context = get<Context>(Context::class.java)
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        manager.defaultVibrator
    } else {
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    if (vibrator.hasVibrator()) {
        val effect = VibrationEffect.createWaveform(
            longArrayOf(0, 200, 100, 200), -1
        )
        vibrator.vibrate(effect)
    }
}

actual fun geocodeAddress(address: String, callback: (Double?, Double?) -> Unit) {
    val context = get<Context>(Context::class.java)
    try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocationName(address, 1)
        if (addresses != null && addresses.isNotEmpty()) {
            val location = addresses[0]
            callback(location.latitude, location.longitude)
        } else {
            callback(null, null)
        }
    } catch (e: Exception) {
        callback(null, null)
    }
}
