package com.ovasta.sellers.presentation.profile.wallet.data

import com.ovasta.sellers.data.network.SellerApiService
import com.ovasta.sellers.presentation.profile.wallet.data.model.RedeemPointsRequest
import com.ovasta.sellers.presentation.profile.wallet.data.model.WithdrawRequest

class WalletRemoteDataSource(private val apiService: SellerApiService) : IWalletRemoteDataSource {
    override suspend fun getWalletTransactions(page: Int?): WalletResponse {
        val response = apiService.getWalletTransactions(page = page)
        return response.data ?: throw IllegalStateException("Failed to fetch wallet: ${response.message}")
    }

    override suspend fun getWithdrawalRequests(page: Int?): List<WithdrawRequests> {
        val response = apiService.getWithdrawalRequests(page = page)
        return response.data ?: throw IllegalStateException("Failed to fetch withdrawals: ${response.message}")
    }

    override suspend fun redeemPoints(points: Int) {
        apiService.redeemPoints(RedeemPointsRequest(points))
    }

    override suspend fun requestWithdraw(amount: Double) {
        apiService.requestWithdraw(WithdrawRequest(amount))
    }
}