package com.ovasta.sellers.presentation.profile.profile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ovasta.sellers.base.*
import com.ovasta.sellers.base.components.sharedComposable.BaseDialog
import com.ovasta.sellers.data.User
import com.ovasta.sellers.presentation.profile.profile.presentation.ProfileScreenActions
import com.ovasta.sellers.presentation.profile.profile.presentation.ProfileViewState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    viewState: ProfileViewState,
    onAction: (ProfileScreenActions) -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CenteredTextAppBar(
                title = "profile",
                showBackButton = false,
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color.Black
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Gray100)
                .padding(paddingValues)
                .padding(horizontal = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "profile_image",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                tint = Gray600
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = viewState.userInfo?.name ?: "",
                style = smNormal,
                color = Gray600,
                textAlign = TextAlign.Center
            )
            Text(
                text = viewState.userInfo?.mobile ?: "",
                style = smNormal,
                color = Gray600,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            ProfileInfoCard(
                title = "wallet",
                value = "${viewState.walletBalance} EGP",
                onClick = { onAction(ProfileScreenActions.OnWalletClicked) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileInfoCard(
                title = "last_orders",
                onClick = { onAction(ProfileScreenActions.OnOrderHistoryTabClicked) }
            )
        }
    }

    if (showLogoutDialog) {
        BaseDialog(
            title = "logout",
            message = "logout_message",
            primaryButtonText = "yes",
            secondaryButtonText = "no",
            onPrimaryClick = {
                showLogoutDialog = false
                onAction(ProfileScreenActions.OnLogout)
            },
            onSecondaryClick = { showLogoutDialog = false },
            dismissOnClickOutside = false
        )
    }
}

@Composable
fun ProfileInfoCard(
    title: String,
    value: String? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = smMedium,
                color = Gray600
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value ?: "",
                    style = smMedium,
                    color = Primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Gray600
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileContentPreview() {
    ProfileContent(
        viewState = ProfileViewState(
            userInfo = User(
                name = "John Doe",
                mobile = "+1234567890",
                email = "", districtId = 1, deliveryId = 1, id = 1, userTypeId = 1, token = null
            ),
            walletBalance = 250.0,
            points = 1200.0
        ),
        onAction = {}
    )
}
