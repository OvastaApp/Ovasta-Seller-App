package com.ovasta.sellers.ui.platform

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private object AndroidPlatform : KoinComponent {
    val context: Context by inject()
}

actual fun openDialer(phoneNumber: String) {
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber")).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    AndroidPlatform.context.startActivity(intent)
}

actual fun openWhatsApp(phoneNumber: String) {
    val url = "https://wa.me/${phoneNumber.replace("+", "")}"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    AndroidPlatform.context.startActivity(intent)
}

actual fun copyToClipboard(text: String) {
    val clipboard = AndroidPlatform.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Copied", text)
    clipboard.setPrimaryClip(clip)
}

actual fun showToast(message: String) {
    Toast.makeText(AndroidPlatform.context, message, Toast.LENGTH_SHORT).show()
}
