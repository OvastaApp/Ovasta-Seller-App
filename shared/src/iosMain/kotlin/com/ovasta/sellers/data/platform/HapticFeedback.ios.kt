package com.ovasta.sellers.data.platform

import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle

actual class HapticFeedback {
    private val generator = UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium)
    
    actual fun performHaptic() {
        generator.prepare()
        generator.impactOccurred()
    }
}
