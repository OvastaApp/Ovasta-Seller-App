package com.ovasta.sellers.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ovasta.sellers.shared.resources.Res
import com.ovasta.sellers.shared.resources.logo
import com.ovasta.sellers.ui.base.BaseScreen
import com.ovasta.sellers.ui.base.LocalNavigator
import com.ovasta.sellers.ui.base.ScreenDirection
import org.jetbrains.compose.resources.painterResource

@Composable
fun SplashScreen(viewModel: SplashViewModel) {
    val navigator = LocalNavigator.current
    val destination by viewModel.destination.collectAsState()

    // StateFlow always delivers the current value to new collectors, so this
    // works correctly both on first launch and after activity recreation.
    LaunchedEffect(destination) {
        when (val dir = destination) {
            is ScreenDirection.Replace -> navigator.replace(dir.screen)
            else -> Unit
        }
    }

    BaseScreen(viewModel = viewModel) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(250.dp)
            )
        }
    }
}
