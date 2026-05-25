package com.ovasta.sellers.base

import com.ovasta.sellers.base.ext.StringId

interface StringResourceProvider {
    fun getString(stringId: StringId, vararg args: Any): String
}

object StringIds {
    val validPhoneRequired = StringId("valid_phone_required")
    val deliveryFeesMinEgpError = StringId("delivery_fees_min_egp_error")
    val addressRequired = StringId("address_required")
    val validAmountRequired = StringId("valid_amount_required")
    val deliveryDateRequired = StringId("delivery_date_required")
    val deliveryTimeRequired = StringId("delivery_time_required")
    val orderSubmittedSuccessfully = StringId("order_submitted_successfully")
    val orderCancelledSuccessfully = StringId("order_cancelled_successfully")
    val noWalletBalanceToWithdraw = StringId("no_wallet_balance_to_withdraw")
    val miniRedeemMessage = StringId("mini_redeem_message")
    val requestSubmittedSuccessfully = StringId("request_submitted_successfully")
    val genericUnknownError = StringId("generic_unknown_error")
}
