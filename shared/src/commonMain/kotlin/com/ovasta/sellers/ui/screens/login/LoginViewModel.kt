package com.ovasta.sellers.ui.screens.login

import androidx.lifecycle.viewModelScope
import com.ovasta.sellers.domain.repository.ILoginRepository
import com.ovasta.sellers.domain.repository.ISettingsRepository
import com.ovasta.sellers.ui.base.BaseViewModel
import com.ovasta.sellers.ui.base.ScreenDirection
import com.ovasta.sellers.ui.screens.Home
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginRepository: ILoginRepository,
    private val settingsRepository: ISettingsRepository,
) : BaseViewModel() {

    private val _viewState = MutableStateFlow(LoginViewState())
    val viewState = _viewState.asStateFlow()

    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.PhoneNumberChanged -> onPhoneNumberChanged(action.phoneNumber)
            is LoginAction.PasswordChanged -> onPasswordChanged(action.password)
            is LoginAction.Login -> with(viewState.value) {
                login(phoneNumber, password, selectedUserType)
            }
            is LoginAction.ResetState -> _viewState.value = LoginViewState()
        }
    }

    fun updateViewState(update: (LoginViewState) -> LoginViewState) {
        _viewState.update(update)
    }

    private fun onPhoneNumberChanged(phoneNumber: String) {
        val isValid = isValidMobile(phoneNumber)
        _viewState.update { it.copy(phoneNumber = phoneNumber, isPhoneValid = isValid) }
        checkBtnEnabled()
    }

    private fun onPasswordChanged(password: String) {
        val isValid = isValidPassword(password)
        _viewState.update { it.copy(password = password, isPasswordValid = isValid) }
        checkBtnEnabled()
    }

    private fun checkBtnEnabled() {
        val state = viewState.value
        _viewState.update {
            it.copy(isLoginButtonEnabled = isValidMobile(state.phoneNumber) && isValidPassword(state.password))
        }
    }

    private fun login(phone: String, password: String, userType: UserType) {
        val handler = CoroutineExceptionHandler { _, throwable ->
            setLoading(false)
            handleError(throwable)
        }

        viewModelScope.launch(dispatcher + handler) {
            setLoading(true)
            val fcmToken = try {
                settingsRepository.getFcmToken().takeIf { it.isNotEmpty() }
            } catch (_: Exception) { null }
            runCatching {
                loginRepository.login(phone, password, userType.typeId, fcmToken)
            }.onSuccess { response ->
                val user = response.data
                if (user == null) {
                    setLoading(false)
                    handleError(IllegalStateException("Login response data is null"))
                    return@launch
                }
                user.token = response.token
                settingsRepository.saveUserData(user)
                setLoading(false)
                emitScreenDirection(ScreenDirection.Replace(Home))
            }.onFailure {
                setLoading(false)
                handleError(it)
            }
        }
    }

    private fun isValidMobile(mobile: String): Boolean =
        mobile.isNotEmpty() && mobile.length == 11

    private fun isValidPassword(password: String): Boolean =
        password.isNotEmpty() && password.length >= 4
}
