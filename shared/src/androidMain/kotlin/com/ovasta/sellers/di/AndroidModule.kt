package com.ovasta.sellers.di

import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    // Android-specific dependencies provided by app module
}
