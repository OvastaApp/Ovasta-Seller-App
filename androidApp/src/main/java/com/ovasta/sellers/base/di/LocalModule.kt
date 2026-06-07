package com.ovasta.sellers.base.di


import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.ovasta.sellers.base.constants.SharedPreferenceConstants
import com.ovasta.sellers.data.preferences.LanguagePreferences
import com.ovasta.sellers.data.preferences.LanguagePreferencesImpl

import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val localModule = module {
    single {
        EncryptedSharedPreferences.create(
            androidApplication(),
            SharedPreferenceConstants.PREFERENCE_NAME,
            MasterKey.Builder(androidApplication())
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    // Language preferences for managing app locale
    single<LanguagePreferences> {
        LanguagePreferencesImpl(androidContext())
    }
}