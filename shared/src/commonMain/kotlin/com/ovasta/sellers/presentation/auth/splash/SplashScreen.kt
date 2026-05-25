package com.ovasta.sellers.presentation.auth.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ovasta.sellers.base.Primary
import com.ovasta.sellers.base.ScreenDirectionEventHandler
import com.ovasta.sellers.base.White
import com.ovasta.sellers.base.lgSemiBold

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
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Ovasta Seller",
                modifier = Modifier.size(80.dp),
                tint = Primary
            )
            Text(
                text = "Ovasta",
                style = lgSemiBold.copy(fontSize = 32.sp, color = Primary)
            )
        }
    }
}
