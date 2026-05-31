package com.ovasta.sellers.ui.base

import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList

@Composable
fun AppNavHost(
    backStack: SnapshotStateList<Any>,
    entryProvider: @Composable (Any) -> Unit
) {
    val current = backStack.lastOrNull()
    AnimatedContent(targetState = current) { screen ->
        if (screen != null) entryProvider(screen)
    }
}
