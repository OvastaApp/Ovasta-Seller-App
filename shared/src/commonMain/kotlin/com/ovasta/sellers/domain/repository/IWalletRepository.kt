package com.ovasta.sellers.domain.repository

import com.ovasta.sellers.domain.model.WalletResponse
import com.ovasta.sellers.domain.model.WithdrawRequests

interface IWalletRepository {
    suspend fun getWalletTransactions(page: Int? = null): WalletResponse
    suspend fun getWithdrawalRequests(page: Int? = null): List<WithdrawRequests>
    suspend fun redeemPoints(points: Int)
    suspend fun requestWithdraw(amount: Double)
}
