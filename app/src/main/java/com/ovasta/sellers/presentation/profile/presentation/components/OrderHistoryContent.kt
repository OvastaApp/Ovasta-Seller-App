package com.ovasta.sellers.presentation.profile.presentation.components

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
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.ui.platform.LocalContext
import com.ovasta.sellers.base.Amber
import com.ovasta.sellers.base.Green
import com.ovasta.sellers.base.ext.makePhoneCall
import com.ovasta.sellers.presentation.home.data.model.OrderSteps
import com.ovasta.sellers.presentation.home.data.model.OrderSteps.Companion.fromStatusId
import com.ovasta.sellers.presentation.profile.presentation.ProfileScreenActions
import com.ovasta.sellers.presentation.profile.presentation.ProfileViewState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun OrderHistoryContent(
    viewState: ProfileViewState,
    onAction: (ProfileScreenActions) -> Unit,
    onNavigateBack: () -> Unit = {}

) {
    val context = LocalContext.current

    val refreshing = viewState.isRefreshing
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = { onAction(ProfileScreenActions.RefreshOrders) }
    )

    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Surface(
                shadowElevation = 2.dp, color = Color.White
            ) {
                CenteredTextAppBar(
                    stringResource(R.string.order_details),
                    onBackButtonPressed = { onNavigateBack() })
            }
        }) { paddingValues ->
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
                            onCallCourier = { phone -> context.makePhoneCall(phone) },
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
}

@Composable
private fun DeliveryOrderCard(
    order: DeliveryOrder,
    onCallCourier: (String) -> Unit = {},
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._12sdp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
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
fun OrderHistoryContentPreview() {
    OrderHistoryContent(
        viewState = ProfileViewState(
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
