package com.ovasta.sellers.presentation.auth.login.presentation

import com.ovasta.sellers.base.UserType
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ovasta.sellers.base.Base_white
import com.ovasta.sellers.base.Gray100
import com.ovasta.sellers.base.Gray200
import com.ovasta.sellers.base.Gray800
import com.ovasta.sellers.base.Primary
import com.ovasta.sellers.base.components.sharedComposable.BaseScreen
import com.ovasta.sellers.base.mdMedium
import com.ovasta.sellers.base.mdRegular
import com.ovasta.sellers.base.smMedium
import com.ovasta.sellers.presentation.auth.login.presentation.components.UserTypeOption

object LoginContentTestTag {

    private const val PREFIX = "LoginContentTestTag"
    const val ROOT = "$PREFIX.ROOT"
    const val BTN_LOGIN = "$PREFIX.BTN_LOHIN"
    const val TXT_INVALID_PHONE = "$PREFIX.TXT_INVALID_PHONE"
    const val TXT_INVALID_PASSWORD = "$PREFIX.TXT_INVALID_PASSWORD"
    const val TF_PHONE = "$PREFIX.TF_PHONE"
    const val TF_PASSWORD = "$PREFIX.TF_PASSWORD"
    const val IC_PASSWORD_VISIBILITY = "$PREFIX.IC_PASSWORD_VISIBILITY"
    const val BTN_DELIVERY_AGENT = "$PREFIX.BTN_DELIVERY_AGENT"
    const val BTN_PICKER = "$PREFIX.BTN_PICKER"
}

@Composable
fun LoginScreen(
    viewModel: LoginViewModel
) {
    val viewState by viewModel.viewState.collectAsState()
    BaseScreen(viewModel = viewModel) {
        LoginContent(
            viewState = viewState,
            onAction = viewModel::onAction
        )
    }
    LaunchedEffect(Unit) {
        viewModel.onAction(LoginAction.ResetState)
    }
}

@Composable
private fun LoginContent(
    viewState: LoginViewState,
    onAction: (LoginAction) -> Unit,
) {
    val keyboard = LocalSoftwareKeyboardController.current
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Gray100)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Text(
                modifier = Modifier.padding(top = 24.dp),
                text = "placeholder_text",
                style = smMedium
            )

            TextField(
                modifier = Modifier
                    .testTag(LoginContentTestTag.TF_PHONE)
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        width = 1.dp,
                        if (viewState.isPhoneValid) Gray200 else Primary,
                        shape = RoundedCornerShape(8.dp)
                    ),
                value = viewState.phoneNumber,
                onValueChange = { input ->
                    if (input.length <= 11) {
                        onAction(LoginAction.PhoneNumberChanged(input))
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Base_white,
                    unfocusedContainerColor = Base_white,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    cursorColor = Color.Black
                ),
                placeholder = {
                    Text(
                        text = "01xxxxxxxxx",
                        style = mdRegular
                    )
                },
                maxLines = 1
            )

            if (!viewState.isPhoneValid) {
                Text(
                    text = "placeholder_text",
                    color = Primary,
                    modifier = Modifier
                        .testTag(LoginContentTestTag.TXT_INVALID_PHONE)
                        .padding(top = 4.dp)
                )
            }

            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = "placeholder_text",
                style = smMedium
            )

            TextField(
                modifier = Modifier
                    .testTag(LoginContentTestTag.TF_PASSWORD)
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        width = 1.dp,
                        if (viewState.isPasswordValid) Gray200 else Primary,
                        shape = RoundedCornerShape(8.dp)
                    ),
                value = viewState.password,
                onValueChange = { input ->
                    if (input.length <= 20) {
                        onAction(LoginAction.PasswordChanged(input))
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Base_white,
                    unfocusedContainerColor = Base_white,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    cursorColor = Color.Black
                ),
                placeholder = {
                    Text(
                        text = "placeholder_text",
                        style = mdRegular
                    )
                },
                trailingIcon = {
                },
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboard?.hide()
                        if (viewState.isLoginButtonEnabled) {
                            onAction(LoginAction.Login)
                        }
                    }
                ),
                maxLines = 1
            )

            if (!viewState.isPasswordValid) {
                Text(
                    text = "placeholder_text",
                    color = Primary,
                    modifier = Modifier
                        .testTag(LoginContentTestTag.TXT_INVALID_PASSWORD)
                        .padding(top = 4.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Base_white)
                .border(
                    width = 1.dp,
                    color = Gray200
                )
                .padding(
                    end = 16.dp,
                    start = 16.dp,
                    top = 8.dp,
                    bottom = 8.dp
                ),
            contentAlignment = Alignment.Center,
        ) {

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(LoginContentTestTag.BTN_LOGIN),
                onClick = { onAction(LoginAction.Login) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary
                ),
                enabled = viewState.isLoginButtonEnabled
            ) {
                Text(
                    text = "placeholder_text",
                    style = mdMedium.copy(color = Base_white)
                )
            }
        }

    }
}

@Composable
@Preview(showBackground = true)
private fun LoginContentPreview() {
    LoginContent(
        LoginViewState(
            phoneNumber = "123456789",
            password = "password123",
            isPhoneValid = true,
            isPasswordValid = true,
            selectedUserType = UserType.COURIER
        ),
        {}
    )
}
