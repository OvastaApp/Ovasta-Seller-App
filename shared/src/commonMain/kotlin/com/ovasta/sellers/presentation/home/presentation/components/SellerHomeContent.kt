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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ovasta.sellers.base.Amber
import com.ovasta.sellers.base.CenteredTextAppBar
import com.ovasta.sellers.base.Gray100
import com.ovasta.sellers.base.Gray200
import com.ovasta.sellers.base.Gray500
import com.ovasta.sellers.base.Green
import com.ovasta.sellers.base.Primary
import com.ovasta.sellers.base.components.sharedComposable.BaseDialog
import com.ovasta.sellers.base.lgSemiBold
import com.ovasta.sellers.base.mdRegular
import com.ovasta.sellers.base.mdSemiBold
import com.ovasta.sellers.base.smNormal
import com.ovasta.sellers.base.smSemiBold
import com.ovasta.sellers.base.xsMedium
import com.ovasta.sellers.base.xsRegular
import com.ovasta.sellers.presentation.home.data.model.CourierInfo
import com.ovasta.sellers.presentation.home.data.model.DeliveryOrder
import com.ovasta.sellers.presentation.home.data.model.DeliveryOrdersResponse
import com.ovasta.sellers.presentation.home.data.model.HomeInfo
import com.ovasta.sellers.presentation.home.data.model.OrderSteps
import com.ovasta.sellers.presentation.home.data.model.OrderSteps.Companion.fromStatusId
import com.ovasta.sellers.presentation.home.presentation.HomeScreenActions
import com.ovasta.sellers.presentation.home.presentation.HomeViewState
import com.ovasta.sellers.platform.openPhoneDialer
import com.ovasta.sellers.resources.Res
import com.ovasta.sellers.resources.are_you_sure_you_want_to_cancel_order
import com.ovasta.sellers.resources.cancel_order
import com.ovasta.sellers.resources.create_order
import com.ovasta.sellers.resources.home
import com.ovasta.sellers.resources.my_orders
import com.ovasta.sellers.resources.no
import com.ovasta.sellers.resources.no_orders_yet
import com.ovasta.sellers.resources.points_rate
import com.ovasta.sellers.resources.price_currency
import com.ovasta.sellers.resources.status_pending
import com.ovasta.sellers.resources.status_assigned
import com.ovasta.sellers.resources.status_picked
import com.ovasta.sellers.resources.status_delivered
import com.ovasta.sellers.resources.status_cancelled
import com.ovasta.sellers.resources.yes
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerHomeContent(
    viewState: HomeViewState,
    onAction: (HomeScreenActions) -> Unit
) {
    var cancelOrderId by remember { mutableStateOf<Int?>(null) }
    var showCancelDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier,
        topBar = {
            CenteredTextAppBar(
                title = stringResource(Res.string.home), showBackButton = false
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Gray100),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Points card
                item(key = "points") {
                    PointsCard(
                        homeInfo = viewState.homeInfo,
                        onClick = { onAction(HomeScreenActions.NavigateToWallet) }
                    )
                }

                // Orders header + Create order button
                item(key = "orders_header") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                    Text(
                        text = stringResource(Res.string.my_orders),
                        style = lgSemiBold
                    )
                    Button(
                        onClick = { onAction(HomeScreenActions.CreateOrder) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        contentPadding = PaddingValues(
                            horizontal = 12.dp,
                            vertical = 6.dp
                        )
                    ) {
                        Text(
                            text = stringResource(Res.string.create_order),
                                style = xsMedium.copy(color = Color.White)
                            )
                        }
                    }
                }

                // Orders list
                val orders = viewState.deliveryOrdersResponse?.orders.orEmpty()
                if (orders.isEmpty()) {
                    item(key = "empty") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(Res.string.no_orders_yet),
                                style = mdRegular.copy(color = Gray500)
                            )
                        }
                    }
                } else {
                    itemsIndexed(
                        items = orders,
                        key = { index, order -> "${order.id}_$index" }
                    ) { _, order ->
                        DeliveryOrderCard(
                            order = order,
                            onClick = { onAction(HomeScreenActions.OrderClicked(order.id)) },
                            onCallCourier = { phone -> openPhoneDialer(phone) },
                            onCancelOrder = {
                                cancelOrderId = order.id
                                showCancelDialog = true
                            }
                        )
                    }
                }

                // Bottom spacing
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }

    // Cancel confirmation dialog
    if (showCancelDialog && cancelOrderId != null) {
        BaseDialog(
            title = stringResource(Res.string.cancel_order),
            message = stringResource(Res.string.are_you_sure_you_want_to_cancel_order),
            primaryButtonText = stringResource(Res.string.yes),
            secondaryButtonText = stringResource(Res.string.no),
            onPrimaryClick = {
                onAction(HomeScreenActions.CancelOrder(cancelOrderId!!))
                showCancelDialog = false
                cancelOrderId = null
            },
            onSecondaryClick = {
                showCancelDialog = false
                cancelOrderId = null
            },
            dismissOnClickOutside = false
        )
    }
}

@Composable
private fun PointsCard(homeInfo: HomeInfo?, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 14.dp,
                    vertical = 10.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val points = homeInfo?.pointsCount ?: 0.0
            val rate = homeInfo?.pointsPerPound ?: 0.0

            val moneyValue = if (rate > 0) points / rate else 0.0

            val money = (moneyValue * 100).toLong() / 100.0
            val formattedPoints = points.toLong().toString()
            val formattedRate = (rate * 10).toLong() / 10.0

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$formattedPoints ",
                    style = lgSemiBold.copy(color = Primary)
                )

                Text(
                    text = " = ",
                    style = smNormal.copy(color = Gray500)
                )

                Text(
                    text = "$money ",
                    style = mdSemiBold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(Res.string.points_rate),
                style = xsRegular.copy(color = Gray500)
            )
        }
    }
}

@Composable
private fun DeliveryOrderCard(
    order: DeliveryOrder,
    onClick: () -> Unit,
    onCallCourier: (String) -> Unit = {},
    onCancelOrder: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                // Header: Order ID + Status badge area
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "#${order.id}",
                        style = mdSemiBold.copy(color = Primary)
                    )
                    StatusBadge(order.statusId)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Address
                InfoRow(value = order.toAddress)
                Spacer(modifier = Modifier.height(6.dp))

                // Receiver phone
                InfoRow(value = order.receiverMobile)

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Gray200)
                Spacer(modifier = Modifier.height(10.dp))

                // Pricing section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(Res.string.price_currency, order.deliveryPrice.toString()),
                            style = smSemiBold,
                            maxLines = 1
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(Res.string.price_currency, order.totalPrice.toString()),
                            style = smSemiBold,
                            maxLines = 1
                        )
                    }
                }

                // Note
                val note = order.note
                if (!note.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = note,
                        style = xsMedium.copy(color = Gray500),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Courier info
                val courier = order.courier
                if (courier != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Gray200)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "${courier.firstName} ${courier.lastName}",
                                style = smSemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Cancel Button
                if (order.canCancelOrder == true && onCancelOrder != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { onCancelOrder() },
                        shape = RoundedCornerShape(50),
                        border = ButtonDefaults.outlinedButtonBorder,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(Res.string.cancel_order),
                            style = xsMedium.copy(color = Color.Red),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(statusId: Int) {
    val stepId = fromStatusId(statusId)
    val (text, color) = when (stepId) {
        OrderSteps.Pending -> Pair(stringResource(Res.string.status_pending), Color.Gray)
        OrderSteps.Assigned -> Pair(stringResource(Res.string.status_assigned), Primary)
        OrderSteps.Picked -> Pair(stringResource(Res.string.status_picked), Amber)
        OrderSteps.Delivered -> Pair(stringResource(Res.string.status_delivered), Green)
        OrderSteps.Canceled -> Pair(stringResource(Res.string.status_cancelled), Color.Red)
        else -> Pair("-", Color.Gray)
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f),
        contentColor = color
    ) {
        Text(
            text = text,
            style = xsMedium.copy(color = color),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun InfoRow(value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = value,
            style = smNormal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showSystemUi = true, showBackground = true, locale = "ar")
@Composable
fun SellerHomeContentPreview() {
    SellerHomeContent(
        viewState = HomeViewState(
            homeInfo = HomeInfo(pointsCount = 1200.1, walletBalance = 1500.0, pointsPerPound = 0.8),
            deliveryOrdersResponse =
                DeliveryOrdersResponse(
                    currentPage = 1,
                    perPage = 10,
                    total = 40,
                    lastPage = 4,
                    orders = listOf(
                        DeliveryOrder(
                            id = 12345,
                            toAddress = "123 Main St, City",
                            receiverMobile = "0123456789",
                            deliveryPrice = 50.0,
                            collectionAmount = 200.0,
                            deliveredAt = "2024-06-01T10:00:00Z",
                            cashbackAwarded = true,
                            createdAt = "",
                            totalPrice = 250.0,
                            note = "Handle with care",
                            statusId = 2,
                            courier = CourierInfo(
                                firstName = "John",
                                lastName = "Doe",
                                mobile = "0123456789"
                            )
                        ),
                    )
                )
        ),
        onAction = {}
    )
}
