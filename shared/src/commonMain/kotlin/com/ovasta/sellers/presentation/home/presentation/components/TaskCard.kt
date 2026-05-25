package com.ovasta.sellers.presentation.home.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ovasta.sellers.base.Base_white
import com.ovasta.sellers.base.Gray200
import com.ovasta.sellers.base.Gray800
import com.ovasta.sellers.base.components.sharedComposable.NavigationAction
import com.ovasta.sellers.base.mdMedium
import com.ovasta.sellers.presentation.home.data.model.HomeTask
import kotlin.text.ifEmpty

@Composable
fun TaskCard(
    homeTask: HomeTask,
    currency: String,
    startedTaskId: Int,
    onTaskDetailsClick: (taskId: Int, retailerId: Int) -> Unit,
    onDirectionClick: (Double, Double) -> Unit,
    onContactClick: (String) -> Unit,
    onWhatsAppClick: (String) -> Unit,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Base_white),
        border = BorderStroke(
            width = 1.dp,
            color = Gray200
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .testTag("taskCard_${homeTask.taskId}")
    ) {
        Column(
            modifier = Modifier.padding(
                all = 16.dp
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                InfoRow(
                    label = homeTask.taskId.toString(),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("taskId")
                )

                StatusTag(
                    statusId = homeTask.statusId,
                    statusName = homeTask.statusName,
                    modifier = Modifier.testTag("taskStatus")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            InfoRow(
                label = homeTask.customerAddress?.ifEmpty { "" }
                    ?: "",
                modifier = Modifier.testTag("clientAddress")
            )

            Spacer(modifier = Modifier.height(8.dp))

            InfoRow(
                label = "${homeTask.itemsCount}",
                modifier = Modifier.testTag("productCount")
            )

            Spacer(modifier = Modifier.height(8.dp))

            InfoRow(
                label = "${homeTask.totalPrice} $currency",
                textStyle = mdMedium.copy(color = Gray800),
                modifier = Modifier.testTag("totalPrice")
            )

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                thickness = 1.dp,
                color = Gray200
            )

            NavigationAction(
                clickedTaskId = homeTask.taskId,
                onDirectionClick = {
                    onDirectionClick(
                        homeTask.clientLang, homeTask.clientLat
                    )
                },
                onContactClick = { onContactClick(homeTask.clientPhone ?: "") },
                onWhatsAppClick = { onWhatsAppClick(homeTask.clientWhatsapp ?: "") }
            )
        }
    }
}

@Preview
@Composable
fun PreviewTaskCard() {
    TaskCard(
        homeTask = HomeTask(
            taskId = 12345,
            statusId = 1,
            statusName = "Pending",
            customerAddress = "123 Main St, City",
            itemsCount = 5,
            totalPrice = 150.0f,
            clientLat = 37.7749,
            clientLang = -122.4194,
            clientPhone = "1234567890"
        ),
        currency = "$",
        startedTaskId = -1,
        onTaskDetailsClick = { _, _ -> },
        onDirectionClick = { _, _ -> },
        onContactClick = { _ -> },
        onWhatsAppClick = { _ -> },
        onClick = {}
    )
}
