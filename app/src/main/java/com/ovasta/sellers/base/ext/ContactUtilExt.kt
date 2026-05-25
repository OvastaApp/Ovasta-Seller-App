package com.ovasta.sellers.base.ext

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.ovasta.sellers.R


fun Context.makePhoneCall(mobile: String?) {

    if (mobile.isNullOrEmpty()) {
        ToastHelper.showShortToaster(getString(R.string.not_available))
        return
    }
    val dialIntent = Intent(Intent.ACTION_DIAL)
    dialIntent.data = Uri.parse("tel:$mobile")
    startActivity(dialIntent)
}

fun Context.navigateToLocationClick(latitude: Double?, longitude: Double?) {

    if (latitude == null || longitude == null) {
        ToastHelper.showShortToaster(getString(R.string.not_available))
        return
    }

    val uriGoogleNav = Uri.parse("google.navigation:q=$latitude,$longitude&mode=d")
    val uriGeo = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")

    try {
        val navIntent = Intent(Intent.ACTION_VIEW, uriGoogleNav).apply {
            setPackage("com.google.android.apps.maps")
        }
        startActivity(navIntent)

    } catch (e: Exception) {

        try {
            val geoIntent = Intent(Intent.ACTION_VIEW, uriGeo)
            startActivity(geoIntent)

        } catch (e: Exception) {

            try {
                val webIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/maps?q=$latitude,$longitude")
                )
                startActivity(webIntent)

            } catch (e: Exception) {
                ToastHelper.showShortToaster(getString(R.string.maps_error_pkg))
            }
        }
    }
}

fun Context.openWhatsApp(phoneNumber: String?) {

    if (phoneNumber.isNullOrEmpty()) {
        ToastHelper.showShortToaster(getString(R.string.phone_number_not_available))
        return
    }

    val cleanedNumber = phoneNumber.filter { it.isDigit() }

    if (cleanedNumber.isEmpty()) {
        ToastHelper.showShortToaster(getString(R.string.phone_number_not_available))
        return
    }

    val uri = Uri.parse("https://wa.me/$cleanedNumber")

    try {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)

    } catch (e: Exception) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.whatsapp")
            }
            startActivity(intent)

        } catch (e: Exception) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                    setPackage("com.whatsapp.w4b")
                }
                startActivity(intent)

            } catch (e: Exception) {
                ToastHelper.showShortToaster(getString(R.string.whatsapp_is_not_installed_on_the_device))
            }
        }
    }
}
