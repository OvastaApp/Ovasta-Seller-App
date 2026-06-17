package com.ovasta.sellers.ui.platform

/**
 * Platform-specific actions that need native implementations.
 */
expect fun openDialer(phoneNumber: String)
expect fun openWhatsApp(phoneNumber: String)
expect fun copyToClipboard(text: String)
expect fun showToast(message: String)
