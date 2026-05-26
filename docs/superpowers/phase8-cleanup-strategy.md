# Phase 8 Cleanup Strategy

## Goal
Remove duplicate code from androidApp now that shared KMP module provides the business logic.

## What to Remove

### 1. Duplicate API Services (Retrofit → Ktor)
These are now in `shared/src/commonMain/.../data/remote/`:
- ✅ `presentation/auth/login/data/api/LoginApi.kt`
- ✅ `presentation/home/data/api/HomeApi.kt`
- ✅ `presentation/createOrder/data/api/CreateOrderApi.kt`
- ✅ `presentation/profile/orderhistory/data/api/OrderHistoryApi.kt`
- ✅ `presentation/profile/wallet/data/api/WalletApi.kt`

**Keep (not yet in shared):**
- `data/notification/FcmTokenApi.kt`
- `data/setting/data/api/SettingsApi.kt`
- `presentation/profile/profile/data/api/ProfileApi.kt`

### 2. Duplicate Repositories
These are now in `shared/src/commonMain/.../data/repository/`:
- ✅ `presentation/auth/login/data/repository/ILoginRepository.kt` + `LoginRepository.kt`
- ✅ `presentation/home/data/repository/IHomeRepository.kt` + `HomeRepository.kt`
- ✅ `presentation/createOrder/data/repository/ICreateOrderRepository.kt` + `CreateOrderRepository.kt`
- ✅ `presentation/profile/orderhistory/data/repository/IOrderHistoryRepository.kt` + `OrderHistoryRepository.kt`
- ✅ `presentation/profile/wallet/data/repository/IWalletRepository.kt` + `WalletRepository.kt`

### 3. Duplicate Data Models
- ✅ `data/model/User.kt` (duplicates `shared/.../domain/model/User.kt`)
- ✅ `data/model/ApiResponse.kt` (duplicates `shared/.../domain/model/ApiResponse.kt`)

### 4. Feature DI Modules - UPDATE (not remove)
Update these to inject shared repositories:
- `presentation/auth/login/di/LoginModule.kt`
- `presentation/home/di/HomeModule.kt`
- `presentation/createOrder/di/CreateOrderModule.kt`
- `presentation/profile/orderhistory/di/OrderHistoryModule.kt`
- `presentation/profile/wallet/di/WalletModule.kt`

## What to Keep

### Essential Legacy Code
- `base/di/RemoteModule.kt` - Retrofit setup for remaining APIs
- `base/di/FirebaseModule.kt` - Firebase UI helpers
- `base/di/LocalModule.kt` - Local resources/cache
- `base/di/HapticsModule.kt` - Haptics helpers
- All ViewModels (UI layer)
- All Compose screens (presentation layer)
- `data/setting/` - Settings module (complex migration)
- `data/notification/` - Notification module

## Execution Plan

1. ✅ Remove duplicate API services (5 files)
2. ✅ Remove duplicate repositories (10 files: 5 interfaces + 5 implementations)
3. ✅ Remove duplicate models (2 files)
4. ✅ Update feature DI modules to use shared repos
5. ✅ Update ViewModels imports (if needed)
6. ✅ Verify build
7. ✅ Commit

## Risk Assessment
- **Low risk:** API services & repositories (not referenced by UI)
- **Medium risk:** DI modules (need careful updates)
- **High risk:** Models (need to verify no UI direct references)

## Rollback Strategy
Git revert if build fails after cleanup.
