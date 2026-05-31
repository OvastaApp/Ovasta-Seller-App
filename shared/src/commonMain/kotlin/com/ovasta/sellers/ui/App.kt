package com.ovasta.sellers.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.ovasta.sellers.ui.base.LocalNavigator
import com.ovasta.sellers.ui.base.Navigator
import com.ovasta.sellers.ui.screens.CreateOrder
import com.ovasta.sellers.ui.screens.Home
import com.ovasta.sellers.ui.screens.LastOrders
import com.ovasta.sellers.ui.screens.Login
import com.ovasta.sellers.ui.screens.Profile
import com.ovasta.sellers.ui.screens.Splash
import com.ovasta.sellers.ui.screens.Wallet
import com.ovasta.sellers.ui.screens.createorder.CreateOrderScreen
import com.ovasta.sellers.ui.screens.createorder.CreateOrderViewModel
import com.ovasta.sellers.ui.screens.home.HomeScreen
import com.ovasta.sellers.ui.screens.home.HomeViewModel
import com.ovasta.sellers.ui.screens.login.LoginScreen
import com.ovasta.sellers.ui.screens.login.LoginViewModel
import com.ovasta.sellers.ui.screens.orderhistory.OrderHistoryScreen
import com.ovasta.sellers.ui.screens.orderhistory.OrderHistoryViewModel
import com.ovasta.sellers.ui.screens.profile.ProfileScreen
import com.ovasta.sellers.ui.screens.profile.ProfileViewModel
import com.ovasta.sellers.ui.screens.splash.SplashScreen
import com.ovasta.sellers.ui.screens.splash.SplashViewModel
import com.ovasta.sellers.ui.screens.wallet.WalletScreen
import com.ovasta.sellers.ui.screens.wallet.WalletViewModel
import com.ovasta.sellers.ui.theme.AppTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(layoutDirection: LayoutDirection = LayoutDirection.Rtl) {
    AppTheme {
        CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
            val backStack = remember { mutableStateListOf<Any>(Splash as Any) }
            val navigator = remember { Navigator(backStack) }

            CompositionLocalProvider(LocalNavigator provides navigator) {
            val currentScreen = backStack.lastOrNull() ?: Splash

            when (currentScreen) {
                is Splash -> {
                    val viewModel = koinViewModel<SplashViewModel>()
                    SplashScreen(viewModel)
                }
                is Login -> {
                    val viewModel = koinViewModel<LoginViewModel>()
                    LoginScreen(viewModel)
                }
                is Home -> {
                    val viewModel = koinViewModel<HomeViewModel>()
                    HomeScreen(viewModel)
                }
                is CreateOrder -> {
                    val viewModel = koinViewModel<CreateOrderViewModel>()
                    CreateOrderScreen(viewModel)
                }
                is Profile -> {
                    val viewModel = koinViewModel<ProfileViewModel>()
                    ProfileScreen(viewModel)
                }
                is LastOrders -> {
                    val viewModel = koinViewModel<OrderHistoryViewModel>()
                    OrderHistoryScreen(viewModel)
                }
                is Wallet -> {
                    val viewModel = koinViewModel<WalletViewModel>()
                    WalletScreen(viewModel)
                }
            }
            }
        }
    }
}
