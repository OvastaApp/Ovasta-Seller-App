package com.ovasta.sellers.ui.base

sealed class ScreenDirection {
    data class Push(val screen: Any) : ScreenDirection()
    data object Pop : ScreenDirection()
    data class Replace(val screen: Any) : ScreenDirection()
    data class ReplaceAll(val screen: Any) : ScreenDirection()
}
