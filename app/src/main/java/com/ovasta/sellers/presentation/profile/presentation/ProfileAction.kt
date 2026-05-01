package com.ovasta.sellers.presentation.profile.presentation


sealed interface ProfileScreenActions {
    data class ChangeLogoutDialogStatus(val isVisible: Boolean) : ProfileScreenActions
    data object OnWalletClicked : ProfileScreenActions
    data object OnLastOrdersClicked : ProfileScreenActions
    data object OnLogout : ProfileScreenActions
    data class OnNoteChanged(val note: String) : ProfileScreenActions
    object RefreshOrders : ProfileScreenActions


}