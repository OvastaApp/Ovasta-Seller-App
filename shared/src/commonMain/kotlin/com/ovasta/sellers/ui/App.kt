package com.ovasta.sellers.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.ovasta.sellers.shared.resources.Res
import com.ovasta.sellers.shared.resources.home
import com.ovasta.sellers.shared.resources.ic_home
import com.ovasta.sellers.shared.resources.ic_profile
import com.ovasta.sellers.shared.resources.profile
import com.ovasta.sellers.ui.base.LocalNavigator
import com.ovasta.sellers.ui.base.Navigator
import com.ovasta.sellers.ui.platform.PlatformBackHandler
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
import com.ovasta.sellers.ui.theme.Primary
import com.ovasta.sellers.ui.theme.smSemiBold
import com.ovasta.sellers.ui.theme.xsMedium
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(layoutDirection: LayoutDirection = LayoutDirection.Rtl) {
    AppTheme {
        CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
            val backStack = remember { mutableStateListOf<Any>(Splash as Any) }
            val navigator = remember { Navigator(backStack) }

            CompositionLocalProvider(LocalNavigator provides navigator) {
                val currentScreen = backStack.lastOrNull() ?: Splash
                val canGoBack =
                    backStack.size > 1 && currentScreen !is Home && currentScreen !is Profile

                PlatformBackHandler(enabled = canGoBack) {
                    navigator.pop()
                }

                val showBottomBar = currentScreen is Home || currentScreen is Profile

                Scaffold(
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    bottomBar = {
                        if (showBottomBar) {
                            AppBottomBar(
                                currentScreen = currentScreen,
                                onHomeClick = {
                                    if (currentScreen !is Home) {
                                        navigator.replaceAll(Home)
                                    }
                                },
                                onProfileClick = {
                                    if (currentScreen !is Profile) {
                                        navigator.replaceAll(Profile)
                                    }
                                }
                            )
                        }
                    }
                ) {
                    Box {
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
    }
}

@Composable
private fun AppBottomBar(
    currentScreen: Any,
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        val homeSelected = currentScreen is Home
        val profileSelected = currentScreen is Profile

        NavigationBarItem(
            selected = homeSelected,
            onClick = onHomeClick,
            icon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_home),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = stringResource(Res.string.home),
                    style = if (homeSelected) smSemiBold else xsMedium
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Primary,
                selectedTextColor = Primary,
                unselectedIconColor = Color.Gray.copy(alpha = 0.5f),
                unselectedTextColor = Color.Gray.copy(alpha = 0.5f),
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            selected = profileSelected,
            onClick = onProfileClick,
            icon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_profile),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = stringResource(Res.string.profile),
                    style = if (profileSelected) smSemiBold else xsMedium
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Primary,
                selectedTextColor = Primary,
                unselectedIconColor = Color.Gray.copy(alpha = 0.5f),
                unselectedTextColor = Color.Gray.copy(alpha = 0.5f),
                indicatorColor = Color.Transparent
            )
        )
    }
}

