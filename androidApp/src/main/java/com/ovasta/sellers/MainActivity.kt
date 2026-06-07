package com.ovasta.sellers

import android.Manifest
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import com.ovasta.sellers.base.LocaleHelper
import com.ovasta.sellers.ui.App

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        // Get user's language preference and apply to activity context
        val language = LocaleHelper.getLanguageFromPrefs(newBase)
        super.attachBaseContext(LocaleHelper.wrapContext(newBase, language))
    }

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

        setContent {
            App()
        }
    }
}
