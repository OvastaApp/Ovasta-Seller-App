package com.ovasta.sellers.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import com.ovasta.sellers.ui.theme.Gray100
import com.ovasta.sellers.ui.theme.Gray200
import com.ovasta.sellers.ui.theme.Gray300

/**
 * Animated shimmer placeholder background. Apply to a sized, clipped element
 * (e.g. `Modifier.size(...).clip(shape).shimmer()`) to show a loading skeleton.
 */
fun Modifier.shimmer(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translate by transition.animateFloat(
        initialValue = -2 * SHIMMER_WIDTH,
        targetValue = 2 * SHIMMER_WIDTH,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val brush = Brush.linearGradient(
        colors = listOf(Gray200, Gray100, Gray300, Gray100, Gray200),
        start = Offset(translate, translate),
        end = Offset(translate + SHIMMER_WIDTH, translate + SHIMMER_WIDTH)
    )

    background(brush)
}

private const val SHIMMER_WIDTH = 320f
