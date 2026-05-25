package com.ovasta.sellers.presentation.profile.wallet.data

import com.ovasta.sellers.data.network.SellerApiService
import com.ovasta.sellers.presentation.profile.wallet.data.model.RedeemPointsRequest
import com.ovasta.sellers.presentation.profile.wallet.data.model.WithdrawRequest

class WalletRemoteDataSource(private val apiService: SellerApiService) : IWalletRemoteDataSource {
    override suspend fun getWalletTransactions(page: Int?): WalletResponse {
        return apiService.getWalletTransactions(page = page).data
    }

    override suspend fun getWithdrawalRequests(page: Int?): List<WithdrawRequests> {
        return apiService.getWithdrawalRequests(page = page).data
    }

    override suspend fun redeemPoints(points: Int) {
        apiService.redeemPoints(RedeemPointsRequest(points))
    }

    override suspend fun requestWithdraw(amount: Double) {
        apiService.requestWithdraw(WithdrawRequest(amount))
    }
}