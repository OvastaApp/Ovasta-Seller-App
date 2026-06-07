package com.ovasta.sellers.presentation.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.ovasta.sellers.base.Primary
import com.ovasta.sellers.base.components.sharedComposable.LocalNavigator
import com.ovasta.sellers.base.components.sharedComposable.Navigator
import com.ovasta.sellers.ui.screens.*
import com.ovasta.sellers.ui.screens.login.LoginScreen
import com.ovasta.sellers.ui.screens.login.LoginViewModel
import com.ovasta.sellers.ui.screens.splash.SplashScreen
import com.ovasta.sellers.ui.screens.splash.SplashViewModel
import com.ovasta.sellers.ui.screens.createorder.CreateOrderScreen
import com.ovasta.sellers.ui.screens.createorder.CreateOrderViewModel
import com.ovasta.sellers.ui.screens.home.HomeScreen
import com.ovasta.sellers.ui.screens.home.HomeViewModel
import com.ovasta.sellers.ui.screens.profile.ProfileScreen
import com.ovasta.sellers.ui.screens.profile.ProfileViewModel
import com.ovasta.sellers.base.smSemiBold
import com.ovasta.sellers.base.xsMedium
import org.koin.androidx.compose.koinViewModel
import androidx.annotation.StringRes
import androidx.compose.ui.res.stringResource
import com.ovasta.sellers.R
import com.ovasta.sellers.ui.screens.orderhistory.OrderHistoryViewModel
import com.ovasta.sellers.ui.screens.orderhistory.OrderHistoryScreen
import com.ovasta.sellers.ui.screens.wallet.WalletScreen
import com.ovasta.sellers.ui.screens.wallet.WalletViewModel


private sealed class BottomNavItem(
    val route: Any,
    @StringRes val labelRes: Int,
    val icon: ImageVector
) {
    object HomeBottomNav : BottomNavItem(Home, R.string.home, Icons.Default.Home)
    object ProfileBottomNav : BottomNavItem(Profile, R.string.profile, Icons.Default.Person)

    companion object {
        val items = listOf(HomeBottomNav, ProfileBottomNav)
    }
}

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val backStack = remember { mutableStateListOf<Any>(Splash) }
    val navigator = remember { Navigator(backStack) }

    // Track current bottom nav selection
    val currentScreen = backStack.lastOrNull()
    var selectedBottomNav by remember { mutableStateOf<Any>(Home) }

    // Keep bottom nav selection in sync with navigation
    LaunchedEffect(currentScreen) {
        if (currentScreen is Home || currentScreen is Profile) {
            selectedBottomNav = currentScreen
        }
    }

    CompositionLocalProvider(LocalNavigator provides navigator) {
        Scaffold(
            bottomBar = {
                if (currentScreen is Home || currentScreen is Profile) {
                    AppBottomBar(
                        selected = selectedBottomNav,
                        onItemSelected = { route ->
                            if (selectedBottomNav != route) {
                                while (backStack.lastOrNull() is Home || backStack.lastOrNull() is Profile) {
                                    backStack.removeAt(backStack.size - 1)
                                }
                                backStack.add(route)
                            }
                        }
                    )
                }
            }
        ) { paddingValues ->
            NavDisplay(
                modifier = modifier.padding(paddingValues),
                backStack = backStack,
                onBack = { navigator.pop() },
                entryProvider = { key ->
                    when (key) {
                        is Splash -> NavEntry(key) {
                            val viewModel: SplashViewModel = koinViewModel()
                            SplashScreen(viewModel)
                        }

                        is Login -> NavEntry(key) {
                            val viewModel: LoginViewModel = koinViewModel()
                            LoginScreen(viewModel)
                        }

                        is Home -> NavEntry(key) {
                            val viewModel: HomeViewModel = koinViewModel()
                            LaunchedEffect(backStack.size) {
                                viewModel.loadHomeData(isRefresh = true)
                            }
                            HomeScreen(viewModel)
                        }

                        is CreateOrder -> NavEntry(key) {
                            val viewModel: CreateOrderViewModel =
                                koinViewModel(key = key.id.toString())
                            CreateOrderScreen(viewModel = viewModel)
                        }

                        is Profile -> NavEntry(key) {
                            val viewModel: ProfileViewModel = koinViewModel()
                            ProfileScreen(viewModel)
                        }

                        is LastOrders -> NavEntry(key) {
                            val viewModel: OrderHistoryViewModel = koinViewModel()
                            OrderHistoryScreen(viewModel)
                        }

                        is Wallet -> NavEntry(key) {
                            val viewModel: WalletViewModel = koinViewModel()
                            WalletScreen(viewModel)
                        }

                        else -> NavEntry(Unit) { Text("Unknown route") }
                    }
                }
            )
        }
    }
}

@Composable
fun AppBottomBar(
    selected: Any,
    onItemSelected: (Any) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        elevation = 8.dp
    ) {
        BottomNavigation(
            backgroundColor = Color.White,
            contentColor = Primary,
            elevation = 0.dp,
            modifier = Modifier
                .navigationBarsPadding()
                .height(64.dp)
        ) {
            BottomNavItem.items.forEach { item ->
                val isSelected = selected == item.route
                BottomNavigationItem(
                    icon = {
                        if (isSelected) {
                            Surface(
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                                color = Primary.copy(alpha = 0.12f),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    item.icon,
                                    contentDescription = "home icon",
                                    tint = Primary,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                )
                            }
                        } else {
                            Icon(
                                item.icon,
                                contentDescription = "profile icon",
                                tint = Color.Gray.copy(alpha = 0.5f)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = stringResource(id = item.labelRes),
                            color = if (isSelected) Primary else Color.Gray.copy(alpha = 0.5f),
                            style = if (isSelected) smSemiBold else xsMedium
                        )
                    },
                    selected = isSelected,
                    onClick = { onItemSelected(item.route) },
                    selectedContentColor = Primary,
                    unselectedContentColor = Color.Gray.copy(alpha = 0.5f),
                    alwaysShowLabel = true
                )
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewHomeNavigationBar() {
    AppNavHost()
}

@Preview(showBackground = true)
@Composable
fun PreviewBottomBar_HomeSelected() {
    MaterialTheme {
        AppBottomBar(
            selected = Home,
            onItemSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBottomBar_ProfileSelected() {
    MaterialTheme {
        AppBottomBar(
            selected = Profile,
            onItemSelected = {}
        )
    }
}