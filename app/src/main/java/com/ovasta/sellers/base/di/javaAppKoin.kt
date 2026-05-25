package com.ovasta.sellers.base.di

import android.app.Application
import com.ovasta.sellers.di.createOrderModule
import com.ovasta.sellers.di.dataStoreModule
import com.ovasta.sellers.di.getSharedModules
import com.ovasta.sellers.di.homeModule
import com.ovasta.sellers.di.loginModule
import com.ovasta.sellers.di.orderHistoryModule
import com.ovasta.sellers.di.profileModule
import com.ovasta.sellers.di.splashModule
import com.ovasta.sellers.di.walletModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

fun startKoin(application: Application) {
    startKoin {
        androidContext(application)
        androidLogger(Level.DEBUG)
        modules(
            dataStoreModule,
            *getSharedModules().toTypedArray(),
            splashModule,
            loginModule,
            homeModule,
            createOrderModule,
            profileModule,
            orderHistoryModule,
            walletModule
        )
    }
}
