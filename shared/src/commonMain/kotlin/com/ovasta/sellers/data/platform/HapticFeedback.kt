package com.ovasta.sellers.data.platform

/**
 * Platform-specific haptic feedback.
 * Android: HapticFeedbackConstants
 * iOS: UIImpactFeedbackGenerator
 */
expect class HapticFeedback {
    fun performHaptic()
}
