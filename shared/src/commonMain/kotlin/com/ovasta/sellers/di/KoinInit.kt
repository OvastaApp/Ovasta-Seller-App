package com.ovasta.sellers.di

import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun initSharedKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            remoteModule,
            settingModule,
            platformModule()
        )
    }
}

fun getSharedModules(): List<Module> = listOf(
    remoteModule,
    settingModule,
    platformModule()
)
