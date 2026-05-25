package com.ovasta.sellers.platform

import android.content.Context
import com.ovasta.sellers.R
import com.ovasta.sellers.base.ext.StringId
import com.ovasta.sellers.base.StringResourceProvider

class AndroidStringResourceProvider(
    private val context: Context
) : StringResourceProvider {
    
    private val stringResourceMap = mapOf(
        "valid_phone_required" to R.string.valid_phone_required,
        "delivery_fees_min_egp_error" to R.string.delivery_fees_min_egp_error,
        "address_required" to R.string.address_required,
        "valid_amount_required" to R.string.valid_amount_required,
        "delivery_date_required" to R.string.delivery_date_required,
        "delivery_time_required" to R.string.delivery_time_required,
        "order_submitted_successfully" to R.string.order_submitted_successfully,
        "order_cancelled_successfully" to R.string.order_cancelled_successfully,
        "no_wallet_balance_to_withdraw" to R.string.no_wallet_balance_to_withdraw,
        "mini_redeem_message" to R.string.mini_redeem_message,
        "request_submitted_successfully" to R.string.request_submitted_successfully,
        "generic_unknown_error" to R.string.generic_unknown_error,
    )

    override fun getString(stringId: StringId, vararg args: Any): String {
        val resId = stringResourceMap[stringId.key]
            ?: return stringId.key
        return if (args.isEmpty()) {
            context.getString(resId)
        } else {
            context.getString(resId, *args)
        }
    }
}
