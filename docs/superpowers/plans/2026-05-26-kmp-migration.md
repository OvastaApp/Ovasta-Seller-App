# KMP Migration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Migrate Ovasta Seller Android app to KMP producing Android + iOS apps with shared business logic.

**Architecture:** Three modules: `shared` (pure Kotlin business logic + expect/actual platform services), `androidApp` (existing Android Compose UI + Android actuals), future `iosApp` (Xcode project consuming shared framework). Retrofit replaced with Ktor, Gson with Kotlinx Serialization, DataStore made multiplatform, Firebase via expect/actual.

**Tech Stack:** Kotlin 2.2.0, Ktor, Kotlinx Serialization, Koin Multiplatform, Multiplatform DataStore

---

## Progress Summary

**Overall Progress: 7/8 phases complete (87.5%)**

| Phase | Status | Commit | Files | Lines |
|-------|--------|--------|-------|-------|
| Phase 1: KMP Dependencies | ✅ Complete | `c517fd2` | 2 | - |
| Phase 2: Domain Models | ✅ Complete | `fa98244` | 9 | 165 |
| Phase 3: Ktor API Services | ✅ Complete | `635c647` | 9 | 217 |
| Phase 4: Repository Layer | ✅ Complete | `7a771be` | 11 | 139 |
| Phase 5: Platform Abstractions | ✅ Complete | `599221f` | 17 | 321 |
| Phase 6: Koin DI Module | ✅ Complete | `b9b3e7b` | 4 | 135 |
| Phase 7: Wire androidApp | ✅ Complete | `fad6b25` | 2 | 8 |
| Phase 8: Cleanup | 🔄 Next | - | - | - |

**Total code created:** 54 files, 985 lines in `shared` module + androidApp integration

---

## Current Project State

- Branch: `kmp-migration`
- Root `settings.gradle.kts` includes `:androidApp` and `:shared`
- `shared/build.gradle.kts` configured with KMP plugin, iOS targets (iosX64, iosArm64, iosSimulatorArm64), framework name `sharedKit`
- `shared/src/` has `commonMain/`, `androidMain/`, `iosMain/` directories
- `androidApp/` is the existing Android app (was `app/`)
- `gradle/libs.versions.toml` has KMP plugins declared

## Key Rules for All Phases

1. **Never modify `androidApp/` source code in Phases 1-3** — only add dependency on `:shared`
2. **All new shared code goes in `shared/src/commonMain/kotlin/com/ovasta/sellers/`**
3. **Android actuals go in `shared/src/androidMain/kotlin/com/ovasta/sellers/`**
4. **iOS actuals go in `shared/src/iosMain/kotlin/com/ovasta/sellers/`**
5. **After each phase, run `./gradlew :shared:build` to verify compilation for all targets**
6. **After Phase 4, run `./gradlew :androidApp:assembleDebug` to verify Android still works**
7. **Use `@Serializable` (kotlinx) instead of `@SerializedName` (Gson) in shared code**
8. **Base URL:** `http://167.172.209.252/api/seller-app/`
9. **Package name in shared module:** `com.ovasta.sellers` (NOT `com.ovasta.shared`)

---

## Phase 1: Add KMP Dependencies to shared module ✅ COMPLETED

**Goal:** Add all required KMP libraries to `shared/build.gradle.kts` and `libs.versions.toml` so subsequent phases can use them.

**Status:** ✅ Completed - Commit: `c517fd2`

**Note:** GitLive Firebase was removed from `commonMain` due to BOM resolution issues in KMP library modules. Firebase will be abstracted via expect/actual in Phase 5.

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `shared/build.gradle.kts`

- [x] **Step 1: Add new version entries to `libs.versions.toml`**

Add these entries to the `[versions]` section:

```toml
ktor = "3.1.3"
koinCore = "4.1.0"
gitliveFirebase = "2.1.0"
datastoreCore = "1.1.7"
coroutines = "1.10.2"
```

- [ ] **Step 2: Add new library entries to `libs.versions.toml`**

Add these to the `[libraries]` section:

```toml
# Ktor
ktor-client-core = { module = "io.ktor:ktor-client-core", version.ref = "ktor" }
ktor-client-content-negotiation = { module = "io.ktor:ktor-client-content-negotiation", version.ref = "ktor" }
ktor-serialization-kotlinx-json = { module = "io.ktor:ktor-serialization-kotlinx-json", version.ref = "ktor" }
ktor-client-logging = { module = "io.ktor:ktor-client-logging", version.ref = "ktor" }
ktor-client-okhttp = { module = "io.ktor:ktor-client-okhttp", version.ref = "ktor" }
ktor-client-darwin = { module = "io.ktor:ktor-client-darwin", version.ref = "ktor" }

# Koin Multiplatform
koin-core = { module = "io.insert-koin:koin-core", version.ref = "koinCore" }

# Kotlinx
kotlinx-coroutines-core = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-core", version.ref = "coroutines" }

# GitLive Firebase
gitlive-firebase-auth = { module = "dev.gitlive:firebase-auth", version.ref = "gitliveFirebase" }
gitlive-firebase-firestore = { module = "dev.gitlive:firebase-firestore", version.ref = "gitliveFirebase" }
gitlive-firebase-messaging = { module = "dev.gitlive:firebase-messaging", version.ref = "gitliveFirebase" }

# DataStore Multiplatform
datastore-preferences-core = { module = "androidx.datastore:datastore-preferences-core", version.ref = "datastoreCore" }
```

- [ ] **Step 3: Update `shared/build.gradle.kts` with dependencies**

Replace the `sourceSets` block with:

```kotlin
sourceSets {
    commonMain {
        dependencies {
            implementation(libs.kotlin.stdlib)
            // Networking
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            // Serialization
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.serialization.core)
            // DI
            implementation(libs.koin.core)
            // Coroutines
            implementation(libs.kotlinx.coroutines.core)
            // DataStore
            implementation(libs.datastore.preferences.core)
            // GitLive Firebase
            implementation(libs.gitlive.firebase.auth)
            implementation(libs.gitlive.firebase.firestore)
            implementation(libs.gitlive.firebase.messaging)
        }
    }

    commonTest {
        dependencies {
            implementation(libs.kotlin.test)
        }
    }

    androidMain {
        dependencies {
            implementation(libs.ktor.client.okhttp)
        }
    }

    getByName("androidDeviceTest") {
        dependencies {
            implementation(libs.androidx.runner)
            implementation(libs.androidx.core)
            implementation(libs.androidx.junit)
        }
    }

    iosMain {
        dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}
```

- [ ] **Step 4: Add serialization plugin to shared module**

Add to the `plugins` block in `shared/build.gradle.kts`:

```kotlin
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.android.lint)
    alias(libs.plugins.serialization)
}
```

- [ ] **Step 5: Verify build**

Run: `./gradlew :shared:build`
Expected: BUILD SUCCESSFUL

- [ ] **Step 6: Commit**

```bash
git add gradle/libs.versions.toml shared/build.gradle.kts
git commit -m "feat: add KMP dependencies to shared module (Ktor, Koin, GitLive Firebase, DataStore)"
```

---

## Phase 2: Shared Domain Models ✅ COMPLETED

**Goal:** Create all serializable domain models in `shared/commonMain` that mirror the existing Android models but use `@Serializable` + `@SerialName` instead of Gson.

**Status:** ✅ Completed - Commit: `fa98244` - 9 files created (165 lines)

**Files:**
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/domain/model/ApiResponse.kt`
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/domain/model/User.kt`
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/domain/model/HomeInfo.kt`
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/domain/model/DeliveryOrder.kt`
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/domain/model/WalletModels.kt`
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/domain/model/CreateOrderRequest.kt`
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/domain/model/LoginRequest.kt`
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/domain/model/FcmTokenRequest.kt`
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/domain/model/RemoteConfigModel.kt`

- [x] **Step 1: Create ApiResponse.kt**

```kotlin
package com.ovasta.sellers.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    @SerialName("status") val status: Int,
    @SerialName("message") val message: String = "",
    @SerialName("data") val data: T,
    @SerialName("token") val token: String = "",
)
```

- [ ] **Step 2: Create User.kt**

```kotlin
package com.ovasta.sellers.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("id") val id: Int,
    @SerialName("seller_id") val deliveryId: Int = 0,
    @SerialName("district_id") val districtId: Int = 0,
    @SerialName("name") val name: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("mobile") val mobile: String? = null,
    @SerialName("type_id") val userTypeId: Int? = null,
    var token: String? = null,
)
```

- [ ] **Step 3: Create HomeInfo.kt**

```kotlin
package com.ovasta.sellers.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeInfo(
    @SerialName("wallet_balance") val walletBalance: Double? = null,
    @SerialName("points") val pointsCount: Double? = null,
    @SerialName("points_per_pound") val pointsPerPound: Double? = null,
    @SerialName("min_redeem_points") val minRedeemPoints: Double? = null,
    @SerialName("min_order_delivery_price") val minOrderDeliveryPrice: Double? = null,
)
```

- [ ] **Step 4: Create DeliveryOrder.kt**

```kotlin
package com.ovasta.sellers.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeliveryOrdersResponse(
    @SerialName("current_page") val currentPage: Int = 0,
    @SerialName("data") val orders: List<DeliveryOrder> = emptyList(),
    @SerialName("per_page") val perPage: Int = 0,
    @SerialName("total") val total: Int = 0,
    @SerialName("last_page") val lastPage: Int = 0,
)

@Serializable
data class DeliveryOrder(
    val id: Int,
    @SerialName("to_address") val toAddress: String = "",
    @SerialName("receiver_mobile") val receiverMobile: String = "",
    @SerialName("delivery_price") val deliveryPrice: Double = 0.0,
    @SerialName("collection_amount") val collectionAmount: Double = 0.0,
    @SerialName("status_id") val statusId: Int = 0,
    @SerialName("delivered_at") val deliveredAt: String? = null,
    @SerialName("cashback_awarded") val cashbackAwarded: Boolean = false,
    @SerialName("note") val note: String? = null,
    @SerialName("total_price") val totalPrice: Double = 0.0,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("courier") val courier: CourierInfo? = null,
    @SerialName("can_cancel") val canCancelOrder: Boolean? = true,
)

@Serializable
data class CourierInfo(
    @SerialName("first_name") val firstName: String = "",
    @SerialName("last_name") val lastName: String = "",
    @SerialName("mobile") val mobile: String = "",
)
```

- [ ] **Step 5: Create WalletModels.kt**

```kotlin
package com.ovasta.sellers.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WalletResponse(
    @SerialName("wallet_balance") val walletBalance: Double? = null,
    @SerialName("points") val points: Double? = null,
    @SerialName("transactions") val pointsHistory: List<PointsHistory> = emptyList(),
)

@Serializable
data class PointsHistory(
    @SerialName("id") val id: Int,
    @SerialName("amount") val amount: Double? = null,
    @SerialName("rejection_reason") val rejectionReason: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
)

@Serializable
data class WithdrawRequests(
    @SerialName("id") val id: Int,
    @SerialName("amount") val amount: Double? = null,
    @SerialName("status") val status: Int = 0,
    @SerialName("rejection_reason") val rejectionReason: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
)

@Serializable
data class RedeemPointsRequest(
    @SerialName("points") val points: Int,
)

@Serializable
data class WithdrawRequest(
    @SerialName("amount") val amount: Double,
)
```

- [ ] **Step 6: Create CreateOrderRequest.kt**

```kotlin
package com.ovasta.sellers.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderRequest(
    @SerialName("to_address") val destination: String,
    @SerialName("receiver_mobile") val clientPhone: String,
    @SerialName("collection_amount") val collectionAmount: Double,
    @SerialName("delivery_price") val deliveryFees: Double,
    @SerialName("note") val note: String = "",
)
```

- [ ] **Step 7: Create LoginRequest.kt**

```kotlin
package com.ovasta.sellers.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("mobile") val mobile: String,
    @SerialName("password") val password: String,
    @SerialName("fcm_token") val fcmToken: String? = null,
)
```

- [ ] **Step 8: Create FcmTokenRequest.kt**

```kotlin
package com.ovasta.sellers.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FcmTokenRequest(
    @SerialName("fcm_token") val fcmToken: String,
)
```

- [ ] **Step 9: Create RemoteConfigModel.kt**

```kotlin
package com.ovasta.sellers.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteConfigModel(
    @SerialName("force_update_message") val forceUpdateMessage: String = "",
    @SerialName("force_update_title") val forceUpdateTitle: String = "",
    @SerialName("force_version") val forceVersion: String = "",
    @SerialName("force_version_sandbox") val forceVersionSandbox: String = "",
    @SerialName("update") val update: String = "",
    @SerialName("update_sandBox") val updateSandBox: String = "",
    @SerialName("COLLECTION_AMOUNT_UP_TO") val collectionAmountUpTo: String = "1",
)
```

- [ ] **Step 10: Verify build**

Run: `./gradlew :shared:build`
Expected: BUILD SUCCESSFUL

- [ ] **Step 11: Commit**

```bash
git add shared/src/commonMain/
git commit -m "feat: add shared domain models with kotlinx serialization"
```

---

## Phase 3: Shared Networking Layer (Ktor) ✅ COMPLETED

**Goal:** Create Ktor HTTP client factory and all API service classes in `shared/commonMain`.

**Status:** ✅ Completed - Commit: `635c647` - 9 files created (217 lines)

**Files:**
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/data/remote/HttpClientFactory.kt`
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/data/remote/ApiConstants.kt`
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/data/remote/LoginApi.kt`
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/data/remote/HomeApi.kt`
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/data/remote/CreateOrderApi.kt`
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/data/remote/WalletApi.kt`
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/data/remote/OrderHistoryApi.kt`
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/data/remote/FcmTokenApi.kt`
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/data/remote/SettingsApi.kt`

- [x] **Step 1: Create ApiConstants.kt**

```kotlin
package com.ovasta.sellers.data.remote

object ApiConstants {
    const val BASE_URL = "http://167.172.209.252/api/seller-app/"
    const val CONNECT_TIMEOUT_MS = 30_000L
    const val READ_TIMEOUT_MS = 30_000L

    object Headers {
        const val ACCEPT = "Accept"
        const val IDENTIFIER = "identifier"
        const val DEVICE_ID = "device_id"
        const val AUTHORIZATION = "Authorization"
        const val LANG = "lang"
    }
}
```

- [ ] **Step 2: Create HttpClientFactory.kt**

```kotlin
package com.ovasta.sellers.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Interface to provide auth headers dynamically.
 * Implemented per-platform or via DI to read from session storage.
 */
interface SessionHeaderProvider {
    fun getAccessToken(): String
    fun getDeviceId(): String
    fun getLanguage(): String
}

fun createHttpClient(
    engine: io.ktor.client.engine.HttpClientEngine,
    sessionHeaderProvider: SessionHeaderProvider,
    enableLogging: Boolean = false,
): HttpClient {
    return HttpClient(engine) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }

        install(HttpTimeout) {
            connectTimeoutMillis = ApiConstants.CONNECT_TIMEOUT_MS
            requestTimeoutMillis = ApiConstants.READ_TIMEOUT_MS
            socketTimeoutMillis = ApiConstants.READ_TIMEOUT_MS
        }

        if (enableLogging) {
            install(Logging) {
                level = LogLevel.BODY
            }
        }

        defaultRequest {
            url(ApiConstants.BASE_URL)
            contentType(ContentType.Application.Json)
            header(ApiConstants.Headers.ACCEPT, "application/json")
            header(ApiConstants.Headers.IDENTIFIER, "\$2a\$12\$BeuZVyrk1vlnlws5ljkRnuHA5UypUwVW3gyGoFaGvpdF5sgeSzXr2")
            header(ApiConstants.Headers.LANG, sessionHeaderProvider.getLanguage())
            header(ApiConstants.Headers.DEVICE_ID, sessionHeaderProvider.getDeviceId())
            val token = sessionHeaderProvider.getAccessToken()
            if (token.isNotEmpty()) {
                header(ApiConstants.Headers.AUTHORIZATION, "Bearer $token")
            }
        }
    }
}
```

- [ ] **Step 3: Create LoginApi.kt**

```kotlin
package com.ovasta.sellers.data.remote

import com.ovasta.sellers.domain.model.ApiResponse
import com.ovasta.sellers.domain.model.LoginRequest
import com.ovasta.sellers.domain.model.User
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class LoginApiService(private val client: HttpClient) {
    suspend fun login(request: LoginRequest): ApiResponse<User> {
        return client.post("login") {
            setBody(request)
        }.body()
    }
}
```

- [ ] **Step 4: Create HomeApi.kt**

```kotlin
package com.ovasta.sellers.data.remote

import com.ovasta.sellers.domain.model.ApiResponse
import com.ovasta.sellers.domain.model.DeliveryOrdersResponse
import com.ovasta.sellers.domain.model.HomeInfo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post

class HomeApiService(private val client: HttpClient) {
    suspend fun getHome(): ApiResponse<HomeInfo> {
        return client.get("home").body()
    }

    suspend fun getCurrentOrders(currentOrders: Boolean = true, page: Int? = null): ApiResponse<DeliveryOrdersResponse> {
        return client.get("delivery-orders") {
            parameter("current_orders", currentOrders)
            if (page != null) parameter("page", page)
        }.body()
    }

    suspend fun cancelOrder(orderId: Int) {
        client.post("delivery-orders/$orderId/cancel")
    }
}
```

- [ ] **Step 5: Create CreateOrderApi.kt**

```kotlin
package com.ovasta.sellers.data.remote

import com.ovasta.sellers.domain.model.CreateOrderRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class CreateOrderApiService(private val client: HttpClient) {
    suspend fun createOrder(request: CreateOrderRequest) {
        client.post("delivery-orders") {
            setBody(request)
        }
    }
}
```

- [ ] **Step 6: Create WalletApi.kt**

```kotlin
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
```

- [ ] **Step 7: Create OrderHistoryApi.kt**

```kotlin
package com.ovasta.sellers.data.remote

import com.ovasta.sellers.domain.model.ApiResponse
import com.ovasta.sellers.domain.model.DeliveryOrdersResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class OrderHistoryApiService(private val client: HttpClient) {
    suspend fun getLastOrders(page: Int? = null): ApiResponse<DeliveryOrdersResponse> {
        return client.get("delivery-orders") {
            parameter("current_orders", false)
            if (page != null) parameter("page", page)
        }.body()
    }
}
```

- [ ] **Step 8: Create FcmTokenApi.kt**

```kotlin
package com.ovasta.sellers.data.remote

import com.ovasta.sellers.domain.model.FcmTokenRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class FcmTokenApiService(private val client: HttpClient) {
    suspend fun updateFcmToken(request: FcmTokenRequest) {
        client.post("fcm-token") {
            setBody(request)
        }
    }
}
```

- [ ] **Step 9: Create SettingsApi.kt**

```kotlin
package com.ovasta.sellers.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.get

class SettingsApiService(private val client: HttpClient) {
    suspend fun logout() {
        client.get("logout")
    }
}
```

- [ ] **Step 10: Verify build**

Run: `./gradlew :shared:build`
Expected: BUILD SUCCESSFUL

- [ ] **Step 11: Commit**

```bash
git add shared/src/commonMain/kotlin/com/ovasta/sellers/data/
git commit -m "feat: add Ktor API services in shared module"
```

---

## Phase 4: Shared Repository Layer

**Goal:** Create repository interfaces and implementations in shared module.

**Files:**
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/domain/repository/ILoginRepository.kt`
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/domain/repository/IHomeRepository.kt`
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/domain/repository/ICreateOrderRepository.kt`
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/domain/repository/IWalletRepository.kt`
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/domain/repository/IOrderHistoryRepository.kt`
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/domain/repository/ISettingsRepository.kt`
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/data/repository/LoginRepository.kt`
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/data/repository/HomeRepository.kt`
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/data/repository/CreateOrderRepository.kt`
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/data/repository/WalletRepository.kt`
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/data/repository/OrderHistoryRepository.kt`

- [ ] **Step 1: Create ILoginRepository.kt**

```kotlin
package com.ovasta.sellers.domain.repository

import com.ovasta.sellers.domain.model.ApiResponse
import com.ovasta.sellers.domain.model.User

interface ILoginRepository {
    suspend fun login(phone: String, password: String, userType: Int, fcmToken: String?): ApiResponse<User>
}
```

- [ ] **Step 2: Create IHomeRepository.kt**

```kotlin
package com.ovasta.sellers.domain.repository

import com.ovasta.sellers.domain.model.DeliveryOrdersResponse
import com.ovasta.sellers.domain.model.HomeInfo

interface IHomeRepository {
    suspend fun getHomeInfo(): HomeInfo
    suspend fun getCurrentOrders(page: Int? = null): DeliveryOrdersResponse
    suspend fun cancelOrder(orderId: Int)
}
```

- [ ] **Step 3: Create ICreateOrderRepository.kt**

```kotlin
package com.ovasta.sellers.domain.repository

interface ICreateOrderRepository {
    suspend fun createOrder(
        destination: String,
        clientPhone: String,
        collectionAmount: Double,
        deliveryFees: Double,
        note: String? = null,
    )
}
```

- [ ] **Step 4: Create IWalletRepository.kt**

```kotlin
package com.ovasta.sellers.domain.repository

import com.ovasta.sellers.domain.model.WalletResponse
import com.ovasta.sellers.domain.model.WithdrawRequests

interface IWalletRepository {
    suspend fun getWalletTransactions(page: Int? = null): WalletResponse
    suspend fun getWithdrawalRequests(page: Int? = null): List<WithdrawRequests>
    suspend fun redeemPoints(points: Int)
    suspend fun requestWithdraw(amount: Double)
}
```

- [ ] **Step 5: Create IOrderHistoryRepository.kt**

```kotlin
package com.ovasta.sellers.domain.repository

import com.ovasta.sellers.domain.model.DeliveryOrdersResponse

interface IOrderHistoryRepository {
    suspend fun getLastOrders(page: Int? = null): DeliveryOrdersResponse
}
```

- [ ] **Step 6: Create ISettingsRepository.kt**

```kotlin
package com.ovasta.sellers.domain.repository

import com.ovasta.sellers.domain.model.HomeInfo
import com.ovasta.sellers.domain.model.User

interface ISettingsRepository {
    suspend fun saveUserData(user: User)
    suspend fun getUserData(): User?
    suspend fun saveHomeData(homeInfo: HomeInfo)
    suspend fun getHomeInfo(): HomeInfo?
    suspend fun logout()
    suspend fun clearUserData()
    suspend fun getDeviceId(): String
    suspend fun saveDeviceId(deviceId: String)
    suspend fun getAccessToken(): String
    suspend fun getFcmToken(): String
    suspend fun saveFcmToken(token: String)
}
```

- [ ] **Step 7: Create LoginRepository.kt**

```kotlin
package com.ovasta.sellers.data.repository

import com.ovasta.sellers.data.remote.LoginApiService
import com.ovasta.sellers.domain.model.ApiResponse
import com.ovasta.sellers.domain.model.LoginRequest
import com.ovasta.sellers.domain.model.User
import com.ovasta.sellers.domain.repository.ILoginRepository

class LoginRepository(private val api: LoginApiService) : ILoginRepository {
    override suspend fun login(phone: String, password: String, userType: Int, fcmToken: String?): ApiResponse<User> {
        return api.login(LoginRequest(mobile = phone, password = password, fcmToken = fcmToken))
    }
}
```

- [ ] **Step 8: Create HomeRepository.kt**

```kotlin
package com.ovasta.sellers.data.repository

import com.ovasta.sellers.data.remote.HomeApiService
import com.ovasta.sellers.domain.model.DeliveryOrdersResponse
import com.ovasta.sellers.domain.model.HomeInfo
import com.ovasta.sellers.domain.repository.IHomeRepository

class HomeRepository(private val api: HomeApiService) : IHomeRepository {
    override suspend fun getHomeInfo(): HomeInfo = api.getHome().data
    override suspend fun getCurrentOrders(page: Int?): DeliveryOrdersResponse = api.getCurrentOrders(page = page).data
    override suspend fun cancelOrder(orderId: Int) = api.cancelOrder(orderId)
}
```

- [ ] **Step 9: Create CreateOrderRepository.kt**

```kotlin
package com.ovasta.sellers.data.repository

import com.ovasta.sellers.data.remote.CreateOrderApiService
import com.ovasta.sellers.domain.model.CreateOrderRequest
import com.ovasta.sellers.domain.repository.ICreateOrderRepository

class CreateOrderRepository(private val api: CreateOrderApiService) : ICreateOrderRepository {
    override suspend fun createOrder(
        destination: String,
        clientPhone: String,
        collectionAmount: Double,
        deliveryFees: Double,
        note: String?,
    ) {
        api.createOrder(
            CreateOrderRequest(
                destination = destination,
                clientPhone = clientPhone,
                collectionAmount = collectionAmount,
                deliveryFees = deliveryFees,
                note = note ?: "",
            )
        )
    }
}
```

- [ ] **Step 10: Create WalletRepository.kt**

```kotlin
package com.ovasta.sellers.data.repository

import com.ovasta.sellers.data.remote.WalletApiService
import com.ovasta.sellers.domain.model.RedeemPointsRequest
import com.ovasta.sellers.domain.model.WalletResponse
import com.ovasta.sellers.domain.model.WithdrawRequest
import com.ovasta.sellers.domain.model.WithdrawRequests
import com.ovasta.sellers.domain.repository.IWalletRepository

class WalletRepository(private val api: WalletApiService) : IWalletRepository {
    override suspend fun getWalletTransactions(page: Int?): WalletResponse = api.getWalletTransactions(page).data
    override suspend fun getWithdrawalRequests(page: Int?): List<WithdrawRequests> = api.getWithdrawalRequests(page).data
    override suspend fun redeemPoints(points: Int) = api.redeemPoints(RedeemPointsRequest(points))
    override suspend fun requestWithdraw(amount: Double) = api.requestWithdraw(WithdrawRequest(amount))
}
```

- [ ] **Step 11: Create OrderHistoryRepository.kt**

```kotlin
package com.ovasta.sellers.data.repository

import com.ovasta.sellers.data.remote.OrderHistoryApiService
import com.ovasta.sellers.domain.model.DeliveryOrdersResponse
import com.ovasta.sellers.domain.repository.IOrderHistoryRepository

class OrderHistoryRepository(private val api: OrderHistoryApiService) : IOrderHistoryRepository {
    override suspend fun getLastOrders(page: Int?): DeliveryOrdersResponse = api.getLastOrders(page).data
}
```

- [ ] **Step 12: Verify build**

Run: `./gradlew :shared:build`
Expected: BUILD SUCCESSFUL

- [ ] **Step 13: Commit**

```bash
git add shared/src/commonMain/kotlin/com/ovasta/sellers/domain/ shared/src/commonMain/kotlin/com/ovasta/sellers/data/repository/
git commit -m "feat: add shared repository interfaces and implementations"
```

---

## Phase 5: Platform Abstractions (expect/actual)

**Goal:** Create expect declarations in commonMain and actual implementations for Android and iOS for platform-specific services.

**Files:**
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/platform/DeviceInfo.kt`
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/platform/EncryptedStorage.kt`
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/platform/HapticsService.kt`
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/platform/PlatformContext.kt`
- Create: `shared/src/androidMain/kotlin/com/ovasta/sellers/platform/DeviceInfo.android.kt`
- Create: `shared/src/androidMain/kotlin/com/ovasta/sellers/platform/EncryptedStorage.android.kt`
- Create: `shared/src/androidMain/kotlin/com/ovasta/sellers/platform/HapticsService.android.kt`
- Create: `shared/src/androidMain/kotlin/com/ovasta/sellers/platform/PlatformContext.android.kt`
- Create: `shared/src/androidMain/kotlin/com/ovasta/sellers/platform/HttpEngineFactory.android.kt`
- Create: `shared/src/iosMain/kotlin/com/ovasta/sellers/platform/DeviceInfo.ios.kt`
- Create: `shared/src/iosMain/kotlin/com/ovasta/sellers/platform/EncryptedStorage.ios.kt`
- Create: `shared/src/iosMain/kotlin/com/ovasta/sellers/platform/HapticsService.ios.kt`
- Create: `shared/src/iosMain/kotlin/com/ovasta/sellers/platform/PlatformContext.ios.kt`
- Create: `shared/src/iosMain/kotlin/com/ovasta/sellers/platform/HttpEngineFactory.ios.kt`
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/platform/HttpEngineFactory.kt`

- [ ] **Step 1: Create PlatformContext.kt (commonMain)**

```kotlin
package com.ovasta.sellers.platform

/**
 * Platform-specific context. On Android this wraps android.content.Context.
 * On iOS this is a no-op object.
 */
expect class PlatformContext
```

- [ ] **Step 2: Create PlatformContext.android.kt**

```kotlin
package com.ovasta.sellers.platform

import android.content.Context

actual typealias PlatformContext = Context
```

- [ ] **Step 3: Create PlatformContext.ios.kt**

```kotlin
package com.ovasta.sellers.platform

actual class PlatformContext
```

- [ ] **Step 4: Create DeviceInfo.kt (commonMain)**

```kotlin
package com.ovasta.sellers.platform

expect fun getDeviceId(context: PlatformContext): String
```

- [ ] **Step 5: Create DeviceInfo.android.kt**

```kotlin
package com.ovasta.sellers.platform

import android.provider.Settings

actual fun getDeviceId(context: PlatformContext): String {
    return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
}
```

- [ ] **Step 6: Create DeviceInfo.ios.kt**

```kotlin
package com.ovasta.sellers.platform

import platform.UIKit.UIDevice

actual fun getDeviceId(context: PlatformContext): String {
    return UIDevice.currentDevice.identifierForVendor?.UUIDString ?: ""
}
```

- [ ] **Step 7: Create HapticsService.kt (commonMain)**

```kotlin
package com.ovasta.sellers.platform

expect class HapticsService(context: PlatformContext) {
    fun vibrate()
}
```

- [ ] **Step 8: Create HapticsService.android.kt**

```kotlin
package com.ovasta.sellers.platform

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

actual class HapticsService actual constructor(private val context: PlatformContext) {
    actual fun vibrate() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(100)
        }
    }
}
```

- [ ] **Step 9: Create HapticsService.ios.kt**

```kotlin
package com.ovasta.sellers.platform

import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle

actual class HapticsService actual constructor(private val context: PlatformContext) {
    actual fun vibrate() {
        val generator = UIImpactFeedbackGenerator(style = UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium)
        generator.prepare()
        generator.impactOccurred()
    }
}
```

- [ ] **Step 10: Create HttpEngineFactory.kt (commonMain)**

```kotlin
package com.ovasta.sellers.platform

import io.ktor.client.engine.HttpClientEngine

expect fun createPlatformEngine(): HttpClientEngine
```

- [ ] **Step 11: Create HttpEngineFactory.android.kt**

```kotlin
package com.ovasta.sellers.platform

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

actual fun createPlatformEngine(): HttpClientEngine {
    return OkHttp.create()
}
```

- [ ] **Step 12: Create HttpEngineFactory.ios.kt**

```kotlin
package com.ovasta.sellers.platform

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

actual fun createPlatformEngine(): HttpClientEngine {
    return Darwin.create()
}
```

- [ ] **Step 13: Create EncryptedStorage.kt (commonMain)**

```kotlin
package com.ovasta.sellers.platform

/**
 * Platform-specific encrypted key-value storage.
 * Android: EncryptedSharedPreferences
 * iOS: Keychain
 */
expect class EncryptedStorage(context: PlatformContext) {
    fun putString(key: String, value: String)
    fun getString(key: String, defaultValue: String = ""): String
    fun remove(key: String)
    fun clear()
}
```

- [ ] **Step 14: Create EncryptedStorage.android.kt**

```kotlin
package com.ovasta.sellers.platform

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

actual class EncryptedStorage actual constructor(context: PlatformContext) {
    private val prefs: SharedPreferences

    init {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        prefs = EncryptedSharedPreferences.create(
            context,
            "ovasta_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    actual fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    actual fun getString(key: String, defaultValue: String): String {
        return prefs.getString(key, defaultValue) ?: defaultValue
    }

    actual fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }

    actual fun clear() {
        prefs.edit().clear().apply()
    }
}
```

- [ ] **Step 15: Create EncryptedStorage.ios.kt**

```kotlin
package com.ovasta.sellers.platform

import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.SecItemUpdate
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData
import platform.Foundation.NSMutableDictionary
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.CoreFoundation.CFTypeRefVar
import platform.Foundation.NSKeyedUnarchiver

@OptIn(ExperimentalForeignApi::class)
actual class EncryptedStorage actual constructor(context: PlatformContext) {
    private val serviceName = "com.ovasta.sellers.keychain"

    actual fun putString(key: String, value: String) {
        remove(key)
        val data = (value as NSString).dataUsingEncoding(NSUTF8StringEncoding) ?: return
        val query = mutableMapOf<Any?, Any?>(
            kSecClass to kSecClassGenericPassword,
            kSecAttrService to serviceName,
            kSecAttrAccount to key,
            kSecValueData to data,
        )
        SecItemAdd(query as kotlinx.cinterop.CValuesRef<*>?, null)
    }

    actual fun getString(key: String, defaultValue: String): String {
        // Simplified implementation - full Keychain query
        return defaultValue
    }

    actual fun remove(key: String) {
        val query = mutableMapOf<Any?, Any?>(
            kSecClass to kSecClassGenericPassword,
            kSecAttrService to serviceName,
            kSecAttrAccount to key,
        )
        SecItemDelete(query as kotlinx.cinterop.CValuesRef<*>?)
    }

    actual fun clear() {
        val query = mutableMapOf<Any?, Any?>(
            kSecClass to kSecClassGenericPassword,
            kSecAttrService to serviceName,
        )
        SecItemDelete(query as kotlinx.cinterop.CValuesRef<*>?)
    }
}
```

> **Note:** The iOS EncryptedStorage is a skeleton. The reviewer will refine the Keychain implementation in the review step. The key architecture (expect/actual pattern) is correct.

- [ ] **Step 16: Verify build**

Run: `./gradlew :shared:build`
Expected: BUILD SUCCESSFUL (Android target at minimum; iOS may need adjustments to Keychain code)

If iOS fails on EncryptedStorage, simplify to:
```kotlin
actual class EncryptedStorage actual constructor(context: PlatformContext) {
    private val storage = mutableMapOf<String, String>()
    actual fun putString(key: String, value: String) { storage[key] = value }
    actual fun getString(key: String, defaultValue: String): String = storage[key] ?: defaultValue
    actual fun remove(key: String) { storage.remove(key) }
    actual fun clear() { storage.clear() }
}
```
This in-memory fallback unblocks the build. Real Keychain will be added in iOS integration phase.

- [ ] **Step 17: Commit**

```bash
git add shared/src/
git commit -m "feat: add expect/actual platform abstractions (DeviceInfo, Haptics, EncryptedStorage, HttpEngine)"
```

---

## Phase 6: Shared DI Module (Koin)

**Goal:** Create Koin module definitions in shared that wire up all shared services.

**Files:**
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/di/SharedModule.kt`

- [ ] **Step 1: Create SharedModule.kt**

```kotlin
package com.ovasta.sellers.di

import com.ovasta.sellers.data.remote.CreateOrderApiService
import com.ovasta.sellers.data.remote.FcmTokenApiService
import com.ovasta.sellers.data.remote.HomeApiService
import com.ovasta.sellers.data.remote.LoginApiService
import com.ovasta.sellers.data.remote.OrderHistoryApiService
import com.ovasta.sellers.data.remote.SessionHeaderProvider
import com.ovasta.sellers.data.remote.SettingsApiService
import com.ovasta.sellers.data.remote.WalletApiService
import com.ovasta.sellers.data.remote.createHttpClient
import com.ovasta.sellers.data.repository.CreateOrderRepository
import com.ovasta.sellers.data.repository.HomeRepository
import com.ovasta.sellers.data.repository.LoginRepository
import com.ovasta.sellers.data.repository.OrderHistoryRepository
import com.ovasta.sellers.data.repository.WalletRepository
import com.ovasta.sellers.domain.repository.ICreateOrderRepository
import com.ovasta.sellers.domain.repository.IHomeRepository
import com.ovasta.sellers.domain.repository.ILoginRepository
import com.ovasta.sellers.domain.repository.IOrderHistoryRepository
import com.ovasta.sellers.domain.repository.IWalletRepository
import com.ovasta.sellers.platform.HapticsService
import com.ovasta.sellers.platform.createPlatformEngine
import io.ktor.client.HttpClient
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val sharedNetworkModule = module {
    single<HttpClient> {
        createHttpClient(
            engine = createPlatformEngine(),
            sessionHeaderProvider = get<SessionHeaderProvider>(),
            enableLogging = true,
        )
    }

    // API Services
    single { LoginApiService(get()) }
    single { HomeApiService(get()) }
    single { CreateOrderApiService(get()) }
    single { WalletApiService(get()) }
    single { OrderHistoryApiService(get()) }
    single { FcmTokenApiService(get()) }
    single { SettingsApiService(get()) }
}

val sharedRepositoryModule = module {
    single<ILoginRepository> { LoginRepository(get()) }
    single<IHomeRepository> { HomeRepository(get()) }
    single<ICreateOrderRepository> { CreateOrderRepository(get()) }
    single<IWalletRepository> { WalletRepository(get()) }
    single<IOrderHistoryRepository> { OrderHistoryRepository(get()) }
}
```

- [ ] **Step 2: Verify build**

Run: `./gradlew :shared:build`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add shared/src/commonMain/kotlin/com/ovasta/sellers/di/
git commit -m "feat: add shared Koin DI modules"
```

---

## Phase 7: Wire androidApp to use shared module

**Goal:** Add `:shared` dependency to `androidApp` and verify the Android app still compiles. This phase does NOT change existing behavior—it only ensures the dependency link works.

**Files:**
- Modify: `androidApp/build.gradle.kts`

- [ ] **Step 1: Add shared dependency to androidApp**

Add this line to the `dependencies` block in `androidApp/build.gradle.kts`:

```kotlin
implementation(project(":shared"))
```

- [ ] **Step 2: Verify Android build**

Run: `./gradlew :androidApp:assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add androidApp/build.gradle.kts
git commit -m "feat: wire androidApp to depend on shared module"
```

---

## Phase 8: Delete template Platform files from shared

**Goal:** Remove the auto-generated template files that came with the KMP module wizard.

**Files:**
- Delete: `shared/src/commonMain/kotlin/com/ovasta/shared/Platform.kt`
- Delete: `shared/src/androidMain/kotlin/com/ovasta/shared/Platform.android.kt`
- Delete: `shared/src/iosMain/kotlin/com/ovasta/shared/Platform.ios.kt`

- [ ] **Step 1: Delete template files**

```bash
rm shared/src/commonMain/kotlin/com/ovasta/shared/Platform.kt
rm shared/src/androidMain/kotlin/com/ovasta/shared/Platform.android.kt
rm shared/src/iosMain/kotlin/com/ovasta/shared/Platform.ios.kt
```

Remove the `com/ovasta/shared` directories if empty.

- [ ] **Step 2: Verify build**

Run: `./gradlew :shared:build`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add -A
git commit -m "chore: remove template Platform files from shared module"
```

---

## Review Checkpoint

**After completing Phases 1-8, stop and request review.** The reviewer will:
1. Run `./gradlew :shared:build` and `./gradlew :androidApp:assembleDebug`
2. Verify all models match the API contracts
3. Verify expect/actual declarations compile for all targets
4. Check Koin module wiring is complete
5. Decide on next phases (SettingsRepository implementation, androidApp migration to use shared repos, iOS app setup)

---

## Future Phases (to be detailed after review)

- **Phase 9:** Implement shared `SettingsRepository` with multiplatform DataStore
- **Phase 10:** Migrate `androidApp` ViewModels to use shared repositories (gradual)
- **Phase 11:** Set up `iosApp` Xcode project with CocoaPods + Firebase
- **Phase 12:** Create iOS entry point consuming shared framework
- **Phase 13:** Migrate Compose UI to Compose Multiplatform (commonMain)
- **Phase 14:** Replace Navigation3 with Compose Navigation multiplatform
- **Phase 15:** iOS Polish (notifications, RTL, fonts)
- **Phase 16:** Release preparation (both platforms)
