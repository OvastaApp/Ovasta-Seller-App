package com.ovasta.sellers.presentation.home.data.model

sealed class OrderSteps {

    object Pending : OrderSteps()
    object Assigned : OrderSteps()
    object Picked : OrderSteps()
    object Delivered : OrderSteps()
    object Canceled : OrderSteps()

    companion object {
        fun fromStatusId(statusId: Int): OrderSteps {
            return when (statusId) {
                1 -> Pending
                2 -> Assigned
                3 -> Picked
                4 -> Delivered
                5 -> Canceled
                else -> throw IllegalArgumentException("Unknown statusId: $statusId")
            }
        }
    }
}