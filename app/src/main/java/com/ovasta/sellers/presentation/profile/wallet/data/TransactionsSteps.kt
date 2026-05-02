package com.ovasta.sellers.presentation.profile.wallet.data

sealed class TransactionsSteps {

    object Pending : TransactionsSteps()
    object Approved : TransactionsSteps()
    object Rejected : TransactionsSteps()

    companion object {
        fun fromStatusId(statusId: Int): TransactionsSteps {
            return when (statusId) {
                0 -> Pending
                1 -> Approved
                2 -> Rejected

                else -> throw IllegalArgumentException("Unknown statusId: $statusId")
            }
        }

        fun toStatus(status: TransactionsSteps): Int {
            return when (status) {
                is Pending -> 0
                is Approved -> 1
                is Rejected -> 2
            }
        }
    }

}