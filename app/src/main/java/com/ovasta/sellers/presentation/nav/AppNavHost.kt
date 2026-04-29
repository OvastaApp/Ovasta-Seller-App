package com.ovasta.sellers.presentation.nav

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.ovasta.sellers.presentation.auth.login.presentation.LoginScreen
import com.ovasta.sellers.presentation.auth.login.presentation.LoginViewModel
import com.ovasta.sellers.presentation.auth.splash.SplashScreen
import com.ovasta.sellers.presentation.auth.splash.SplashViewModel
import com.ovasta.sellers.presentation.createOrder.presentation.CreateOrderScreen
import com.ovasta.sellers.presentation.createOrder.presentation.CreateOrderViewModel
import com.ovasta.sellers.presentation.home.presentation.HomeScreen
import com.ovasta.sellers.presentation.home.presentation.HomeViewModel
import com.ovasta.sellers.presentation.profile.presentation.ProfileScreen
import com.ovasta.sellers.presentation.profile.presentation.ProfileViewModel
import org.koin.androidx.compose.koinViewModel

data object Splash
data object Login
data object Home
data object Profile
data class CreateOrder(val id: Long = System.currentTimeMillis())

private sealed class BottomNavItem(val route: Any, val label: String, val icon: ImageVector) {
    object HomeBottomNav : BottomNavItem(Home, "الرئيسية", Icons.Default.Home)
    object ProfileBottomNav : BottomNavItem(Profile, "الملف الشخصي", Icons.Default.Person)
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
                            HomeScreen(viewModel)
                        }

                        is Profile -> NavEntry(key) {
                            val viewModel: ProfileViewModel = koinViewModel()
                            ProfileScreen(viewModel)
                        }

                        is CreateOrder -> NavEntry(key) {
                            val viewModel: CreateOrderViewModel =
                                koinViewModel(key = key.id.toString())
                            CreateOrderScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navigator.pop() })
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
    BottomNavigation(
        backgroundColor = Color.White,
        contentColor = Primary
    ) {
        BottomNavItem.items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = selected == item.route,
                onClick = { onItemSelected(item.route) }
            )
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