package com.ovasta.sellers.base.ext

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.ovasta.sellers.base.BaseViewModel
import com.ovasta.sellers.base.StringResourceProvider
import org.koin.compose.koinInject

@Composable
fun ToastEventHandler(
    viewModel: BaseViewModel,
    context: Context = LocalContext.current
) {
    val stringProvider: StringResourceProvider = koinInject()
    LaunchedEffect(Unit) {
        viewModel.toastEvent.collect { event ->
            when (event) {
                is ToastEvent.ResourceToastEvent -> {
                    val message = stringProvider.getString(event.stringId, *event.args.toTypedArray())
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
                is ToastEvent.StringToastEvent -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }
}
