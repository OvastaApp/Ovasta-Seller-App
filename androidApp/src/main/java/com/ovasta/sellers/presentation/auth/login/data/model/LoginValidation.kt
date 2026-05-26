package com.ovasta.sellers.presentation.auth.login.data.model

sealed class LoginValidation {
    object InValidMobile : LoginValidation()
    class InValidCredential(val message: String) : LoginValidation()
}