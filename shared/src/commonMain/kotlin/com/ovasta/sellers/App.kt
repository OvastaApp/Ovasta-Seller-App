package com.ovasta.sellers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ovasta.sellers.base.Primary
import com.ovasta.sellers.base.components.sharedComposable.LocalNavigator
import com.ovasta.sellers.base.components.sharedComposable.Navigator
import com.ovasta.sellers.presentation.auth.login.presentation.LoginScreen
import com.ovasta.sellers.presentation.auth.login.presentation.LoginViewModel
import com.ovasta.sellers.presentation.auth.splash.SplashScreen
import com.ovasta.sellers.presentation.auth.splash.SplashViewModel
import com.ovasta.sellers.presentation.createOrder.presentation.CreateOrderScreen
import com.ovasta.sellers.presentation.createOrder.presentation.CreateOrderViewModel
import com.ovasta.sellers.presentation.home.presentation.HomeScreen
import com.ovasta.sellers.presentation.home.presentation.HomeViewModel
import com.ovasta.sellers.presentation.nav.AppRoute
import com.ovasta.sellers.presentation.profile.orderhistory.presentation.OrderHistoryViewModel
import com.ovasta.sellers.presentation.profile.orderhistory.presentation.OrdersScreen
import com.ovasta.sellers.presentation.profile.profile.presentation.ProfileScreen
import com.ovasta.sellers.presentation.profile.profile.presentation.ProfileViewModel
import com.ovasta.sellers.presentation.profile.wallet.presentation.WalletScreen
import com.ovasta.sellers.platform.PlatformBackHandler
import com.ovasta.sellers.presentation.profile.wallet.presentation.WalletViewModel
import com.ovasta.sellers.ui.theme.OvastaSellersTheme
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    OvastaSellersTheme {
        AppNavHost()
    }
}

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val backStack: SnapshotStateList<Any> = remember { mutableStateListOf<Any>(AppRoute.Splash) }
    val navigator = remember { Navigator(backStack) }

    val currentScreen = backStack.lastOrNull()
    var selectedBottomNav by remember { mutableStateOf<AppRoute>(AppRoute.Home) }

    LaunchedEffect(currentScreen) {
        when (currentScreen) {
            is AppRoute.Home -> selectedBottomNav = AppRoute.Home
            is AppRoute.Profile -> selectedBottomNav = AppRoute.Profile
            else -> {}
        }
    }

    CompositionLocalProvider(LocalNavigator provides navigator) {
        PlatformBackHandler(
            enabled = backStack.size > 1,
            onBack = { navigator.pop() }
        )

        Scaffold(
            bottomBar = {
                if (currentScreen is AppRoute.Home || currentScreen is AppRoute.Profile) {
                    AppBottomBar(
                        selected = selectedBottomNav,
                        onItemSelected = { route ->
                            if (selectedBottomNav != route) {
                                while (backStack.lastOrNull() is AppRoute.Home || backStack.lastOrNull() is AppRoute.Profile) {
                                    backStack.removeAt(backStack.size - 1)
                                }
                                backStack.add(route)
                            }
                        }
                    )
                }
            }
        ) { paddingValues ->
            AppScreenContent(
                modifier = Modifier.padding(paddingValues),
                route = currentScreen,
                backStack = backStack,
                navigator = navigator
            )
        }
    }
}

@Composable
private fun AppScreenContent(
    modifier: Modifier = Modifier,
    route: Any?,
    backStack: SnapshotStateList<Any>,
    navigator: Navigator
) {
    Box(modifier = modifier) {
        when (route) {
            is AppRoute.Splash -> {
                val viewModel: SplashViewModel = koinViewModel()
                SplashScreen(viewModel)
            }
            is AppRoute.Login -> {
                val viewModel: LoginViewModel = koinViewModel()
                LoginScreen(viewModel)
            }
            is AppRoute.Home -> {
                val viewModel: HomeViewModel = koinViewModel()
                LaunchedEffect(backStack.size) {
                    viewModel.loadHomeData(isRefresh = true)
                }
                HomeScreen(viewModel)
            }
            is AppRoute.CreateOrder -> {
                val viewModel: CreateOrderViewModel = koinViewModel(key = route.id.toString())
                CreateOrderScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navigator.pop() }
                )
            }
            is AppRoute.Profile -> {
                val viewModel: ProfileViewModel = koinViewModel()
                ProfileScreen(viewModel)
            }
            is AppRoute.LastOrders -> {
                val viewModel: OrderHistoryViewModel = koinViewModel()
                OrdersScreen(
                    viewModel,
                    onNavigateBack = { navigator.pop() }
                )
            }
            is AppRoute.Wallet -> {
                val viewModel: WalletViewModel = koinViewModel()
                WalletScreen(
                    viewModel,
                    onNavigateBack = { navigator.pop() }
                )
            }
            null -> {
                Text("Unknown route")
            }
        }
    }
}

@Composable
fun AppBottomBar(
    selected: AppRoute,
    onItemSelected: (AppRoute) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        contentColor = Primary,
        tonalElevation = 0.dp
    ) {
        BottomNavItem.items.forEach { item ->
            val isSelected = selected == item.route
            NavigationBarItem(
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = null,
                        tint = if (isSelected) Primary else Color.Gray.copy(alpha = 0.5f)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (isSelected) Primary else Color.Gray.copy(alpha = 0.5f)
                    )
                },
                selected = isSelected,
                onClick = { onItemSelected(item.route) }
            )
        }
    }
}

private sealed class BottomNavItem(
    val route: AppRoute,
    val label: String,
    val icon: ImageVector
) {
    object HomeBottomNav : BottomNavItem(AppRoute.Home, "Home", Icons.Default.Home)
    object ProfileBottomNav : BottomNavItem(AppRoute.Profile, "Profile", Icons.Default.Person)

    companion object {
        val items = listOf(HomeBottomNav, ProfileBottomNav)
    }
}
