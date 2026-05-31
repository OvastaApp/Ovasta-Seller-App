package com.ovasta.sellers.ui.model

sealed class OrderSteps {
    data object Pending : OrderSteps()
    data object Assigned : OrderSteps()
    data object Picked : OrderSteps()
    data object Delivered : OrderSteps()
    data object Canceled : OrderSteps()

    companion object {
        fun fromStatusId(statusId: Int): OrderSteps {
            return when (statusId) {
                1 -> Pending
                2 -> Assigned
                3 -> Picked
                4 -> Delivered
                5 -> Canceled
                else -> Pending
            }
        }
    }
}
