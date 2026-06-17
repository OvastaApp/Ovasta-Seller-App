package com.ovasta.sellers

import android.Manifest
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.ovasta.sellers.base.LocaleHelper
import com.ovasta.sellers.platform.LanguageChangeSignal
import com.ovasta.sellers.ui.App
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
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

        ActivityCompat.requestPermissions(this, permissions, 100)

        lifecycleScope.launch {
            LanguageChangeSignal.recreateFlow.collect {
                recreate()
            }
        }

        // Read the saved language AFTER attachBaseContext has applied the locale,
        // so this always reflects the language that is currently active in the UI.
        val language = LocaleHelper.getLanguageFromPrefs(this)
        val layoutDirection = if (language == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr

        setContent {
            App(layoutDirection = layoutDirection)
        }
    }
}
