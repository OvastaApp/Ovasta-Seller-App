package com.ovasta.sellers.ui.screens.profile

import com.ovasta.sellers.domain.model.HomeInfo
import com.ovasta.sellers.domain.model.User

data class ProfileViewState(
    val homeInfo: HomeInfo? = null,
    val userInfo: User? = null,
    val walletBalance: Double = 0.0,
    val points: Double = 0.0,
    val selectedLanguage: String = "ar",
)

sealed interface ProfileScreenActions {
    data class ChangeLogoutDialogStatus(val isVisible: Boolean) : ProfileScreenActions
    data object OnWalletClicked : ProfileScreenActions
    data object OnOrderHistoryTabClicked : ProfileScreenActions
    data object OnMyProductsClicked : ProfileScreenActions
    data object OnLogout : ProfileScreenActions
    data class OnLanguageSelected(val languageCode: String) : ProfileScreenActions
}
