package com.ovasta.sellers.presentation.profile.wallet.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ovasta.sellers.R
import com.ovasta.sellers.base.CenteredTextAppBar
import com.ovasta.sellers.base.Gray100
import com.ovasta.sellers.base.Gray200
import com.ovasta.sellers.base.Gray500
import com.ovasta.sellers.base.Primary
import com.ovasta.sellers.base.mdRegular
import com.ovasta.sellers.base.mdSemiBold
import com.ovasta.sellers.base.smNormal
import com.ovasta.sellers.base.smSemiBold
import com.ovasta.sellers.base.xsMedium
import com.ovasta.sellers.presentation.home.data.model.DeliveryOrder
import com.ovasta.sellers.presentation.home.data.model.CourierInfo
import com.ovasta.sellers.presentation.home.data.model.DeliveryOrdersResponse
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.ExperimentalMaterialApi
import com.ovasta.sellers.presentation.profile.wallet.presentation.WalletAction
import com.ovasta.sellers.presentation.profile.wallet.presentation.WalletViewState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun WalletContnet(
    viewState: WalletViewState,
    onAction: (WalletAction) -> Unit,
    onNavigateBack: () -> Unit = {}

) {

    LaunchedEffect(Unit) {

    }
    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Surface(
                shadowElevation = 2.dp, color = Color.White
            ) {
                CenteredTextAppBar(
                    stringResource(R.string.wallet),
                    onBackButtonPressed = { onNavigateBack() })
            }
        }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {

        }
    }
}
