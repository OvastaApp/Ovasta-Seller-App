package com.ovasta.sellers.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ovasta.sellers.shared.resources.Res
import com.ovasta.sellers.shared.resources.arrow_narrow_left
import com.ovasta.sellers.shared.resources.confirm
import com.ovasta.sellers.shared.resources.ic_logout
import com.ovasta.sellers.shared.resources.ic_profile_circle
import com.ovasta.sellers.shared.resources.language
import com.ovasta.sellers.shared.resources.language_arabic
import com.ovasta.sellers.shared.resources.language_english
import com.ovasta.sellers.shared.resources.last_orders
import com.ovasta.sellers.shared.resources.logout
import com.ovasta.sellers.shared.resources.logout_message
import com.ovasta.sellers.shared.resources.my_products
import com.ovasta.sellers.shared.resources.no
import com.ovasta.sellers.shared.resources.price_currency
import com.ovasta.sellers.shared.resources.profile
import com.ovasta.sellers.shared.resources.profile_image
import com.ovasta.sellers.shared.resources.wallet
import com.ovasta.sellers.shared.resources.yes
import com.ovasta.sellers.ui.base.BaseScreen
import com.ovasta.sellers.ui.components.BaseDialog
import com.ovasta.sellers.ui.components.CenteredTextAppBar
import com.ovasta.sellers.ui.theme.Gray100
import com.ovasta.sellers.ui.theme.Gray600
import com.ovasta.sellers.ui.theme.Primary
import com.ovasta.sellers.ui.theme.smMedium
import com.ovasta.sellers.ui.theme.smNormal
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileScreen(viewModel: ProfileViewModel) {
    val viewState by viewModel.viewState.collectAsState()

    BaseScreen(viewModel = viewModel) {
        ProfileContent(
            viewState = viewState,
            onAction = viewModel::onScreenAction
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileContent(
    viewState: ProfileViewState,
    onAction: (ProfileScreenActions) -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showLanguageSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenteredTextAppBar(
                title = stringResource(Res.string.profile),
                showBackButton = false,
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_logout),
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
                .consumeWindowInsets(paddingValues)
                .padding(paddingValues)
                .padding(horizontal = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Image(
                painter = painterResource(Res.drawable.ic_profile_circle),
                contentDescription = stringResource(Res.string.profile_image),
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White)
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
                title = stringResource(Res.string.wallet),
                value = stringResource(Res.string.price_currency, viewState.walletBalance.toString()),
                onClick = { onAction(ProfileScreenActions.OnWalletClicked) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileInfoCard(
                title = stringResource(Res.string.last_orders),
                onClick = { onAction(ProfileScreenActions.OnOrderHistoryTabClicked) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (viewState.homeInfo?.canUpdateProducts == true) {
                ProfileInfoCard(
                    title = stringResource(Res.string.my_products),
                    onClick = { onAction(ProfileScreenActions.OnMyProductsClicked) }
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            val currentLangLabel = if (viewState.selectedLanguage == "ar")
                stringResource(Res.string.language_arabic)
            else
                stringResource(Res.string.language_english)

            ProfileInfoCard(
                title = stringResource(Res.string.language),
                value = currentLangLabel,
                onClick = { showLanguageSheet = true }
            )
        }
    }

    if (showLogoutDialog) {
        BaseDialog(
            title = stringResource(Res.string.logout),
            message = stringResource(Res.string.logout_message),
            primaryButtonText = stringResource(Res.string.yes),
            secondaryButtonText = stringResource(Res.string.no),
            onPrimaryClick = {
                showLogoutDialog = false
                onAction(ProfileScreenActions.OnLogout)
            },
            onSecondaryClick = { showLogoutDialog = false },
            onDismiss = { showLogoutDialog = false },
            dismissOnClickOutside = false
        )
    }

    if (showLanguageSheet) {
        LanguagePickerBottomSheet(
            currentLanguage = viewState.selectedLanguage,
            sheetState = sheetState,
            onConfirm = { selectedLang ->
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    showLanguageSheet = false
                    onAction(ProfileScreenActions.OnLanguageSelected(selectedLang))
                }
            },
            onDismiss = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    showLanguageSheet = false
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguagePickerBottomSheet(
    currentLanguage: String,
    sheetState: SheetState,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    // Pending selection is independent of the confirmed state so the user can
    // browse options before hitting Confirm.
    var pendingLanguage by remember(currentLanguage) { mutableStateOf(currentLanguage) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        contentWindowInsets = { WindowInsets.navigationBars },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = stringResource(Res.string.language),
                style = smMedium,
                color = Gray600,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                textAlign = TextAlign.Center
            )

            HorizontalDivider(color = Gray100)

            Spacer(modifier = Modifier.height(8.dp))

            LanguageOption(
                label = stringResource(Res.string.language_arabic),
                selected = pendingLanguage == "ar",
                onClick = { pendingLanguage = "ar" }
            )

            LanguageOption(
                label = stringResource(Res.string.language_english),
                selected = pendingLanguage == "en",
                onClick = { pendingLanguage = "en" }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onConfirm(pendingLanguage) },
                enabled = pendingLanguage != currentLanguage,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text(
                    text = stringResource(Res.string.confirm),
                    style = smMedium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun LanguageOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = Primary)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = smNormal, color = Gray600)
    }
}

@Composable
private fun ProfileInfoCard(
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
            Text(text = title, style = smMedium, color = Gray600)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = value ?: "", style = smMedium, color = Primary)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = painterResource(Res.drawable.arrow_narrow_left),
                    contentDescription = null,
                    tint = Gray600,
                    modifier = Modifier.scale(scaleX = -1f, scaleY = 1f)
                )
            }
        }
    }
}
