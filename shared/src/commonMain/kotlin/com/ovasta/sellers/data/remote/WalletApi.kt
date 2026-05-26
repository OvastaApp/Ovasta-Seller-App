package com.ovasta.sellers.data.remote

import com.ovasta.sellers.domain.model.ApiResponse
import com.ovasta.sellers.domain.model.RedeemPointsRequest
import com.ovasta.sellers.domain.model.WalletResponse
import com.ovasta.sellers.domain.model.WithdrawRequest
import com.ovasta.sellers.domain.model.WithdrawRequests
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class WalletApiService(private val client: HttpClient) {
    suspend fun getWalletTransactions(page: Int? = null): ApiResponse<WalletResponse> {
        return client.get("wallet") {
            if (page != null) parameter("page", page)
        }.body()
    }

    suspend fun getWithdrawalRequests(page: Int? = null): ApiResponse<List<WithdrawRequests>> {
        return client.get("withdrawal-requests") {
            if (page != null) parameter("page", page)
        }.body()
    }

    suspend fun redeemPoints(request: RedeemPointsRequest) {
        client.post("wallet/redeem-points") {
            setBody(request)
        }
    }

    suspend fun requestWithdraw(request: WithdrawRequest) {
        client.post("withdrawal-requests") {
            setBody(request)
        }
    }
}
