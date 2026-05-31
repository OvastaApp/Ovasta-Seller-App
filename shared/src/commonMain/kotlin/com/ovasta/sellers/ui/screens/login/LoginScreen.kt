package com.ovasta.sellers.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.ovasta.sellers.shared.resources.Res
import com.ovasta.sellers.shared.resources.enter_password
import com.ovasta.sellers.shared.resources.enter_your_phone_number
import com.ovasta.sellers.shared.resources.ic_visibility_off
import com.ovasta.sellers.shared.resources.ic_visibility_on
import com.ovasta.sellers.shared.resources.invalid_password
import com.ovasta.sellers.shared.resources.invalid_phone_number
import com.ovasta.sellers.shared.resources.login
import com.ovasta.sellers.shared.resources.password
import com.ovasta.sellers.ui.base.BaseScreen
import com.ovasta.sellers.ui.components.CenteredTextAppBar
import com.ovasta.sellers.ui.theme.BaseWhite
import com.ovasta.sellers.ui.theme.Gray100
import com.ovasta.sellers.ui.theme.Gray200
import com.ovasta.sellers.ui.theme.Gray800
import com.ovasta.sellers.ui.theme.Primary
import com.ovasta.sellers.ui.theme.mdMedium
import com.ovasta.sellers.ui.theme.mdRegular
import com.ovasta.sellers.ui.theme.smMedium
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginScreen(viewModel: LoginViewModel) {
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

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenteredTextAppBar(
                title = stringResource(Res.string.login),
                showBackButton = false
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Gray100)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                // Phone Number Field
                Text(
                    modifier = Modifier.padding(top = 24.dp),
                    text = stringResource(Res.string.enter_your_phone_number),
                    style = smMedium
                )

                TextField(
                    modifier = Modifier
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
                        focusedContainerColor = BaseWhite,
                        unfocusedContainerColor = BaseWhite,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = Color.Black
                    ),
                    placeholder = {
                        Text(text = "01xxxxxxxxx", style = mdRegular)
                    },
                    maxLines = 1
                )

                if (!viewState.isPhoneValid) {
                    Text(
                        text = stringResource(Res.string.invalid_phone_number),
                        color = Primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Password Field
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = stringResource(Res.string.password),
                    style = smMedium
                )

                TextField(
                    modifier = Modifier
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
                        focusedContainerColor = BaseWhite,
                        unfocusedContainerColor = BaseWhite,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = Color.Black
                    ),
                    placeholder = {
                        Text(text = stringResource(Res.string.enter_password), style = mdRegular)
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                modifier = Modifier.padding(end = 4.dp),
                                painter = painterResource(
                                    if (passwordVisible) Res.drawable.ic_visibility_on
                                    else Res.drawable.ic_visibility_off
                                ),
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = Gray800
                            )
                        }
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
                        text = stringResource(Res.string.invalid_password),
                        color = Primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Login Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .background(BaseWhite)
                    .border(width = 1.dp, color = Gray200)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onAction(LoginAction.Login) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    enabled = viewState.isLoginButtonEnabled
                ) {
                    Text(
                        text = stringResource(Res.string.login),
                        style = mdMedium,
                        color = BaseWhite
                    )
                }
            }
        }
    }
}
