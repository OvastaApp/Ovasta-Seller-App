package com.ovasta.sellers.base.di

import com.ovasta.sellers.data.network.AuthTokenProvider
import com.ovasta.sellers.data.notification.FcmTokenRemoteDataSource
import com.ovasta.sellers.data.notification.IFcmTokenRemoteDataSource
import com.ovasta.sellers.data.network.SellerApiService
import com.ovasta.sellers.data.network.createHttpClient
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import org.koin.dsl.module

val remoteModule = module {
    single<AuthTokenProvider> { SessionAuthTokenProvider(get()) }
    single { SellerApiService(createHttpClient(get())) }
    single<IFcmTokenRemoteDataSource> { FcmTokenRemoteDataSource(get()) }
    single<FirebaseFirestore> { Firebase.firestore }
}