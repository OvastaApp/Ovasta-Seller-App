package com.ovasta.sellers

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import com.ovasta.sellers.presentation.nav.AppNavHost
import com.ovasta.sellers.ui.theme.OvastaSellersTheme

class MainActivity : ComponentActivity() {

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.POST_NOTIFICATIONS,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        ActivityCompat.requestPermissions(
            this, permissions, 100
        )
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            OvastaSellersTheme {
                AppNavHost()
            }
        }
    }
}
