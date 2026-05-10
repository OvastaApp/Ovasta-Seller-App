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
import com.ovasta.sellers.base.components.sharedComposable.BaseDialog
import com.ovasta.sellers.base.lgSemiBold
import com.ovasta.sellers.base.mdRegular
import com.ovasta.sellers.base.mdSemiBold
import com.ovasta.sellers.base.smNormal
import com.ovasta.sellers.base.smSemiBold
import com.ovasta.sellers.base.xsMedium
import com.ovasta.sellers.presentation.home.data.model.HomeInfo
import com.ovasta.sellers.presentation.home.data.model.DeliveryOrder
import com.ovasta.sellers.presentation.home.data.model.CourierInfo
import com.ovasta.sellers.presentation.home.data.model.DeliveryOrdersResponse
import com.ovasta.sellers.presentation.home.presentation.HomeScreenActions
import com.ovasta.sellers.presentation.home.presentation.HomeViewState
import com.ovasta.sellers.ui.theme.BLACK
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.ui.platform.LocalContext
import com.ovasta.sellers.base.Amber
import com.ovasta.sellers.base.Green
import com.ovasta.sellers.base.ext.makePhoneCall
import com.ovasta.sellers.base.xsRegular
import com.ovasta.sellers.presentation.home.data.model.OrderSteps
import com.ovasta.sellers.presentation.home.data.model.OrderSteps.Companion.fromStatusId
import com.ovasta.sellers.presentation.home.data.model.OrderSteps.Companion.toStatus

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun SellerHomeContent(
    viewState: HomeViewState,
    onAction: (HomeScreenActions) -> Unit
) {
    val context = LocalContext.current

    val refreshing = viewState.isRefreshing ?: false
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = { onAction(HomeScreenActions.RefreshHome) }
    )

    var cancelOrderId by remember { mutableStateOf<Int?>(null) }
    var showCancelDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CenteredTextAppBar(
                title = stringResource(R.string.home), showBackButton = false
            )
        },
        floatingActionButton = {},
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
        ) {
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
                            text = stringResource(R.string.my_orders),
                            style = lgSemiBold // section header
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
                val orders = viewState.deliveryOrdersResponse?.orders.orEmpty()
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
                        DeliveryOrderCard(
                            order = order,
                            onClick = { onAction(HomeScreenActions.OrderClicked(order.id)) },
                            onCallCourier = { phone -> context.makePhoneCall(phone) },
                            onCancelOrder = {
                                cancelOrderId = order.id
                                showCancelDialog = true
                            }
                        )
                    }
                }

                // Bottom spacing
                item { Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._16sdp))) }
            }
            PullRefreshIndicator(
                refreshing = refreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }

    // Cancel confirmation dialog
    if (showCancelDialog && cancelOrderId != null) {
        BaseDialog(
            title = stringResource(R.string.cancel_order),
            message = stringResource(R.string.are_you_sure_you_want_to_cancel_order),
            primaryButtonText = stringResource(R.string.yes),
            secondaryButtonText = stringResource(R.string.no),
            onPrimaryClick = {
                onAction(HomeScreenActions.CancelOrder(cancelOrderId!!))
                showCancelDialog = false
                cancelOrderId = null
            },
            onSecondaryClick = {
                showCancelDialog = false
                cancelOrderId = null
            },
            onDismiss = {
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
        shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._12sdp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(com.intuit.sdp.R.dimen._14sdp),
                    vertical = dimensionResource(com.intuit.sdp.R.dimen._10sdp)
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val points = homeInfo?.pointsCount ?: 0.0
            val rate = homeInfo?.pointsPerPound ?: 0.0

            val moneyValue = if (rate > 0) points / rate else 0.0

            val money = String.format("%.2f", moneyValue)
            val formattedPoints = String.format("%.0f", points)
            val formattedRate = String.format("%.1f", rate)

            // الصف الأول
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$formattedPoints ",
                    style = lgSemiBold.copy(color = Primary)
                )

                Text(
                    text = "${stringResource(R.string.points)} = ",
                    style = smNormal.copy(color = Gray500)
                )

                Text(
                    text = "$money ${stringResource(R.string.egp)}",
                    style = mdSemiBold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(
                    R.string.points_rate_format,
                    formattedRate,
                    stringResource(R.string.points),
                    stringResource(R.string.egp)
                ),
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
        shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._12sdp)),
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
                    StatusBadge(order.statusId)
                }

                Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._10sdp)))

                // Address
                InfoRow(
                    icon = R.drawable.ic_location, value = order.toAddress
                )
                Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._6sdp)))

                // Receiver phone
                InfoRow(
                    icon = R.drawable.ic_call, value = order.receiverMobile
                )

                Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._10sdp)))
                HorizontalDivider(color = Gray200)
                Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._10sdp)))

                // Pricing section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
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
                            text = stringResource(R.string.price_currency, order.deliveryPrice),
                            style = smSemiBold,
                            maxLines = 1
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_total_price),
                            contentDescription = null,
                            modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._14sdp)),
                            tint = Gray500
                        )
                        Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._4sdp)))
                        Text(
                            text = stringResource(R.string.price_currency, order.totalPrice),
                            style = smSemiBold,
                            maxLines = 1
                        )
                    }
                }

                // Note
                if (!order.note.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))
                    Text(
                        text = order.note,
                        style = xsMedium.copy(color = Gray500),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Courier info
                if (order.courier != null) {
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
                                text = "${order.courier.firstName} ${order.courier.lastName}",
                                style = smSemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        if (!order.courier.mobile.isNullOrEmpty()) {
                            IconButton(
                                onClick = { onCallCourier(order.courier.mobile) },
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

                // --- Cancel Button at the bottom, after all content ---
                if (order.canCancelOrder == true && onCancelOrder != null) {
                    Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._10sdp)))
                    OutlinedButton(
                        onClick = { onCancelOrder() },
                        shape = RoundedCornerShape(50),
                        border = ButtonDefaults.outlinedButtonBorder,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.cancel_order),
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
        OrderSteps.Pending -> Pair(stringResource(R.string.status_pending), Color.Gray)
        OrderSteps.Assigned -> Pair(stringResource(R.string.status_assigned), Primary)
        OrderSteps.Picked -> Pair(stringResource(R.string.status_picked), Amber)
        OrderSteps.Delivered -> Pair(stringResource(R.string.status_delivered), Green)
        OrderSteps.Canceled -> Pair(stringResource(R.string.status_cancelled), Color.Red)
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
