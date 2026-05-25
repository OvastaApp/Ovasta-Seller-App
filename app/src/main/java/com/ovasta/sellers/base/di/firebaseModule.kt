package com.ovasta.sellers.base.di

import com.ovasta.sellers.platform.FirebaseProvider
import org.koin.core.module.Module
import org.koin.dsl.module

val firebaseModule: Module = module {
    single { FirebaseProvider() }
}
