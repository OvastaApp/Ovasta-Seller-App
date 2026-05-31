package com.ovasta.sellers.data.repository

import com.ovasta.sellers.data.remote.WalletApiService
import com.ovasta.sellers.domain.model.RedeemPointsRequest
import com.ovasta.sellers.domain.model.WalletResponse
import com.ovasta.sellers.domain.model.WithdrawRequest
import com.ovasta.sellers.domain.model.WithdrawRequests
import com.ovasta.sellers.domain.repository.IWalletRepository

class WalletRepository(private val api: WalletApiService) : IWalletRepository {
    override suspend fun getWalletTransactions(page: Int?): WalletResponse =
        api.getWalletTransactions(page).data ?: WalletResponse()

    override suspend fun getWithdrawalRequests(page: Int?): List<WithdrawRequests> =
        api.getWithdrawalRequests(page).data ?: emptyList()

    override suspend fun redeemPoints(points: Int) = api.redeemPoints(RedeemPointsRequest(points))
    override suspend fun requestWithdraw(amount: Double) = api.requestWithdraw(WithdrawRequest(amount))
}
