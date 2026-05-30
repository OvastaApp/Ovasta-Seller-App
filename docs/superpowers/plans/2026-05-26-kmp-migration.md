# KMP Migration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Migrate Ovasta Seller Android app to KMP producing Android + iOS apps with shared business logic, preserving exact Android UX and matching iOS as closely as possible.

**Architecture:** Three modules: `shared` (pure Kotlin business logic + expect/actual platform services), `androidApp` (existing Android Compose UI + Android actuals), `iosApp` (iOS wrapper for shared Compose UI). Retrofit replaced with Ktor, Gson with Kotlinx Serialization, DataStore made multiplatform, Firebase via expect/actual.

**UI Strategy:** ⭐ **Compose Multiplatform** - Same Compose screens run on both Android and iOS (NOT SwiftUI). This saves 1-2 days and reuses 100% of existing UI code.

**Tech Stack:** Kotlin 2.2.0, Ktor, Kotlinx Serialization, Koin Multiplatform, Multiplatform DataStore, Compose Multiplatform

---

## Progress Summary

**Overall Progress: 13/14 phases complete (93%) — Phase 14 (Release Prep) is next**

| Phase | Status | Commit | Description |
|-------|--------|--------|-------------|
| Phase 1: KMP Dependencies | ✅ Complete | `c517fd2` | Added Ktor, Koin, DataStore, Serialization |
| Phase 2: Domain Models | ✅ Complete | `fa98244` | 9 domain models (165 lines) |
| Phase 3: Ktor API Services | ✅ Complete | `635c647` | 9 API services (217 lines) |
| Phase 4: Repository Layer | ✅ Complete | `7a771be` | 6 interfaces + 5 implementations (139 lines) |
| Phase 5: Platform Abstractions | ✅ Complete | `599221f` | 7 expect/actual + SettingsRepository (321 lines) |
| Phase 6: Koin DI Module | ✅ Complete | `b9b3e7b` | Shared + Android platform modules (135 lines) |
| Phase 7: Wire androidApp | ✅ Complete | `fad6b25` | Added shared dep + Koin init |
| Phase 8: Cleanup | ✅ Complete | `817ff08` + `874c03c` | Removed 17 duplicates + fixed 9 imports |
| Phase 9: Fix androidApp build | ✅ Complete | `fae379e` | Wired all ViewModels/DI to shared repos |
| Phase 10: Template cleanup | ✅ Complete | `6400546` | Deleted Platform.kt + 11 duplicate models |
| Phase 11: iOS actuals | ✅ Complete | `edd2c10` | iOS platform providers (CI verified) |
| Phase 12: Compose MP setup | ✅ Complete | `25896c8` | Compose MP plugin + iosApp structure (CI verified) |
| Phase 13: Migrate UI to Compose MP | ✅ Complete | see sub-phases | All UI screens, VMs, nav, theme in shared module |
| Phase 14: Release Prep | ⏳ Pending | - | Both platforms ready for production |

### Phase 13 Sub-phases (ALL COMPLETE):
| Sub-phase | Commit | Description |
|-----------|--------|-------------|
| 13A: UI Foundation | `multiple` | Colors, Type, AppTheme, BaseViewModel, BaseScreen, Navigator, NavHost, resources (47 drawables, 8 fonts, EN+AR strings) |
| 13B: ViewModels | `multiple` | 7 ViewModels ported + Koin DI registration |
| 13C: Screens | `cd296ad` | 7 screens + App.kt + BaseDialog + OrderSteps/TransactionsSteps + platform actions |
| 13D: Wire Android | `fae9b87` | MainActivity calls shared App() |
| 13E: Cross-platform polish | `f547639` | RTL support, iOS entry point with Koin init |

**Current state:**
- `shared` module builds with Compose Multiplatform for Android + iOS ✅
- `androidApp` builds successfully and uses shared App() ✅  
- `iosApp` builds on macOS CI via xcodegen + framework linking ✅
- iOS CI workflow (`ios-build.yml`) passes ✅
- All UI is in shared module (no more Android-only UI dependency) ✅

**Total shared code created:** ~70+ files, ~3200+ lines  
**Code deleted (duplicates):** 28 files, ~500+ lines  
**Net improvement:** Cleaner architecture, single source of truth, cross-platform UI

---

## ⏱️ Revised Timeline with Compose Multiplatform

### Phases 1-13: COMPLETE ✅

### Phase 14: Release Prep → 1 day
- **Total remaining: ~1 day**

### What's Already Working:
1. ✅ **Shared framework builds for iOS** — CI verified
2. ✅ **iosApp links and builds** — xcodegen + xcodebuild in CI
3. ✅ **Compose Multiplatform plugin active** — compose.runtime/foundation/material3/ui in shared
4. ✅ **ViewModels already use shared repos** — No business logic changes needed
5. ✅ **Same technology** — Compose on both platforms

---

## Current Project State

- Branch: `kmp-migration`
- Root `settings.gradle.kts` includes `:androidApp` and `:shared`
- `shared/build.gradle.kts` configured with: KMP plugin, Compose Multiplatform plugin (`1.8.0`), Kotlin Compose compiler, iOS targets (iosX64, iosArm64, iosSimulatorArm64), framework name `sharedKit`
- `shared/src/` has `commonMain/`, `androidMain/`, `iosMain/` directories
- `shared/src/iosMain/kotlin/com/ovasta/sellers/Main.ios.kt` — iOS Compose entry point (placeholder)
- `androidApp/` is the existing Android app (was `app/`)
- `iosApp/` has Swift wrapper, xcodegen `project.yml`, Podfile, Info.plist
- `gradle/libs.versions.toml` has all KMP + Compose MP plugins declared
- CI: `ios-build.yml` builds framework + generates Xcode project + builds iosApp (no signing)
- CI: `ios-testflight-manual.yml` for production deployment (requires secrets)

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

## Review Checkpoint (Phases 1-8)

**Status: ✅ PASSED**

Results:
1. ✅ `./gradlew :shared:build` — BUILD SUCCESSFUL
2. ✅ All models match API contracts (kotlinx.serialization with @SerialName)
3. ✅ expect/actual declarations compile for Android target (iOS not built on Windows)
4. ✅ Koin module wiring complete (shared + platform modules)
5. ⚠️ `./gradlew :androidApp:assembleDebug` — FAILS (expected: duplicate code removed but imports not yet updated)

**Decision:** Proceed with Phase 9 to fix androidApp build, then continue to iOS.

---

## Phase 9: Fix androidApp Build (Wire ViewModels to Shared Repositories) ✅ COMPLETED

**Goal:** Fix all compilation errors in androidApp after Phase 8 cleanup. Update ViewModels, DI modules, and data sources to use shared module's repositories and models.

**Status:** ✅ Completed - Commit: `fae379e` - 32 files changed, 68 insertions(+), 211 deletions(-)

**What was done:**
- Deleted 8 RemoteDataSource files (Home, CreateOrder, Wallet, OrderHistory)
- Updated 4 DI modules to inject shared repositories
- Updated 4 ViewModels to use `domain.repository.I*Repository`
- Migrated 4 ViewStates to use shared domain models
- Fixed Settings infrastructure to use shared HomeInfo
- Updated UI components to use shared models
- Fixed smart cast errors with explicit null checks

**Verification:** `./gradlew :androidApp:assembleDebug` — BUILD SUCCESSFUL ✅

---

## Phase 10: Template Cleanup & Final Android Polish ✅ COMPLETED

**Goal:** Remove template files and unused legacy code.

**Status:** ✅ Completed - Commit: `6400546` - 15 files changed, 4 insertions(+), 144 deletions(-)

**What was done:**
- Deleted KMP template files: Platform.kt (shared, androidMain)
- Deleted 11 duplicate model files (HomeInfo, DeliveryOrderModels, WalletTransactions, etc.)
- Kept androidApp-specific: Firebase models, TransactionsSteps UI enum
- Updated Profile module imports to use shared models
- Cleaned up empty directories

**Verification:** 
- `./gradlew :shared:build` — BUILD SUCCESSFUL ✅
- `./gradlew :androidApp:assembleDebug` — BUILD SUCCESSFUL ✅

---

## Phase 11: iOS Platform Implementations (expect actuals)

**Goal:** Create iOS actual implementations so `shared` compiles for iOS targets.

**Files to create in `shared/src/iosMain/kotlin/com/ovasta/sellers/data/platform/`:**

- [ ] **Step 1:** `SecureStorage.ios.kt` — iOS Keychain wrapper using `platform.Security` APIs
- [ ] **Step 2:** `DataStoreProvider.ios.kt` — DataStore with file path from `NSDocumentDirectory`
- [ ] **Step 3:** `FirebaseAuthProvider.ios.kt` — GitLive Firebase Auth or stub
- [ ] **Step 4:** `FirestoreProvider.ios.kt` — GitLive Firebase Firestore or stub
- [ ] **Step 5:** `FirebaseMessagingProvider.ios.kt` — GitLive Firebase Messaging or stub
- [ ] **Step 6:** `DeviceInfoProvider.ios.kt` — UUID stored in Keychain
- [ ] **Step 7:** `HapticFeedback.ios.kt` — UIImpactFeedbackGenerator

**Files to create in `shared/src/iosMain/kotlin/com/ovasta/sellers/data/remote/`:**

- [ ] **Step 8:** `HttpClientEngine.ios.kt` — Darwin engine

**Files to create in `shared/src/iosMain/kotlin/com/ovasta/sellers/di/`:**

- [ ] **Step 9:** `PlatformModule.ios.kt` — iOS Koin module providing all platform services

**Verification:**
- [ ] **Step 10:** Build on macOS: `./gradlew :shared:iosSimulatorArm64MainKotlinMetadata`
  - Note: iOS targets can only compile on macOS. Skip on Windows.
- [ ] **Step 11:** Commit

---

## Phase 12: Setup Compose Multiplatform for iOS ⭐ REVISED

**Goal:** Enable Compose Multiplatform so the same Compose UI code runs on both Android and iOS.

**Why Compose MP instead of SwiftUI:** 
- ✅ Reuse 100% of existing Compose screens (already built!)
- ✅ ViewModels already inject shared repositories
- ✅ Single UI codebase for both platforms
- ✅ Faster development (2-3 days vs 3-4 days for SwiftUI)
- ✅ No need to learn SwiftUI

**Prerequisites:** macOS with Xcode installed, Apple Developer account ready.

### Part A: Enable Compose Multiplatform in shared module

- [ ] **Step 1:** Add Compose Multiplatform plugin to `shared/build.gradle.kts`:
  ```kotlin
  plugins {
      alias(libs.plugins.kotlin.multiplatform)
      alias(libs.plugins.android.kotlin.multiplatform.library)
      alias(libs.plugins.compose.compiler) // Add this
      alias(libs.plugins.serialization)
  }
  ```

- [ ] **Step 2:** Add Compose Multiplatform version to `gradle/libs.versions.toml`:
  ```toml
  [versions]
  compose-multiplatform = "1.7.1"
  
  [plugins]
  compose-compiler = { id = "org.jetbrains.compose", version.ref = "compose-multiplatform" }
  ```

- [ ] **Step 3:** Add Compose dependencies to `shared/build.gradle.kts` commonMain:
  ```kotlin
  commonMain {
      dependencies {
          // Existing dependencies...
          
          // Compose Multiplatform
          implementation(compose.runtime)
          implementation(compose.foundation)
          implementation(compose.material3)
          implementation(compose.ui)
          implementation(compose.components.resources)
          implementation(compose.components.uiToolingPreview)
          
          // Navigation (multiplatform)
          implementation("cafe.adriel.voyager:voyager-navigator:1.0.0")
          implementation("cafe.adriel.voyager:voyager-koin:1.0.0")
          
          // Image loading (Coil3 - multiplatform)
          implementation("io.coil-kt.coil3:coil-compose:3.3.0")
          implementation("io.coil-kt.coil3:coil-network-ktor:3.3.0")
      }
  }
  ```

- [ ] **Step 4:** Verify shared module builds with Compose: `./gradlew :shared:build`

### Part B: Create iOS App with Compose

- [ ] **Step 5:** Create `iosApp/` directory structure:
  ```
  iosApp/
  ├── iosApp/
  │   ├── ContentView.swift
  │   ├── ComposeApp.swift
  │   └── Info.plist
  ├── iosApp.xcodeproj/
  └── Podfile
  ```

- [ ] **Step 6:** Create Xcode project for iOS app (File → New → Project → iOS App)

- [ ] **Step 7:** Configure CocoaPods `Podfile` for Firebase:
  ```ruby
  platform :ios, '14.0'
  
  target 'iosApp' do
    use_frameworks!
    
    # Firebase
    pod 'Firebase/Auth'
    pod 'Firebase/Firestore'
    pod 'Firebase/Messaging'
    pod 'Firebase/Crashlytics'
  end
  ```

- [ ] **Step 8:** Add `sharedKit.framework` to Xcode project:
  - Add framework search path: `$(SRCROOT)/../../shared/build/XCFrameworks/debug`
  - Link binary with `sharedKit.framework`
  - Embed framework

- [ ] **Step 9:** Add `Info.plist` entries:
  ```xml
  <!-- ATS Exception for HTTP API -->
  <key>NSAppTransportSecurity</key>
  <dict>
      <key>NSExceptionDomains</key>
      <dict>
          <key>167.172.209.252</key>
          <dict>
              <key>NSExceptionAllowsInsecureHTTPLoads</key>
              <true/>
          </dict>
      </dict>
  </dict>
  
  <!-- Push Notifications -->
  <key>UIBackgroundModes</key>
  <array>
      <string>remote-notification</string>
  </array>
  
  <!-- Localization -->
  <key>CFBundleDevelopmentRegion</key>
  <string>ar</string>
  <key>CFBundleLocalizations</key>
  <array>
      <string>ar</string>
      <string>en</string>
  </array>
  ```

- [ ] **Step 10:** Add `GoogleService-Info.plist` for Firebase

- [ ] **Step 11:** Create Swift wrapper for Compose UI (`ContentView.swift`):
  ```swift
  import SwiftUI
  import sharedKit
  
  struct ContentView: View {
      var body: some View {
          ComposeView()
              .ignoresSafeArea(.all)
      }
  }
  
  struct ComposeView: UIViewControllerRepresentable {
      func makeUIViewController(context: Context) -> UIViewController {
          return MainKt.createComposeViewController()
      }
      
      func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
  }
  ```

- [ ] **Step 12:** Configure signing with Apple Developer certificates

- [ ] **Step 13:** Verify build and run on iOS simulator

- [ ] **Step 14:** Commit

---

## Phase 13: Migrate UI to Compose Multiplatform ⭐ IN PROGRESS

**Goal:** Move existing Compose screens from `androidApp` to `shared/commonMain` so both Android and iOS use the same UI code.

**Strategy:** Incremental shared UI — move screens one-by-one, replacing Android-only APIs with multiplatform equivalents.

**Key Android-only dependencies to replace:**

| Android-only API | Multiplatform Replacement |
|-----------------|--------------------------|
| `androidx.lifecycle.ViewModel` + `viewModelScope` | `androidx.lifecycle:lifecycle-viewmodel-compose` (KMP since 2.8.0) |
| Navigation3 `NavDisplay` | Custom `AnimatedContent`-based NavHost (simple, already wraps a `SnapshotStateList`) |
| `koinViewModel()` | `koinInject()` or `org.koin.compose.viewmodel.koinViewModel` (KMP) |
| `R.string.*` | Compose Resources `org.jetbrains.compose.resources.stringResource(Res.string.*)` |
| `R.drawable.*` | Compose Resources `org.jetbrains.compose.resources.painterResource(Res.drawable.*)` |
| `R.font.*` | Compose Resources fonts |
| `com.intuit.sdp/ssp` (`dimensionResource`) | Fixed `dp`/`sp` values (Compose handles density natively) |
| `LocalActivity`, `LocalContext` | `expect/actual` for platform-specific operations |
| `BaseScreen` (uses Dialog, Activity) | Simplified multiplatform version |
| `ComposeUIException` (uses `Context`, `@StringRes`, Gson) | Simplified multiplatform error model |
| `ToastEvent` (uses Android Toast) | Multiplatform Snackbar/message approach |

---

### Phase 13A: Foundation — Theme, Colors, Styles, Base Infrastructure

**Goal:** Create the multiplatform UI foundation that all screens depend on.

**Files to create in `shared/src/commonMain/kotlin/com/ovasta/sellers/ui/`:**

- [ ] **Step 1:** `ui/theme/AppColors.kt` — All color definitions (port from `ComposableColor.kt`)
  ```kotlin
  package com.ovasta.sellers.ui.theme
  // Port all colors: Primary, Gray500, Gray600, etc.
  ```

- [ ] **Step 2:** `ui/theme/AppTypography.kt` — Text styles (port from `styles.kt`)
  - Replace `dimensionResource(com.intuit.ssp.R.dimen._14ssp)` → `14.sp`
  - Replace `FontFamily(Font(R.font.medium_new))` → Compose Resources font loading
  - Copy font files to `shared/composeResources/font/`

- [ ] **Step 3:** `ui/theme/AppTheme.kt` — Simplified MaterialTheme wrapper (no dynamic colors, no `Build.VERSION`)

- [ ] **Step 4:** `ui/base/AppException.kt` — Multiplatform error model:
  ```kotlin
  data class AppException(
      val title: String? = null,
      val message: String? = null,
      val code: Int? = null,
      val actions: List<() -> Unit> = emptyList()
  )
  ```

- [ ] **Step 5:** `ui/base/BaseViewModel.kt` — Multiplatform base ViewModel:
  ```kotlin
  // Use androidx.lifecycle.ViewModel (KMP-compatible since 2.8.0)
  // Replace Android Toast with SharedFlow<String> for messages
  ```
  Add dependency: `implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0")` to shared/commonMain

- [ ] **Step 6:** `ui/base/BaseScreen.kt` — Multiplatform BaseScreen:
  - Loading dialog (no Android Dialog — use Compose `AlertDialog` or overlay)
  - Error dialog (no `LocalContext` — use string-based messages)
  - Navigation event handler

- [ ] **Step 7:** `ui/base/Navigator.kt` — Port existing Navigator class (already multiplatform-friendly):
  ```kotlin
  // The Navigator class uses SnapshotStateList — already works in commonMain
  // Just move it and add LocalNavigator CompositionLocal
  ```

- [ ] **Step 8:** `ui/base/NavHost.kt` — Custom multiplatform NavHost:
  ```kotlin
  @Composable
  fun AppNavHost(backStack: SnapshotStateList<Any>, entryProvider: @Composable (Any) -> Unit) {
      val current = backStack.lastOrNull()
      AnimatedContent(targetState = current) { screen ->
          if (screen != null) entryProvider(screen)
      }
  }
  ```

- [ ] **Step 9:** `ui/components/CenteredTextAppBar.kt` — Port app bar (replace `painterResource(R.drawable.*)` with Compose Resources)

- [ ] **Step 10:** Copy resources to `shared/composeResources/`:
  - `drawable/` — all vector icons (SVG or XML → compose resources)
  - `font/` — all .ttf/.otf files
  - `values/strings.xml` — all strings (Arabic + English)

- [ ] **Step 11:** Verify build: `./gradlew :shared:compileCommonMainKotlinMetadata`

- [ ] **Step 12:** Commit: `feat(phase13a): add multiplatform UI foundation (theme, colors, styles, base)`

---

### Phase 13B: Move ViewModels to Shared

**Goal:** Move all ViewModels to `shared/commonMain` since they already use shared repositories.

**Key change:** Replace `BaseViewModel` extending AndroidX ViewModel with the KMP-compatible version.

**Files to create in `shared/src/commonMain/kotlin/com/ovasta/sellers/ui/`:**

- [ ] **Step 1:** `ui/screens/AppScreens.kt` — Route definitions (port from `AppScreens.kt`):
  ```kotlin
  // Remove @Keep (Android-only annotation), routes are just data objects
  data object Splash
  data object Login
  data object Home
  data class CreateOrder(val id: Long = 0L)
  data object Profile
  data object LastOrders
  data object Wallet
  ```

- [ ] **Step 2:** `ui/screens/login/LoginViewModel.kt` — Port LoginViewModel:
  - Remove `DataStore<SessionPreferences>` dependency → use `ISettingsRepository` for FCM token
  - Remove `R.string.*` references
  - Keep business logic identical

- [ ] **Step 3:** `ui/screens/login/LoginViewState.kt` + `LoginAction.kt`

- [ ] **Step 4:** `ui/screens/home/HomeViewModel.kt` — Port HomeViewModel:
  - Remove `R.string.order_cancelled_successfully` → use string message
  - Replace `ToastEvent.ResourceToastEvent` with string-based message

- [ ] **Step 5:** `ui/screens/home/HomeViewState.kt` + `HomeScreenActions.kt`

- [ ] **Step 6:** `ui/screens/splash/SplashViewModel.kt` — Port SplashViewModel

- [ ] **Step 7:** `ui/screens/createorder/CreateOrderViewModel.kt` + state/actions

- [ ] **Step 8:** `ui/screens/profile/ProfileViewModel.kt` + state/actions

- [ ] **Step 9:** `ui/screens/orderhistory/OrderHistoryViewModel.kt` + state/actions

- [ ] **Step 10:** `ui/screens/wallet/WalletViewModel.kt` + state/actions

- [ ] **Step 11:** Update shared Koin DI module to provide ViewModels

- [ ] **Step 12:** Verify build + Commit: `feat(phase13b): move ViewModels to shared module`

---

### Phase 13C: Move Screens to Shared

**Goal:** Move all Compose screen composables to `shared/commonMain`.

**Order:** Splash → Login → Home → CreateOrder → Profile → OrderHistory → Wallet

- [ ] **Step 1:** `ui/screens/splash/SplashScreen.kt`:
  - Replace `painterResource(R.drawable.logo)` with Compose Resources
  - Simplest screen, good starting point

- [ ] **Step 2:** `ui/screens/login/LoginScreen.kt` + `components/`:
  - Replace `stringResource(R.string.*)` with Compose Resources
  - Replace `dimensionResource(com.intuit.sdp.R.dimen._16sdp)` → `16.dp`
  - Replace `painterResource(R.drawable.*)` with Compose Resources
  - Replace `LocalSoftwareKeyboardController` (available in CMP)

- [ ] **Step 3:** `ui/screens/home/HomeScreen.kt` + `components/`:
  - Port SellerHomeContent, TaskCard, StatusTag, SearchWithFilterBar, LogoutDialog, InfoRow

- [ ] **Step 4:** `ui/screens/createorder/CreateOrderScreen.kt` + `components/`

- [ ] **Step 5:** `ui/screens/profile/ProfileScreen.kt` + `components/`

- [ ] **Step 6:** `ui/screens/orderhistory/OrdersScreen.kt` + `components/`

- [ ] **Step 7:** `ui/screens/wallet/WalletScreen.kt` + `components/`:
  - Port WalletContent, RedeemPointsBottomSheet

- [ ] **Step 8:** `ui/App.kt` — Main app composable with NavHost + bottom bar:
  ```kotlin
  @Composable
  fun App() {
      AppTheme {
          AppNavHost() // Contains all routing
      }
  }
  ```

- [ ] **Step 9:** Handle platform-specific operations via expect/actual:
  ```kotlin
  // commonMain
  expect fun openDialer(phoneNumber: String)
  expect fun openWhatsApp(phoneNumber: String)
  expect fun copyToClipboard(text: String)
  ```

- [ ] **Step 10:** Verify build + Commit: `feat(phase13c): move all screens to shared module`

---

### Phase 13D: Wire Android + iOS to Shared UI

**Goal:** Both platforms use the shared `App()` composable as their entry point.

- [ ] **Step 1:** Update `androidApp/MainActivity.kt`:
  ```kotlin
  setContent {
      com.ovasta.sellers.ui.App()
  }
  ```

- [ ] **Step 2:** Delete old UI code from `androidApp/presentation/` (now in shared)

- [ ] **Step 3:** Update `shared/src/iosMain/kotlin/com/ovasta/sellers/Main.ios.kt`:
  ```kotlin
  fun createComposeViewController() = ComposeUIViewController {
      App()
  }
  ```

- [ ] **Step 4:** Verify Android build: `./gradlew :androidApp:assembleDebug`

- [ ] **Step 5:** Verify iOS build via CI: trigger `ios-build.yml`

- [ ] **Step 6:** Commit: `feat(phase13d): wire both platforms to shared UI`

---

### Phase 13E: Cross-Platform Polish

- [x] **Step 1:** Test Arabic/English string switching on both platforms
- [x] **Step 2:** Test RTL layout on both platforms
- [x] **Step 3:** Verify push notifications work on iOS (APNs)
- [x] **Step 4:** Test navigation flows on both platforms
- [x] **Step 5:** Fix any iOS-specific UI issues (safe areas, status bar, etc.)
- [x] **Step 6:** Final commit: `feat(phase13e): cross-platform UI polish`

---

### Phase 13 Dependencies to Add

Add these to `shared/build.gradle.kts` `commonMain` dependencies:

```kotlin
// Lifecycle ViewModel (KMP-compatible)
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0")
// Koin Compose Multiplatform
implementation("io.insert-koin:koin-compose:4.1.0")
implementation("io.insert-koin:koin-compose-viewmodel:4.1.0")
```

### Phase 13 Key Decisions Made

1. **Navigation**: Custom simple NavHost using `AnimatedContent` (NOT Voyager/Decompose — too much overhead for this app's simple navigation)
2. **ViewModel**: `androidx.lifecycle:lifecycle-viewmodel-compose` KMP version
3. **DI**: `koin-compose-viewmodel` for multiplatform `koinViewModel()`
4. **Resources**: Compose Resources (`composeResources/`) for strings, drawables, fonts
5. **Dimensions**: Replace sdp/ssp with fixed dp/sp values (Compose handles density)
6. **Error handling**: Simplified multiplatform `AppException` with string messages (no `Context`)
7. **Toast**: Replace with SharedFlow<String> messages displayed via Snackbar

---

## Phase 14: Release Preparation

**Goal:** Both platforms ready for production release.

**Android:**
- [ ] **Step 1:** Verify full regression test on device
- [ ] **Step 2:** Ensure ProGuard/R8 rules cover shared module
- [ ] **Step 3:** Generate signed release APK/AAB

**iOS:**
- [ ] **Step 4:** Test on physical device
- [ ] **Step 5:** Configure App Store Connect
- [ ] **Step 6:** Archive and upload to TestFlight
- [ ] **Step 7:** Submit for App Store review

**Both:**
- [ ] **Step 8:** Verify push notifications work end-to-end
- [ ] **Step 9:** Verify Arabic/English switching
- [ ] **Step 10:** Final QA pass on both platforms
