package com.ovasta.sellers.ui.screens.login

import com.ovasta.sellers.ui.base.AppException

enum class UserType(val typeId: Int) {
    ADMIN(1),
    CONSUMER(2),
    COURIER(3),
    WORKER(4)
}

data class LoginViewState(
    val phoneNumber: String = "",
    val password: String = "",
    val isPhoneValid: Boolean = true,
    val isPasswordValid: Boolean = true,
    val isLoginButtonEnabled: Boolean = false,
    val selectedUserType: UserType = UserType.COURIER,
    val error: AppException? = null,
)

sealed interface LoginAction {
    data class PhoneNumberChanged(val phoneNumber: String) : LoginAction
    data class PasswordChanged(val password: String) : LoginAction
    data object Login : LoginAction
    data object ResetState : LoginAction
}
