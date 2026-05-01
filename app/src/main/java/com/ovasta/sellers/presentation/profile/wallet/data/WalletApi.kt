package com.ovasta.sellers.presentation.profile.wallet.data

import com.ovasta.sellers.data.ApiResponse
import com.ovasta.sellers.presentation.home.data.model.DeliveryOrdersResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WalletApi {
    @GET("WalletScreen")
    suspend fun getWalletTransactions(
        @Query("page") page: Int? = null
    ): ApiResponse<WalletTransactionsResponse>

    @GET("withdrawal-requests")
    suspend fun getWithdrawalRequests(
        @Query("page") page: Int? = null
    ): ApiResponse<List<WithdrawRequests>>
}