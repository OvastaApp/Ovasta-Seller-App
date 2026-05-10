package com.ovasta.sellers.presentation.auth.login.presentation


sealed interface LoginAction {
    data class PhoneNumberChanged(val phoneNumber: String) : LoginAction
    data class PasswordChanged(val password: String) : LoginAction
    object Login : LoginAction
    object ResetState : LoginAction


}