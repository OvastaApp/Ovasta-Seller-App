package com.ovasta.sellers.presentation.profile.wallet.data

import com.ovasta.sellers.data.ApiResponse
import com.ovasta.sellers.presentation.profile.wallet.data.model.RedeemPointsRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface WalletApi {
    @GET("wallet")
    suspend fun getWalletTransactions(
        @Query("page") page: Int? = null
    ): ApiResponse<WalletResponse>

    @GET("withdrawal-requests")
    suspend fun getWithdrawalRequests(@Query("page") page: Int? = null): ApiResponse<List<WithdrawRequests>>

    @POST("wallet/redeem-points")
    suspend fun redeemPoints(@Body redeemPointsRequest: RedeemPointsRequest)

    @POST("withdrawal-requests")
    suspend fun requestWithdraw()
}