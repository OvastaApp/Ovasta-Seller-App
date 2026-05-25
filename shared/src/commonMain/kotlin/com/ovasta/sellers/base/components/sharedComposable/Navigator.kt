package com.ovasta.sellers.base.components.sharedComposable

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.snapshots.SnapshotStateList

val LocalNavigator = compositionLocalOf<Navigator> {
    error("Navigator not provided")
}

class Navigator(
    private val backStack: SnapshotStateList<Any>
) {
    fun push(screen: Any) {
        backStack.add(screen)
    }

    fun replace(screen: Any) {
        backStack.removeLastOrNull()
        backStack.add(screen)
    }

    fun pop() {
        backStack.removeLastOrNull()
    }

    fun replaceAll(screen: Any) {
        backStack.clear()
        backStack.add(screen)
    }
}
