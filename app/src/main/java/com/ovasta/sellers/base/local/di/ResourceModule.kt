package com.ovasta.sellers.base.local.di

import com.ovasta.sellers.base.local.AppResources
import com.ovasta.sellers.base.local.repository.ResourcesRepository
import org.koin.dsl.module


val resourcesModule = module {
    single { AppResources(get()) }
    single { ResourcesRepository(get()) }
}