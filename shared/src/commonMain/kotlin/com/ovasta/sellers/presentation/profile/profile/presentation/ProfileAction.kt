package com.ovasta.sellers.presentation.profile.profile.presentation

sealed interface ProfileScreenActions {
    data object OnWalletClicked : ProfileScreenActions
    data object OnOrderHistoryTabClicked : ProfileScreenActions
    data object OnLogout : ProfileScreenActions
}
