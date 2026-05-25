package com.ovasta.sellers.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.ovasta.sellers.base.components.sharedComposable.LocalNavigator

@Composable
fun ScreenDirectionEventHandler(
    viewModel: BaseViewModel
) {
    val navigator = LocalNavigator.current
    LaunchedEffect(Unit) {
        viewModel.screenDirectionEvent.collect { direction ->
            when (direction) {
                is ScreenDirection.Push -> navigator.push(direction.screen)
                is ScreenDirection.Pop -> navigator.pop()
                is ScreenDirection.Replace -> navigator.replace(direction.screen)
                is ScreenDirection.ReplaceAll -> navigator.replaceAll(direction.screen)
                null -> Unit
            }
        }
    }
}
