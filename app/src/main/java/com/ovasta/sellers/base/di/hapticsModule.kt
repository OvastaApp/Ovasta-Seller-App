package com.ovasta.sellers.base.di

import com.ovasta.sellers.base.ext.OrderVibrator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val hapticsModule = module {
    single { OrderVibrator(androidContext()) }
}

