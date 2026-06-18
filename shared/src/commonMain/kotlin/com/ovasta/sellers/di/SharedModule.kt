package com.ovasta.sellers.di

import com.ovasta.sellers.data.remote.CreateOrderApiService
import com.ovasta.sellers.data.remote.FcmTokenApiService
import com.ovasta.sellers.data.remote.HomeApiService
import com.ovasta.sellers.data.remote.HttpClientFactory
import com.ovasta.sellers.data.remote.LoginApiService
import com.ovasta.sellers.data.remote.OrderHistoryApiService
import com.ovasta.sellers.data.remote.ProductApiService
import com.ovasta.sellers.data.remote.SettingsApiService
import com.ovasta.sellers.data.remote.UnauthorizedHandler
import com.ovasta.sellers.data.remote.WalletApiService
import com.ovasta.sellers.data.repository.CreateOrderRepository
import com.ovasta.sellers.data.repository.HomeRepository
import com.ovasta.sellers.data.repository.LoginRepository
import com.ovasta.sellers.data.repository.OrderHistoryRepository
import com.ovasta.sellers.data.repository.ProductRepository
import com.ovasta.sellers.data.repository.SettingsRepository
import com.ovasta.sellers.data.repository.WalletRepository
import com.ovasta.sellers.domain.repository.ICreateOrderRepository
import com.ovasta.sellers.domain.repository.IHomeRepository
import com.ovasta.sellers.domain.repository.ILoginRepository
import com.ovasta.sellers.domain.repository.IOrderHistoryRepository
import com.ovasta.sellers.domain.repository.IProductRepository
import com.ovasta.sellers.domain.repository.ISettingsRepository
import com.ovasta.sellers.domain.repository.IWalletRepository
import com.ovasta.sellers.ui.screens.createorder.CreateOrderViewModel
import com.ovasta.sellers.ui.screens.home.HomeViewModel
import com.ovasta.sellers.ui.screens.login.LoginViewModel
import com.ovasta.sellers.ui.screens.orderhistory.OrderHistoryViewModel
import com.ovasta.sellers.ui.screens.products.CategoryProductsViewModel
import com.ovasta.sellers.ui.screens.products.ProductCategoriesViewModel
import com.ovasta.sellers.ui.screens.profile.ProfileViewModel
import com.ovasta.sellers.ui.screens.splash.SplashViewModel
import com.ovasta.sellers.ui.screens.wallet.WalletViewModel
import com.ovasta.sellers.ui.permissions.PermissionViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Shared Koin module providing common dependencies (networking, repositories).
 * Platform-specific modules (androidModule, iosModule) must provide:
 * - SecureStorage
 * - DataStoreProvider
 * - FirebaseAuthProvider
 * - FirestoreProvider
 * - FirebaseMessagingProvider
 * - DeviceInfoProvider
 * - HapticFeedback
 * - SessionHeaderProvider (for HTTP client auth headers)
 */
val sharedModule = module {
    // Unauthorized Handler
    singleOf(::UnauthorizedHandler)
    
    // HTTP Client
    single { HttpClientFactory.create(get(), get()) }

    // API Services
    singleOf(::LoginApiService)
    singleOf(::HomeApiService)
    singleOf(::CreateOrderApiService)
    singleOf(::WalletApiService)
    singleOf(::OrderHistoryApiService)
    singleOf(::ProductApiService)
    singleOf(::FcmTokenApiService)
    singleOf(::SettingsApiService)

    // Repositories
    singleOf(::LoginRepository) bind ILoginRepository::class
    singleOf(::HomeRepository) bind IHomeRepository::class
    singleOf(::CreateOrderRepository) bind ICreateOrderRepository::class
    singleOf(::WalletRepository) bind IWalletRepository::class
    singleOf(::OrderHistoryRepository) bind IOrderHistoryRepository::class
    singleOf(::ProductRepository) bind IProductRepository::class
    singleOf(::SettingsRepository) bind ISettingsRepository::class

    // ViewModels
    viewModelOf(::SplashViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::CreateOrderViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::OrderHistoryViewModel)
    viewModelOf(::ProductCategoriesViewModel)
    viewModelOf(::CategoryProductsViewModel)
    viewModelOf(::WalletViewModel)
    viewModelOf(::PermissionViewModel)
}

/**
 * Platform-specific modules must be provided by each platform.
 * Use this expect function to get the platform module.
 */
expect fun platformModule(): Module
