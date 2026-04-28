package com.ovasta.sellers.presentation.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
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
import org.koin.androidx.compose.koinViewModel

data object Splash
data object Login
data object Home
data class CreateOrder(val id: Long = System.currentTimeMillis())

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val backStack = remember { mutableStateListOf<Any>(Splash) }
    val navigator = remember { Navigator(backStack) }

    CompositionLocalProvider(LocalNavigator provides navigator) {
        Scaffold(bottomBar = {}) { paddingValues ->
            NavDisplay(
                modifier = modifier.padding(paddingValues),
                backStack = backStack,
                onBack = { navigator.pop() }, // use navigator

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

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewHomeNavigationBar() {
    AppNavHost()
}