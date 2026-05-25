package com.ovasta.sellers.presentation.profile.wallet.data

sealed class TransactionsSteps {

    object Pending : TransactionsSteps()
    object Approved : TransactionsSteps()
    object Rejected : TransactionsSteps()

    companion object {
        fun fromStatusId(statusId: Int): TransactionsSteps {
            return when (statusId) {
                1 -> Pending
                2 -> Approved
                3 -> Rejected

                else -> throw IllegalArgumentException("Unknown statusId: $statusId")
            }
        }
    }

}