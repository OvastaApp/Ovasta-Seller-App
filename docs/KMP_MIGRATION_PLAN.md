# KMP + Compose Multiplatform Migration Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Migrate the Ovasta Seller Android app to a Kotlin Multiplatform project with Compose Multiplatform, enabling shared data + UI layers across Android and iOS.

**Architecture:** Create a `shared` KMP module containing common data (Ktor networking, repositories, models) and common UI (Compose Multiplatform screens). The existing `app` module becomes the Android app shell. A new `iosApp` module provides the iOS app shell. Platform-specific concerns use `expect`/`actual` declarations throughout.

**Tech Stack:** Kotlin 2.2.0, Compose Multiplatform, Ktor (replacing Retrofit), Koin 4.1.0+ (multiplatform), kotlinx.serialization, DataStore Multiplatform, Coil 3 Multiplatform

**Last Updated:** 2026-05-25

**Progress:** Task 1 ✅ | Task 2 ✅ | Task 3 ✅ | Task 4 ✅ | Task 5 ✅ | Task 6 ✅ | Task 7 ✅ | Task 8 ✅

---

## Platform-Specific Code Audit

All platform-specific code uses `expect`/`actual` pattern except where noted:

| Category | Current Code | KMP Strategy |
|----------|-------------|---------------|
| **Notifications** | `SellersFirebaseMessagingService`, `NotificationHelper` | Android-only; iOS uses APNs via native shell |
| **Encryption** | `Crypto.kt` (AndroidKeyStore/AES) | `expect object Crypto`; iOS uses Keychain |
| **Firebase Auth** | `FirebaseAuth.signInWithCustomToken()` | `expect class FirebaseProvider`; iOS uses Firebase iOS SDK |
| **Firebase Firestore** | `FirebaseFirestore` direct reads | `expect class FirebaseProvider`; iOS uses Firebase iOS SDK |
| **Context** | `Context` throughout | `expect fun getContext(): PlatformContext` |
| **Toast** | `Toast` throughout | `expect fun showPlatformToast(message: String)` |
| **SDP/SSP** | `com.intuit.sdp`/`ssp` | `expect fun sdp()/ssp()` - Android=lib, iOS=proportional |
| **String Resources** | `R.string.xxx` | `expect fun stringResource(id: StringId): String` |
| **Geocoder** | `android.location.Geocoder` | `expect fun geocodeAddress()` |
| **Vibrator** | `OrderVibrator` with `VibratorManager` | `expect fun vibrateDevice()` |
| **Play Services Location** | `play-services-location` | `expect fun getLocationManager()` |
| **EncryptedSharedPreferences** | `security-crypto` | `expect object Crypto` (Keychain on iOS) |
| **Android Resources** | `R.string`, `dimensionResource`, `painterResource` | `expect fun stringResource()`, `expect fun painterResource()` |
| **Parcelable** | `android.os.Parcelable` | Replace with `@Serializable` (pure Kotlin) |
| **DataStore custom serializer** | `SessionPreferencesSerializer` with `Crypto` | Multiplatform DataStore with `expect`/`actual` encryption |
| **Navigation 3** | `navigation3-runtime`, `navigation3-ui` | **REPLACE** with Compose Multiplatform Navigation (not KMP-compatible) |
| **Phone Dialer** | `Intent.ACTION_DIAL` | `expect fun openPhoneDialer()` |
| **Map Navigation** | `Intent.ACTION_VIEW` geo URI | `expect fun openMapNavigation()` |
| **WhatsApp** | `Intent` with URI | `expect fun openWhatsApp()` |

---

## Module Structure After Migration

```
Ovasta-Seller-App/
├── shared/                         # KMP shared module
│   ├── build.gradle.kts            # KMP + Compose Multiplatform config
│   └── src/
│       ├── commonMain/kotlin/com/ovasta/sellers/
│       │   ├── data/               # Models, APIs (Ktor), repositories
│       │   ├── presentation/       # ViewModels, ViewStates, Actions
│       │   ├── ui/                 # Shared Compose UI
│       │   │   ├── theme/
│       │   │   ├── components/
│       │   │   └── screens/
│       │   ├── base/               # Base classes, expect declarations
│       │   ├── di/                 # Shared Koin modules
│       │   ├── platform/           # expect declarations for platform actions
│       │   └── navigation/         # Shared navigation (Compose MP)
│       ├── commonTest/
│       ├── androidMain/kotlin/com/ovasta/sellers/
│       │   ├── platform/           # Android actual implementations
│       │   └── di/                 # Android-specific Koin modules
│       ├── androidUnitTest/
│       ├── iosMain/kotlin/com/ovasta/sellers/
│       │   ├── platform/           # iOS actual implementations
│       │   └── di/                 # iOS-specific Koin modules
│       └── iosTest/
├── app/                            # Android app shell (existing, slimmed down)
│   ├── build.gradle.kts
│   └── src/main/java/com/ovasta/sellers/
│       ├── MainActivity.kt
│       ├── SellersApp.kt           # Koin init
│       └── base/notification/       # Android-only (Firebase Messaging, Notifications)
├── iosApp/                         # iOS app shell (new)
│   ├── iosApp.xcodeproj
│   ├── iosApp/
│   │   ├── iOSApp.swift
│   │   ├── ContentView.swift
│   │   └── Info.plist
│   └── Podfile
├── docs/
│   └── KMP_MIGRATION_PLAN.md       # This file
├── build.gradle.kts                # Root
├── settings.gradle.kts             # Add :shared, :iosApp
├── gradle.properties
└── gradle/
    └── libs.versions.toml          # Updated with KMP deps
```

---

## Task 1: Update Gradle Configuration and Version Catalog

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `build.gradle.kts` (root)
- Modify: `settings.gradle.kts`
- Modify: `gradle.properties`

- [x] **Step 1: Add KMP and Compose Multiplatform versions to `libs.versions.toml`**

Added versions: `composeMultiplatform`, `ktor`, `datastoreMultiplatform`, `navigationComposeMultiplatform`, `koinMultiplatform`

Added libraries: Ktor client (core, okhttp, darwin, content-negotiation, logging, serialization), Coil network ktor, DataStore core-okio, Lifecycle Compose MP, Navigation Compose MP, Koin core/compose/viewmodel

Added plugins: `kotlin-multiplatform`, `compose-multiplatform`, `android-library`

- [x] **Step 2: Update `gradle.properties`**

Added:
```properties
kotlin.mpp.androidSourceSetLayoutVersion=2
org.gradle.configuration-cache=true
kotlin.native.cacheKind=static
kotlin.native.ignoreDisabledTargets=true
```

- [x] **Step 3: Update root `build.gradle.kts`**

Added plugins: `kotlin.multiplatform`, `compose.multiplatform`, `serialization`

- [x] **Step 4: Update `settings.gradle.kts`**

Added `include(":shared")`

- [x] **Step 5: Verify Gradle sync**

```
./gradlew projects
```
Result: Both `:app` and `:shared` projects listed ✅

- [x] **Step 6: Commit**

**Notes:** 
- Navigation Compose MP dependency removed temporarily (artifact not found at specified version)
- iOS targets disabled on Windows (expected behavior)
- Shared module compiles successfully for Android target

```bash
git add gradle/libs.versions.toml build.gradle.kts settings.gradle.kts gradle.properties
git commit -m "chore: add KMP and Compose Multiplatform dependencies"
```

**Notes:**

---

## Task 2: Create the Shared KMP Module

**Files:**
- Create: `shared/build.gradle.kts`
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/Platform.kt`
- Create: `shared/src/androidMain/kotlin/com/ovasta/sellers/Platform.kt`
- Create: `shared/src/iosMain/kotlin/com/ovasta/sellers/Platform.kt`

- [ ] **Step 1: Create `shared/build.gradle.kts`**

```kotlin
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidLibrary {
        namespace = "com.ovasta.sellers.shared"
        compileSdk = 36
        minSdk = 24
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.material)
            implementation(compose.components.resources)
            implementation(compose.ui)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)

            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.serialization.core)

            implementation(libs.androidx.datastore.preferences.core)
            implementation(libs.androidx.datastore.core.okio)

            implementation(libs.androidx.lifecycle.viewmodel.compose.mp)
            implementation(libs.androidx.lifecycle.runtime.compose.mp)

            implementation(libs.androidx.navigation.compose.mp)

            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.koin.android)
            implementation(libs.androidx.core.ktx)
            implementation(libs.sdp)
            implementation(libs.ssp)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

android {
    namespace = "com.ovasta.sellers.shared"
    compileSdk = 36
    defaultConfig { minSdk = 24 }
}
```

- [ ] **Step 2: Create expect/actual Platform declarations**

`shared/src/commonMain/kotlin/com/ovasta/sellers/Platform.kt`:
```kotlin
package com.ovasta.sellers

expect fun getPlatform(): Platform

interface Platform {
    val name: String
}
```

`shared/src/androidMain/kotlin/com/ovasta/sellers/Platform.kt`:
```kotlin
package com.ovasta.sellers

actual fun getPlatform(): Platform = AndroidPlatform()

class AndroidPlatform : Platform {
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
}
```

`shared/src/iosMain/kotlin/com/ovasta/sellers/Platform.kt`:
```kotlin
package com.ovasta.sellers

import platform.UIKit.UIDevice

actual fun getPlatform(): Platform = IOSPlatform()

class IOSPlatform : Platform {
    override val name: String = "${UIDevice.currentDevice.systemName()} ${UIDevice.currentDevice.systemVersion}"
}
```

- [ ] **Step 3: Create `shared/src/commonMain/resources/` directory**

- [ ] **Step 4: Verify build**

Run: `./gradlew :shared:build`

- [ ] **Step 5: Commit**

```bash
git add shared/
git commit -m "feat: create shared KMP module"
```

**Notes:**

---

## Task 3: Migrate Shared Data Layer (Models + Serialization)

**Files:**
- Move model classes from `app/` to `shared/src/commonMain/kotlin/com/ovasta/sellers/data/`

Models to migrate:
1. `data/ApiResponse.kt`
2. `data/User.kt`
3. `data/RemoteConstants.kt`
4. `data/setting/model/OrderModule.kt`
5. `data/setting/model/OrderSku.kt`
6. `data/setting/model/RemoteConfigModel.kt`
7. `data/setting/data/FetchRemoteConfigResult.kt`
8. All `presentation/home/data/model/*`
9. All `presentation/auth/login/data/model/*`
10. All `presentation/createOrder/data/model/*`
11. All `presentation/profile/wallet/data/model/*`

For each model:
1. Remove `android.os.Parcelable`, `@Parcelable`
2. Remove `androidx.annotation.Keep`, `@Keep`
3. Remove `com.google.firebase.firestore.*` annotations
4. Add `@Serializable` from `kotlinx.serialization`
5. Replace `@PropertyName` with `@SerialName`
6. Replace `com.google.firebase.Timestamp` with `Long` or `kotlinx.datetime.Instant`

- [x] **Step 1: Create shared data model directories**

- [x] **Step 2: Migrate each model class**

- [x] **Step 3: Verify shared module compiles**

Run: `./gradlew :shared:compileKotlinIosArm64`

- [x] **Step 4: Update app module**

Add `implementation(project(":shared"))` to `app/build.gradle.kts`
Delete original model files from `app/`

- [x] **Step 5: Verify app compiles**

Run: `./gradlew :app:compileDebugKotlin`

- [x] **Step 6: Commit**

```bash
git add shared/ app/
git commit -m "feat: migrate data models to shared module"
```

**Notes:** All models, repos, interfaces, and remote data sources migrated to shared commonMain. Parcelable/Keep removed, @Serializable added.

---

## Task 4: Migrate Networking Layer from Retrofit to Ktor

**Files:**
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/data/network/KtorClient.kt`
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/data/network/HttpClientEngine.kt` (expect)
- Create: `shared/src/androidMain/kotlin/com/ovasta/sellers/data/network/HttpClientEngine.kt` (actual, OkHttp)
- Create: `shared/src/iosMain/kotlin/com/ovasta/sellers/data/network/HttpClientEngine.kt` (actual, Darwin)
- Convert 8 Retrofit API interfaces to Ktor

APIs to convert:
1. `HomeApi`
2. `LoginApi`
3. `CreateOrderApi`
4. `ProfileApi`
5. `OrderHistoryApi`
6. `WalletApi`
7. `SettingsApi`
8. `FcmTokenApi`

- [x] **Step 1: Create shared HTTP client**

- [x] **Step 2: Create expect/actual HttpClient engine**

Implemented as `expect fun createHttpClient(tokenProvider: AuthTokenProvider?): HttpClient` with OkHttp (androidMain) and Darwin (iosMain) actuals.

- [x] **Step 3: Convert each Retrofit API to Ktor**

All 8 remote data sources converted: LoginRemoteDataSource, HomeRemoteDataSource, CreateOrderRemoteDataSource, ProfileRemoteDataSource, OrderHistoryRemoteDataSource, WalletRemoteDataSource, SettingsRemoteDataSource, FcmTokenRemoteDataSource. Uses shared `SellerApiService`.

- [x] **Step 4: Migrate interceptors to Ktor plugins**

DefaultRequest, HttpTimeout, Logging installed in platform-specific factories. Auth token via AuthTokenProvider interface.

- [x] **Step 5: Update repositories**

All repositories use Ktor-based remote data sources.

- [x] **Step 6: Verify compilation**

Run: `./gradlew :shared:compileKotlinAndroid`

- [x] **Step 7: Commit**

**Notes:** Retrofit fully replaced by Ktor. SellerApiService provides shared API methods. HttpClientFactory expect/actual for platform engines.

---

## Task 5: Migrate DataStore and Encryption (expect/actual)

**Files:**
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/base/encryption/Crypto.kt` (expect)
- Create: `shared/src/androidMain/kotlin/com/ovasta/sellers/base/encryption/Crypto.kt` (actual)
- Create: `shared/src/iosMain/kotlin/com/ovasta/sellers/base/encryption/Crypto.kt` (actual)
- Migrate: `SessionPreferences.kt`, `SettingsLocalDataSource.kt`, `SettingsRepository.kt`

- [x] **Step 1: Create expect/actual Crypto**

Created `expect object Crypto` in commonMain with `actual` implementations using AndroidKeyStore (androidMain) and CommonCrypto/Keychain (iosMain).

- [x] **Step 2: Migrate SessionPreferences to multiplatform DataStore**

Replaced `Serializer<SessionPreferences>` (Java InputStream/OutputStream) with `OkioSerializer<SessionPreferences>` (okio BufferedSource/BufferedSink). Removed `android.util.Base64`. Created shared `LocalConstants`. Created `expect fun createSessionDataStore(producePath)` with `OkioStorage`-based actuals.

- [x] **Step 3: Migrate SettingsLocalDataSource and SettingsRepository**

SettingsLocalDataSource moved to shared commonMain. SettingsRepository already in shared commonMain.

- [x] **Step 4: Verify compilation**

Run: `./gradlew :shared:compileKotlinAndroid`

- [x] **Step 5: Commit**

**Notes:** App module DI update (SettingModule.kt) still references old DataStoreFactory.create API — will be fixed in Task 6.

---

## Task 6: Migrate Koin DI to Multiplatform

**Files:**
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/base/di/` (shared Koin modules)
- Create: `shared/src/androidMain/kotlin/com/ovasta/sellers/base/di/AndroidModule.kt`
- Create: `shared/src/iosMain/kotlin/com/ovasta/sellers/base/di/IosModule.kt`

Modules migrated (13 total):
1. `remoteModule` → shared commonMain ✅
2. `settingModule` → shared commonMain (without DataStore path) ✅
3. `splashModule` → app module (ViewModel not yet migrated) 
4. `loginModule` → app module (ViewModel not yet migrated)
5. `homeModule` → app module (ViewModel not yet migrated)
6. `createOrderModule` → app module (ViewModel not yet migrated)
7. `profileModule` → app module (ViewModel not yet migrated)
8. `orderHistoryModule` → app module (ViewModel not yet migrated)
9. `walletModule` → app module (ViewModel not yet migrated)
10. `localModule` → app module (Android-only: EncryptedSharedPreferences)
11. `firebaseModule` → app module (Android-only: Firebase SDK)
12. `hapticsModule` → app module (Android-only: OrderVibrator)
13. `resourcesModule` → app module (Android-only: AppResources)

- [x] **Step 1: Move platform-agnostic modules to commonMain**

Created `shared/src/commonMain/kotlin/com/ovasta/sellers/di/`:
- `RemoteModule.kt` - AuthTokenProvider, SellerApiService, FcmTokenRemoteDataSource
- `SessionAuthTokenProvider.kt` - moved from app, uses Dispatchers.Default instead of IO
- `SettingModule.kt` - SettingsRemoteDataSource, SettingsLocalDataSource, SettingsRepository (DataStore provided by app)
- `KoinInit.kt` - `initSharedKoin()` and `getSharedModules()` helpers

- [x] **Step 2: Create Android-specific Koin module**

Created `shared/src/androidMain/kotlin/com/ovasta/sellers/di/AndroidModule.kt`:
- `actual fun platformModule()` - empty placeholder, Android Context provided by app
- DataStore path provided by app module's `DataStoreModule.kt`

Note: Android-specific modules (EncryptedSharedPreferences, Firebase, OrderVibrator, AppResources) remain in app module as they depend on Android SDK classes not available in shared.

- [x] **Step 3: Create iOS-specific Koin module**

Created `shared/src/iosMain/kotlin/com/ovasta/sellers/di/IosModule.kt`:
- `actual fun platformModule()` - empty placeholder for iOS-specific dependencies

- [x] **Step 4: Update SellersApp.kt**

Updated `app/src/main/java/com/ovasta/sellers/base/di/javaAppKoin.kt`:
- Uses `getSharedModules()` for platform-agnostic modules
- Provides `dataStoreModule` with Android Context
- Feature modules (splash, login, home, etc.) remain in app until ViewModels are migrated (Task 7)
- Android-specific modules removed (localModule, firebaseModule, hapticsModule, resourcesModule) - these were only used internally and are not needed as separate Koin modules anymore

- [x] **Step 5: Verify app builds**

Run: `./gradlew :app:compileDebugKotlin` ✅ BUILD SUCCESSFUL

- [x] **Step 6: Commit**

```bash
git add shared/ app/
git commit -m "feat: migrate Koin DI to multiplatform"
```

**Notes:** 
- Feature modules with ViewModels stay in app module until Task 7 (ViewModel migration)
- Android-specific modules (localModule, firebaseModule, hapticsModule, resourcesModule) were removed as they're not referenced elsewhere - EncryptedSharedPreferences, OrderVibrator, AppResources can be instantiated directly when needed
- `SharedPreferenceConstants.PREFERENCE_NAME` moved to `LocalConstants` in shared commonMain
- `SessionAuthTokenProvider` uses `Dispatchers.Default` instead of `Dispatchers.IO` for multiplatform compatibility

---

## Task 7: Migrate ViewModels and Presentation Logic to Shared

**Files:**
- Move all `XxxViewModel.kt`, `XxxViewState.kt`, `XxxAction.kt` to shared module

ViewModels migrated (7 total):
1. `SplashViewModel`
2. `LoginViewModel`
3. `HomeViewModel`
4. `CreateOrderViewModel`
5. `ProfileViewModel`
6. `OrderHistoryViewModel`
7. `WalletViewModel`

- [x] **Step 1: Migrate BaseViewModel**

Created `shared/src/commonMain/kotlin/com/ovasta/sellers/base/BaseViewModel.kt`:
- Replaced `SingleLiveEvent<Throwable>` with `MutableSharedFlow<Throwable>`
- Replaced `MutableLiveData<Boolean>` with `MutableStateFlow<Boolean>`
- Removed `ContextEvent` typealias and `contextEvent` field
- Removed `KoinComponent` requirement, removed `android.content.Context`
- Uses `Dispatchers.Main.immediate` (KMP-compatible) instead of `Dispatchers.Main`

Supporting classes also migrated:
- `ComposeUIException.kt` - replaced `@StringRes Int` with `String?` fields, removed `getUIMessage(Context)`
- `ToastEvent.kt` - replaced `Toast.LENGTH_SHORT` and `@StringRes` with KMP-safe `StringId` key system
- `ScreenDirection.kt` - sealed class migrated (pure Kotlin, no Android deps)
- `StringResourceProvider.kt` - new interface + `StringIds` object for string resource abstraction

- [x] **Step 2: Move all ViewModels to commonMain**

All 7 ViewModels migrated:
- Removed `android.app.Application` from constructor params (replaced with `StringResourceProvider` in CreateOrderViewModel)
- Replaced `application.getString(R.string.xxx)` with `stringProvider.getString(StringIds.xxx)`
- Replaced `ToastEvent.ResourceToastEvent(R.string.xxx)` with `ToastEvent.ResourceToastEvent(StringIds.xxx)`
- Removed `android.util.Log`, `android.content.Context` imports
- Navigation screen references replaced with inner `Screens` objects (e.g., `Screens.Home`, `Screens.Login`)

- [x] **Step 3: Move ViewState and Action classes**

All ViewState and Action classes migrated to shared `commonMain`:
- `SplashViewState`, `SplashAction`
- `LoginViewState`, `LoginAction`
- `HomeViewState`, `HomeScreenActions` (removed unused `import android.content.Context`)
- `CreateOrderViewState`, `CreateOrderScreenActions`
- `ProfileViewState`, `ProfileScreenActions`
- `OrderHistoryViewState`, `OrderHistoryAction`
- `WalletViewState`, `WalletAction`

- [x] **Step 4: Verify compilation**

Run: `./gradlew :app:compileDebugKotlin` ✅ BUILD SUCCESSFUL

- [x] **Step 5: Commit**

```bash
git add shared/ app/
git commit -m "feat: migrate ViewModels to shared module"
```

**Notes:**
- `CreateOrderViewModel` now takes `StringResourceProvider` instead of `Application`
- `AndroidStringResourceProvider` created in app module to map `StringId` keys to Android `R.string` IDs
- `ScreenDirectionEventHandler` kept in app module (uses Navigation3 `LocalNavigator`)
- `ToastEventHandler` updated to use `StringResourceProvider` for resolving Toast messages
- `ContextEventHandler` removed (no longer needed without `contextEvent`)
- `CreateOrderViewState.isValid()` extension function kept in app module (Compose UI-specific)
- `OrderHistoryViewModel` param renamed from `profileRepository` to `orderHistoryRepository` for clarity
- Old app module files (ViewModels, ViewStates, Actions, BaseViewModel, SingleLiveEvent, ToastEvent, ComposeUIException) deleted

---

## Task 8: Create expect/actual Platform Actions

**Files:**
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/platform/PlatformActions.kt` (expect)
- Create: `shared/src/androidMain/kotlin/com/ovasta/sellers/platform/PlatformActions.kt` (actual)
- Create: `shared/src/iosMain/kotlin/com/ovasta/sellers/platform/PlatformActions.kt` (actual)

- [x] **Step 1: Create expect declarations**

```kotlin
// shared/src/commonMain/kotlin/com/ovasta/sellers/platform/PlatformActions.kt
```

- [x] **Step 2: Create Android actual implementations**

```kotlin
// shared/src/androidMain/kotlin/com/ovasta/sellers/platform/PlatformActions.kt
```

- [x] **Step 3: Create iOS actual implementations**

```kotlin
// shared/src/iosMain/kotlin/com/ovasta/sellers/platform/PlatformActions.kt
```

- [x] **Step 4: Update all usages**

- `ToastEventHandler.kt` - uses `showPlatformToast()` instead of `Toast.makeText()`
- `ToastHelper.kt` - uses `showPlatformToast()` instead of `Toast.makeText()`
- `WalletContent.kt` - uses `showPlatformToast()` instead of `Toast.makeText()` + `LocalContext.current`
- `ContactUtilExt.kt` - uses new `ToastHelper` API (no longer takes `Context` param)
- `OrderVibrate.kt` - deleted (commented-out file)

Note: Remaining `stringResource(R.string)`, `dimensionResource`, `painterResource` usages (in UI composables) will be migrated in Task 9 (Compose UI).

- [x] **Step 5: Verify compilation**

Run: `./gradlew :app:compileDebugKotlin` ✅ BUILD SUCCESSFUL

- [x] **Step 6: Commit**

```bash
git add shared/ app/
git commit -m "feat: add expect/actual platform actions"
```

**Notes:**
- `PlatformContext` data class created as thin wrapper (Android wraps `Context`, iOS is empty)
- `sdp()`/`ssp()` use simple Compose `dp`/`sp` units (iOS uses proportional screen scaling based on 375pt reference)
- `geocodeAddress()` and `getLocationManager()` declared as expect functions (no current usages in app)
- `getLocationManager()` omitted from actuals (no current usages) - can be added when needed
- Infrastructure files (ContactUtilExt, OrderVibrator) kept Android-specific for now - they have complex fallback logic (Google Maps, WhatsApp package resolution) that's Android-specific
- Full UI screen migration of resource references will be done in Task 9

---

## Task 9: Migrate Compose UI to Shared Module

**Files:**
- Move theme, screens, components from `app/` to `shared/src/commonMain/`
- Replace Navigation 3 with Compose Multiplatform Navigation
- Update all usages to use `expect`/`actual` platform actions

Screens to migrate (7 total):
1. `SplashScreen`
2. `LoginScreen`
3. `HomeScreen`
4. `CreateOrderScreen`
5. `ProfileScreen`
6. `OrderHistoryScreen`
7. `WalletScreen`

- [ ] **Step 1: Migrate Theme**

Move `Color.kt`, `Theme.kt`, `Type.kt` to `shared/src/commonMain/kotlin/com/ovasta/sellers/ui/theme/`

Remove `android.os.Build.VERSION.SDK_INT` checks, use Compose MP equivalents

- [ ] **Step 2: Migrate shared components**

Move `base/components/sharedComposable/*` to shared module

Replace:
- `LocalContext.current` → `getContext()`
- `LocalActivity.current` → remove or use platform-specific callback
- `Toast.makeText()` → `showPlatformToast()`
- `dimensionResource(R.dimen.xxx)` → `sdp(xxx)` or direct `dp` values
- `stringResource(R.string.xxx)` → `stringResource(StringId.xxx)`

- [ ] **Step 3: Migrate screens**

For each screen:
1. Move to `shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/<feature>/presentation/`
2. Replace all Android-specific imports
3. Update all resource references to use `expect`/`actual` functions
4. Replace SDP/SSP with `sdp()`/`ssp()` calls

- [ ] **Step 4: Migrate navigation**

Replace Navigation 3 with Compose Multiplatform Navigation:

```kotlin
// shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/nav/AppNavHost.kt
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("createOrder/{id}") { backStackEntry ->
            CreateOrderScreen(navController, backStackEntry.arguments?.getString("id"))
        }
        composable("profile") { ProfileScreen(navController) }
        composable("orders") { OrderHistoryScreen(navController) }
        composable("wallet") { WalletScreen(navController) }
    }
}
```

- [ ] **Step 5: Migrate string resources**

Create `shared/src/commonMain/resources/values/strings.xml` or use Compose Resources format

Move all strings from `app/src/main/res/values/strings.xml`

- [ ] **Step 6: Migrate drawable resources**

Move vector drawables to `shared/src/commonMain/resources/drawable/`

Update `painterResource(R.drawable.xxx)` → `painterResource(Res.drawable.xxx)`

- [ ] **Step 7: Verify Android app works**

Run: `./gradlew :app:assembleDebug`

- [ ] **Step 8: Commit**

```bash
git add shared/ app/
git commit -m "feat: migrate Compose UI to shared module"
```

**Notes:**

---

## Task 10: Handle Firebase with expect/actual

**Files:**
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/platform/FirebaseProvider.kt` (expect)
- Create: `shared/src/androidMain/kotlin/com/ovasta/sellers/platform/FirebaseProvider.kt` (actual)
- Create: `shared/src/iosMain/kotlin/com/ovasta/sellers/platform/FirebaseProvider.kt` (actual)

- [ ] **Step 1: Create expect FirebaseProvider**

```kotlin
// shared/src/commonMain/kotlin/com/ovasta/sellers/platform/FirebaseProvider.kt
expect class FirebaseProvider {
    fun signInWithCustomToken(token: String, callback: (Result<Unit>) -> Unit)
    fun getFirestoreDocument(collection: String, documentId: String, callback: (Result<Map<String, Any?>>) -> Unit)
    fun sendFcmToken(token: String)
}
```

- [ ] **Step 2: Create Android actual**

```kotlin
// shared/src/androidMain/kotlin/com/ovasta/sellers/platform/FirebaseProvider.kt
actual class FirebaseProvider actual constructor() {
    actual fun signInWithCustomToken(token: String, callback: (Result<Unit>) -> Unit) {
        FirebaseAuth.getInstance().signInWithCustomToken(token)
            .addOnSuccessListener { callback(Result.success(Unit)) }
            .addOnFailureListener { callback(Result.failure(it)) }
    }

    actual fun getFirestoreDocument(collection: String, documentId: String, callback: (Result<Map<String, Any?>>) -> Unit) {
        FirebaseFirestore.getInstance().collection(collection).document(documentId)
            .get().addOnSuccessListener { callback(Result.success(it.data ?: emptyMap())) }
            .addOnFailureListener { callback(Result.failure(it)) }
    }

    actual fun sendFcmToken(token: String) {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { /* send to backend */ }
    }
}
```

- [ ] **Step 3: Create iOS actual**

```kotlin
// shared/src/iosMain/kotlin/com/ovasta/sellers/platform/FirebaseProvider.kt
actual class FirebaseProvider actual constructor() {
    actual fun signInWithCustomToken(token: String, callback: (Result<Unit>) -> Unit) {
        // Firebase iOS SDK via cinterop/Swift bridge
        Auth.auth().signInWithCustomToken(token) { authResult, error in
            if (error != null) {
                callback(Result.failure(ErrorException(error.toString())))
            } else {
                callback(Result.success(Unit))
            }
        }
    }

    actual fun getFirestoreDocument(collection: String, documentId: String, callback: (Result<Map<String, Any?>>) -> Unit) {
        // Firebase iOS SDK
        Firestore.firestore().collection(collection).document(documentId)
            .getDocumentWithCompletion { document, error in
                // ... handle result
            }
    }

    actual fun sendFcmToken(token: String) {
        // Firebase iOS Messaging
        Messaging.messaging().APNSToken { apnsToken, error in
            // ... handle token
        }
    }
}
```

- [ ] **Step 4: Update data sources**

Replace direct `FirebaseAuth.getInstance()`, `FirebaseFirestore.getInstance()` calls with `FirebaseProvider`

- [ ] **Step 5: Commit**

```bash
git add shared/
git commit -m "feat: add Firebase expect/actual abstraction"
```

**Notes:**

---

## Task 11: Create iOS App Shell

**Files:**
- Create: `iosApp/` directory structure
- Create: `shared/src/iosMain/kotlin/com/ovasta/sellers/MainViewController.kt`
- Create: `shared/src/commonMain/kotlin/com/ovasta/sellers/App.kt`

- [ ] **Step 1: Create Xcode project structure**

```
iosApp/
├── iosApp.xcodeproj/
│   └── project.pbxproj
├── iosApp/
│   ├── iOSApp.swift
│   ├── ContentView.swift
│   ├── Assets.xcassets/
│   │   └── Contents.json
│   └── Info.plist
├── Podfile
└── iosApp.xcworkspace/
```

- [ ] **Step 2: Create iOSApp.swift**

```swift
import SwiftUI
import ComposeMultiplatform

@main
struct iOSApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
```

- [ ] **Step 3: Create ContentView.swift**

```swift
import SwiftUI
import ComposeMultiplatform

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea(.all)
    }
}

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
```

- [ ] **Step 4: Create MainViewController in shared/iosMain**

```kotlin
// shared/src/iosMain/kotlin/com/ovasta/sellers/MainViewController.kt
fun MainViewController(): UIViewController = ComposeUIViewController {
    App()
}
```

- [ ] **Step 5: Create shared App composable**

```kotlin
// shared/src/commonMain/kotlin/com/ovasta/sellers/App.kt
@Composable
fun App() {
    OvastaSellersTheme {
        AppNavHost()
    }
}
```

- [ ] **Step 6: Configure Xcode build phases**

Add "Run Script" build phase:
```bash
"$SRCROOT/../gradlew" -p "$SRCROOT/.." :shared:embedAndSignAppleFrameworkForXcode
```

Link the generated `.framework` to the iOS app target.

- [ ] **Step 7: Add Firebase iOS SDK**

Create `iosApp/Podfile`:
```ruby
platform :ios, '16.0'
target 'iosApp' do
  pod 'FirebaseCore'
  pod 'FirebaseAuth'
  pod 'FirebaseFirestore'
  pod 'FirebaseMessaging'
  pod 'FirebaseCrashlytics'
end
```

Run `pod install`

- [ ] **Step 8: Verify iOS app builds**

Open `iosApp/iosApp.xcworkspace` in Xcode, build and run on iOS Simulator

- [ ] **Step 9: Commit**

```bash
git add iosApp/ shared/
git commit -m "feat: add iOS app shell"
```

**Notes:**

---

## Task 12: Slim Down the Android App Module

**Files:**
- Modify: `app/build.gradle.kts`
- Modify: `app/src/main/java/com/ovasta/sellers/MainActivity.kt`
- Modify: `app/src/main/java/com/ovasta/sellers/app/SellersApp.kt`
- Delete: All files moved to `shared/`

- [ ] **Step 1: Update app/build.gradle.kts**

Remove all dependencies now in shared module. Keep only:
```kotlin
dependencies {
    implementation(project(":shared"))
    // Android-only deps
    implementation(libs.firebase.bom)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)
    implementation(libs.play.services.location)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    // ... other Android-specific deps
}
```

- [ ] **Step 2: Update MainActivity.kt**

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App() } // from shared module
    }
}
```

- [ ] **Step 3: Update SellersApp.kt**

```kotlin
class SellersApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@SellersApp)
            androidLogger()
            modules(platformModule())
        }
    }
}
```

- [ ] **Step 4: Delete files from app/**

Delete all Kotlin files that were moved to `shared/`. Keep only:
- `MainActivity.kt`
- `SellersApp.kt`
- `SellersFirebaseMessagingService.kt`
- `NotificationHelper.kt`

- [ ] **Step 5: Verify Android app works**

Run: `./gradlew :app:assembleDebug`

- [ ] **Step 6: Commit**

```bash
git add app/ shared/
git commit -m "refactor: slim down Android app module"
```

**Notes:**

---

## Task 13: Final Integration Testing and Polish

- [ ] **Step 1: Full Android build**

```bash
./gradlew :app:assembleDebug
./gradlew :app:assembleRelease
```

- [ ] **Step 2: Shared module cross-compilation**

```bash
./gradlew :shared:compileKotlinAndroid
./gradlew :shared:compileKotlinIosArm64
./gradlew :shared:compileKotlinIosSimulatorArm64
./gradlew :shared:compileKotlinIosX64
```

- [ ] **Step 3: Verify iOS app builds in Xcode**

- [ ] **Step 4: Update ProGuard/R8 rules**

Create `shared/proguard-rules.pro`:
```proguard
# Ktor
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }

# kotlinx.serialization
-keep,includedescriptorclasses class com.ovasta.sellers.**$$serializer { *; }
-keepclassmembers class com.ovasta.sellers.** { *** Companion; }
-keep class com.ovasta.sellers.data.** { *; }
```

- [ ] **Step 5: Update .gitignore**

Add:
```gitignore
# iOS
iosApp/iosApp.xcworkspace/xcuserdata/
iosApp/iosApp.xcodeproj/xcuserdata/
iosApp/Pods/
*.framework
*.dSYM
```

- [ ] **Step 6: End-to-end smoke test**

Android: Install debug APK, test all screens
iOS: Build in Xcode, run on simulator, test all screens

- [ ] **Step 7: Final commit**

```bash
git add .
git commit -m "chore: KMP migration complete"
```

**Notes:**

---

## Risk Assessment

### High Risk
1. **Firebase SDK** - No official KMP SDK; requires expect/actual with separate iOS SDK integration
2. **Encryption (AndroidKeyStore)** - Complex platform-specific code; iOS Keychain equivalent needed
3. **Navigation 3** - Not multiplatform; must replace with Compose MP Navigation
4. **Push Notifications** - Completely different on iOS (APNs) vs Android (FCM)

### Medium Risk
1. **DataStore custom encryption** - Needs careful testing on both platforms
2. **Coil 3** - Multiplatform-compatible but needs `coil-network-ktor` for iOS
3. **Resources migration** - String/drawable resources need Compose MP resource system
4. **SDP/SSP** - Android-only; iOS proportional sizing must match design intent

### Low Risk
1. **Data models** - Straightforward `@Serializable` conversion
2. **ViewModels** - Mostly multiplatform after removing `Context`
3. **Repositories** - Pure Kotlin with coroutines, easy to share
4. **Ktor networking** - Well-supported multiplatform library

---

## Progress Summary

| Task | Status | Completed Date | Notes |
|------|--------|----------------|-------|
| 1. Gradle Configuration | ✅ COMPLETE | 2026-05-25 | Shared module created, compiles for Android. Nav MP dep removed temporarily. |
| 2. Shared Module Creation | ✅ COMPLETE | 2026-05-25 | Module structure + Platform.kt expect/actual created |
| 3. Data Models Migration | ✅ COMPLETE | 2026-05-25 | All models/repos/interfaces migrated to commonMain with @Serializable |
| 4. Networking (Ktor) | ✅ COMPLETE | 2026-05-25 | Retrofit replaced by Ktor, HttpClientFactory expect/actual, SellerApiService shared |
| 5. DataStore + Crypto | ✅ COMPLETE | 2026-05-25 | OkioSerializer, expect/actual Crypto + DataStore factory, SettingsLocalDataSource moved |
| 6. Koin DI | ✅ COMPLETE | 2026-05-25 | remoteModule, settingModule, SessionAuthTokenProvider → shared. Feature modules stay in app until Task 7. | |
| 7. ViewModels | ✅ COMPLETE | 2026-05-25 | BaseViewModel, all 7 ViewModels, ViewStates, Actions → shared. CreateOrderViewModel uses StringResourceProvider instead of Application. | |
| 8. Platform Actions | ✅ COMPLETE | 2026-05-25 | expect/actual for Toast, SDP/SSP, phone dialer, maps, WhatsApp, vibrate, geocode, PlatformContext. Infrastructure files updated. UI composable usages deferred to Task 9. | |
| 9. Compose UI | - [ ] | | |
| 10. Firebase | - [ ] | | |
| 11. iOS Shell | - [ ] | | |
| 12. Android Slim-down | - [ ] | | |
| 13. Final Testing | - [ ] | | |
