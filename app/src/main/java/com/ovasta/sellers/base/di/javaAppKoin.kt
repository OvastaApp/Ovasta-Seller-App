package com.ovasta.sellers.base.di

import android.app.Application
import com.ovasta.sellers.base.local.di.resourcesModule
import com.ovasta.sellers.data.setting.di.settingModule
import com.ovasta.sellers.presentation.auth.login.di.loginModule
import com.ovasta.sellers.presentation.auth.splash.di.splashModule
import com.ovasta.sellers.presentation.createOrder.di.createOrderModule
import com.ovasta.sellers.presentation.home.di.homeModule
import com.ovasta.sellers.presentation.profile.di.profileModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

fun startKoin(application: Application) {
    startKoin {
        androidContext(application)
        printLogger(Level.DEBUG)
        modules(
            listOf(
                localModule,
                remoteModule,
                settingModule,
                firebaseModule,
                resourcesModule,
                hapticsModule,
                splashModule,
                loginModule,
                homeModule,
                createOrderModule,
                profileModule
            )
        )
    }
}