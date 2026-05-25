package com.ovasta.sellers.base

sealed class ScreenDirection {
    data class Push(val screen: Any) : ScreenDirection()
    object Pop : ScreenDirection()
    data class Replace(val screen: Any) : ScreenDirection()
    data class ReplaceAll(val screen: Any) : ScreenDirection()
}
