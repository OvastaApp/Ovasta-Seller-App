package com.ovasta.sellers.presentation.auth.splash

sealed interface SplashAction {
    object NextScreen : SplashAction
}