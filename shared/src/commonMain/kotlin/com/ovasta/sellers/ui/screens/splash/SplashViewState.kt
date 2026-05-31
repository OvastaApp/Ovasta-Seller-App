package com.ovasta.sellers.ui.screens.splash

data class SplashViewState(
    val isLoading: Boolean = false
)

sealed interface SplashAction {
    data object NextScreen : SplashAction
}
