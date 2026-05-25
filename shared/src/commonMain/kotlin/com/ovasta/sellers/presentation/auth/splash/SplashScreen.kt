package com.ovasta.sellers.presentation.auth.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ovasta.sellers.base.ScreenDirectionEventHandler
import com.ovasta.sellers.base.White

@Composable
fun SplashScreen(
    viewModel: SplashViewModel
) {

    ScreenDirectionEventHandler(
        viewModel = viewModel
    )


    Box(modifier = Modifier.fillMaxSize()) {
        SplashContent()
    }
}

@Composable
private fun SplashContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White),
        contentAlignment = Alignment.Center
    ) {
    }
}

@Composable
@Preview(showBackground = true)
fun SplashPreview() {
    SplashContent()
}
