package com.ovasta.sellers.presentation.profile.orderhistory.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ovasta.sellers.base.*
import com.ovasta.sellers.platform.openPhoneDialer
import com.ovasta.sellers.presentation.home.data.model.DeliveryOrder
import com.ovasta.sellers.presentation.home.data.model.CourierInfo
import com.ovasta.sellers.presentation.home.data.model.DeliveryOrdersResponse
import com.ovasta.sellers.presentation.home.data.model.OrderSteps
import com.ovasta.sellers.presentation.home.data.model.OrderSteps.Companion.fromStatusId
import com.ovasta.sellers.presentation.profile.orderhistory.presentation.OrderHistoryAction
import com.ovasta.sellers.presentation.profile.orderhistory.presentation.OrderHistoryViewState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryContent(
    viewState: OrderHistoryViewState,
    onAction: (OrderHistoryAction) -> Unit,
    onNavigateBack: () -> Unit = {}

) {
    LaunchedEffect(Unit) {
        onAction(OrderHistoryAction.LoadOrderHistory)
    }
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Surface(
                shadowElevation = 2.dp, color = Color.White
            ) {
                CenteredTextAppBar(
                    "last_orders",
                    onBackButtonPressed = { onNavigateBack() })
            }
        }) { paddingValues ->
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
                                text = "no_orders_yet",
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
                            onCallCourier = { phone -> openPhoneDialer(phone) },
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
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
        shape = RoundedCornerShape(12.dp),
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
                    .padding(14.dp)
            ) {
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

                InfoRow(
                    icon = Icons.Default.LocationOn, value = order.toAddress
                )
                Spacer(modifier = Modifier.height(6.dp))

                InfoRow(
                    icon = Icons.Default.Call, value = order.receiverMobile
                )

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Gray200)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Gray500
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${order.deliveryPrice} EGP",
                            style = smSemiBold,
                            maxLines = 1
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Gray500
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${order.totalPrice} EGP",
                            style = smSemiBold,
                            maxLines = 1
                        )
                    }
                }

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
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${courier.firstName} ${courier.lastName}",
                                style = smSemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        if (!courier.mobile.isNullOrEmpty()) {
                            val courierMobile = courier.mobile
                            IconButton(
                                onClick = { courierMobile?.let { onCallCourier(it) } },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
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
        OrderSteps.Pending -> Pair("status_pending", Color.Gray)
        OrderSteps.Assigned -> Pair("status_assigned", Primary)
        OrderSteps.Picked -> Pair("status_picked", Amber)
        OrderSteps.Delivered -> Pair("status_delivered", Green)
        OrderSteps.Canceled -> Pair("status_cancelled", Color.Red)
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
private fun InfoRow(icon: ImageVector, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = Gray500
        )
        Spacer(modifier = Modifier.width(6.dp))
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
        viewState = OrderHistoryViewState(
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
