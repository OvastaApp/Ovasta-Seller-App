package com.ovasta.sellers.data.network

import com.ovasta.sellers.data.ApiResponse
import com.ovasta.sellers.data.User
import com.ovasta.sellers.data.notification.FcmTokenRequest
import com.ovasta.sellers.presentation.auth.login.data.model.LoginRequest
import com.ovasta.sellers.presentation.createOrder.data.model.CreateOrderRequest
import com.ovasta.sellers.presentation.home.data.model.DeliveryOrdersResponse
import com.ovasta.sellers.presentation.home.data.model.HomeInfo
import com.ovasta.sellers.presentation.profile.wallet.data.WithdrawRequests
import com.ovasta.sellers.presentation.profile.wallet.data.WalletResponse
import com.ovasta.sellers.presentation.profile.wallet.data.model.RedeemPointsRequest
import com.ovasta.sellers.presentation.profile.wallet.data.model.WithdrawRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.path

class SellerApiService(private val client: HttpClient) {

    suspend fun login(request: LoginRequest): ApiResponse<User> {
        return client.post("login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getHomeInfo(): ApiResponse<HomeInfo> {
        return client.get("home").body()
    }

    suspend fun getCurrentOrders(
        currentOrders: Boolean = true,
        page: Int? = null
    ): ApiResponse<DeliveryOrdersResponse> {
        return client.get("delivery-orders") {
            url {
                parameters.append("current_orders", currentOrders.toString())
                page?.let { parameters.append("page", it.toString()) }
            }
        }.body()
    }

    suspend fun getPastOrders(page: Int? = null): ApiResponse<DeliveryOrdersResponse> {
        return client.get("delivery-orders") {
            url {
                parameters.append("current_orders", "false")
                page?.let { parameters.append("page", it.toString()) }
            }
        }.body()
    }

    suspend fun cancelOrder(orderId: Int): HttpResponse {
        return client.post("delivery-orders/$orderId/cancel")
    }

    suspend fun createOrder(request: CreateOrderRequest): HttpResponse {
        return client.post("delivery-orders") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun getWalletTransactions(page: Int? = null): ApiResponse<WalletResponse> {
        return client.get("wallet") {
            url {
                page?.let { parameters.append("page", it.toString()) }
            }
        }.body()
    }

    suspend fun getWithdrawalRequests(page: Int? = null): ApiResponse<List<WithdrawRequests>> {
        return client.get("withdrawal-requests") {
            url {
                page?.let { parameters.append("page", it.toString()) }
            }
        }.body()
    }

    suspend fun redeemPoints(request: RedeemPointsRequest): HttpResponse {
        return client.post("wallet/redeem-points") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun requestWithdraw(request: WithdrawRequest): HttpResponse {
        return client.post("withdrawal-requests") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun updateFcmToken(request: FcmTokenRequest): ApiResponse<Unit> {
        return client.post("fcm-token") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun logout(): HttpResponse {
        return client.get("logout")
    }
}