package com.ovasta.sellers.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

@Composable
expect fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit)

expect fun showPlatformToast(message: String)

expect fun getPlatformContext(): PlatformContext

expect fun sdp(value: Int): Dp

expect fun ssp(value: Int): TextUnit

expect fun openPhoneDialer(phoneNumber: String)

expect fun openMapNavigation(latitude: Double, longitude: Double)

expect fun openWhatsApp(phoneNumber: String)

expect fun vibrateDevice()

expect fun geocodeAddress(address: String, callback: (Double?, Double?) -> Unit)
