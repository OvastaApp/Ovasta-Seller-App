package com.ovasta.sellers.presentation.profile.orderhistory.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ovasta.sellers.R
import com.ovasta.sellers.base.CenteredTextAppBar
import com.ovasta.sellers.base.Gray100
import com.ovasta.sellers.base.Gray600
import com.ovasta.sellers.base.Primary
import com.ovasta.sellers.base.components.sharedComposable.BaseDialog
import com.ovasta.sellers.base.smMedium
import com.ovasta.sellers.base.smNormal
import com.ovasta.sellers.domain.model.User
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
        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CenteredTextAppBar(
                title = stringResource(R.string.profile),
                showBackButton = false,
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_logout),
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
                .padding(horizontal = dimensionResource(com.intuit.sdp.R.dimen._14sdp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._24sdp)))

            // Profile Image
            Image(
                painter = painterResource(id = R.drawable.ic_profile_circle),
                contentDescription = stringResource(R.string.profile_image),
                modifier = Modifier
                    .size(dimensionResource(com.intuit.sdp.R.dimen._80sdp))
                    .clip(CircleShape)
                    .background(Color.White)
            )

            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._12sdp)))

            // Name & Phone
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

            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._24sdp)))

            // Wallet Card
            ProfileInfoCard(
                title = stringResource(R.string.wallet),
                value = stringResource(R.string.price_currency, viewState.walletBalance),
                onClick = { onAction(ProfileScreenActions.OnWalletClicked) }
            )

            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._12sdp)))

            // order history
            ProfileInfoCard(
                title = stringResource(R.string.last_orders),
                onClick = { onAction(ProfileScreenActions.OnOrderHistoryTabClicked) }
            )

        }
    }

    // Logout confirmation dialog
    if (showLogoutDialog) {
        BaseDialog(
            title = stringResource(R.string.logout),
            message = stringResource(R.string.logout_message),
            primaryButtonText = stringResource(R.string.yes),
            secondaryButtonText = stringResource(R.string.no),
            onPrimaryClick = {
                showLogoutDialog = false
                onAction(ProfileScreenActions.OnLogout)
            },
            onSecondaryClick = { showLogoutDialog = false },
            onDismiss = { showLogoutDialog = false },
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
                .padding(dimensionResource(com.intuit.sdp.R.dimen._14sdp)),
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
