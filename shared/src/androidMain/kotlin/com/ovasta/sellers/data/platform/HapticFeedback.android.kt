package com.ovasta.sellers.data.platform

import android.content.Context
import android.view.HapticFeedbackConstants
import android.view.View

actual class HapticFeedback(private val context: Context) {
    actual fun performHaptic() {
        // Find the root view and perform haptic feedback
        // Note: In real usage, this would be called from a Composable with LocalView.current
        // For now, this is a placeholder that can be improved when wiring to UI
        try {
            val view = View(context)
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        } catch (e: Exception) {
            // Haptic feedback not critical, silently fail
        }
    }
}
