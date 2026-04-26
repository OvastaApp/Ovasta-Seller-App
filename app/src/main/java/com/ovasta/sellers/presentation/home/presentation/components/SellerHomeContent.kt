package com.ovasta.sellers.presentation.home.presentation.components

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
import com.ovasta.sellers.base.lgSemiBold
import com.ovasta.sellers.base.mdRegular
import com.ovasta.sellers.base.mdSemiBold
import com.ovasta.sellers.base.smNormal
import com.ovasta.sellers.base.smSemiBold
import com.ovasta.sellers.base.xsMedium
import com.ovasta.sellers.presentation.home.data.model.OrderInfo
import com.ovasta.sellers.presentation.home.data.model.Courier
import com.ovasta.sellers.presentation.home.data.model.HomeInfo
import com.ovasta.sellers.presentation.home.presentation.HomeScreenActions
import com.ovasta.sellers.presentation.home.presentation.HomeViewState
import com.ovasta.sellers.ui.theme.BLACK
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerHomeContent(
    viewState: HomeViewState,
    onAction: (HomeScreenActions) -> Unit
) {
    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CenteredTextAppBar(
                title = stringResource(R.string.home), showBackButton = false, actions = {
                    IconButton(onClick = {
                        onAction(HomeScreenActions.ChangeLogoutDialogStatus(isVisible = true))
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_logout),
                            contentDescription = "Logout",
                            tint = Color.Black
                        )
                    }
                })
        },
        floatingActionButton = {},
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Gray100),
            contentPadding = PaddingValues(dimensionResource(com.intuit.sdp.R.dimen._12sdp)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(com.intuit.sdp.R.dimen._12sdp))
        ) {
            // Points card
            item(key = "points") {
                PointsCard(homeInfo = viewState.homeInfo)
            }

            // Orders header + Create order button
            item(key = "orders_header") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.my_orders),
                        style = lgSemiBold.copy(color = BLACK)
                    )
                    Button(
                        onClick = { onAction(HomeScreenActions.CreateOrder) },
                        shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        contentPadding = PaddingValues(
                            horizontal = dimensionResource(com.intuit.sdp.R.dimen._12sdp),
                            vertical = dimensionResource(com.intuit.sdp.R.dimen._6sdp)
                        )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_add),
                            contentDescription = null,
                            modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._16sdp))
                        )
                        Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._4sdp)))
                        Text(
                            text = stringResource(R.string.create_order),
                            style = xsMedium.copy(color = Color.White)
                        )
                    }
                }
            }

            // Orders list
            val orders = viewState.myOrders.orEmpty()
            if (orders.isEmpty()) {
                item(key = "empty") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimensionResource(com.intuit.sdp.R.dimen._40sdp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_orders_yet),
                            style = mdRegular.copy(color = Gray500)
                        )
                    }
                }
            } else {
                itemsIndexed(orders, key = { _, order -> order.id }) { _, order ->
                    OrderCard(
                        order = order,
                        onClick = { onAction(HomeScreenActions.OrderClicked(order.id)) },
                        onCallCourier = { phone -> onAction(HomeScreenActions.CallCourier(phone)) })
                }
            }

            // Bottom spacing
            item { Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._16sdp))) }
        }
    }
}

@Composable
private fun PointsCard(homeInfo: HomeInfo?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._12sdp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(com.intuit.sdp.R.dimen._14sdp),
                    vertical = dimensionResource(com.intuit.sdp.R.dimen._10sdp)
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            val points = homeInfo?.pointsCount ?: 0
            val money = String.format("%.0f", homeInfo?.walletBalance ?: 0.0)
            val rate = String.format("%.1f", homeInfo?.pointsPerPound ?: 0.0)

            Text(
                text = "$points ", style = lgSemiBold.copy(color = Primary)
            )
            Text(
                text = "pt  ≈  ", style = smNormal.copy(color = Gray500)
            )
            Text(
                text = "$money EGP", style = mdSemiBold.copy(color = BLACK)
            )
            Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._6sdp)))
            Text(
                text = "(1pt = $rate)", style = xsMedium.copy(color = Gray500)
            )
        }
    }
}

@Composable
private fun OrderCard(order: OrderInfo, onClick: () -> Unit, onCallCourier: (String) -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._12sdp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(com.intuit.sdp.R.dimen._14sdp))
        ) {
            // Header: Order ID + Status badge area
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.hash_task, order.id.toString()),
                    style = mdSemiBold.copy(color = Primary)
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._10sdp)))

            // Client info section
            if (!order.clientName.isNullOrEmpty()) {
                InfoRow(
                    icon = R.drawable.ic_profile, value = order.clientName!!
                )
                Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._6sdp)))
            }

            InfoRow(
                icon = R.drawable.ic_location, value = order.clientAddress
            )
            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._6sdp)))

            InfoRow(
                icon = R.drawable.ic_call, value = order.clientPhone
            )

            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._10sdp)))
            HorizontalDivider(color = Gray200)
            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._10sdp)))

            // Pricing section
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delivery_fees),
                        contentDescription = null,
                        modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._14sdp)),
                        tint = Gray500
                    )
                    Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._4sdp)))
                    Text(
                        text = stringResource(R.string.price_currency, order.deliveryFees),
                        style = smSemiBold.copy(color = BLACK),
                        maxLines = 1
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_price),
                        contentDescription = null,
                        modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._14sdp)),
                        tint = Gray500
                    )
                    Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._4sdp)))
                    Text(
                        text = stringResource(R.string.price_currency, order.orderPrice),
                        style = smSemiBold.copy(color = BLACK),
                        maxLines = 1
                    )
                }
            }

            // Courier info
            if (!order.courier.name.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))
                HorizontalDivider(color = Gray200)
                Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_delivery_agent),
                            contentDescription = null,
                            modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._16sdp)),
                            tint = Primary
                        )
                        Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._6sdp)))
                        Text(
                            text = order.courier.name!!,
                            style = smNormal.copy(color = BLACK),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (!order.courier.phone.isNullOrEmpty()) {
                        IconButton(
                            onClick = { onCallCourier(order.courier.phone!!) },
                            modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._28sdp))
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_call),
                                contentDescription = null,
                                modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._16sdp)),
                                tint = Primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(icon: Int, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._16sdp)),
            tint = Gray500
        )
        Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._6sdp)))
        Text(
            text = value,
            style = smNormal.copy(color = BLACK),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun SellerHomeContentPreview() {
    SellerHomeContent(
        viewState = HomeViewState(
            homeInfo = HomeInfo(
                walletBalance = 120.50,
                pointsCount = 300.00,
                pointsPerPound = 5.0,
                minRedeemPoints = 140.0
            ), myOrders = listOf(
                OrderInfo(
                    id = 101,
                    clientName = "Ahmed Mohamed",
                    clientAddress = "Nasr City, Cairo",
                    clientPhone = "01012345678",
                    orderPrice = "150.00",
                    deliveryFees = "25.00",
                    courier = Courier(id = 1, name = "Courier 1", phone = "010000")
                ), OrderInfo(
                    id = 102,
                    clientName = "Sara Ali",
                    clientAddress = "Maadi, Cairo",
                    clientPhone = "01198765432",
                    orderPrice = "320.50",
                    deliveryFees = "30.00",
                    courier = Courier(id = 2, name = "Courier 2", phone = "010001")
                )
            )
        ), onAction = {})
}
