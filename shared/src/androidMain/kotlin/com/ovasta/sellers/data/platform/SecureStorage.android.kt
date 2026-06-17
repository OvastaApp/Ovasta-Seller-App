package com.ovasta.sellers.data.platform

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.io.File
import java.security.GeneralSecurityException

actual class SecureStorage(private val context: Context) {
    private val prefsFileName = "ovasta_secure_prefs"
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = createEncryptedPreferences()

    private fun createEncryptedPreferences(): SharedPreferences {
        return try {
            EncryptedSharedPreferences.create(
                context,
                prefsFileName,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: GeneralSecurityException) {
            // Handle corrupted keystore by deleting the encrypted preferences file
            // and recreating it
            deleteCorruptedPrefs()
            EncryptedSharedPreferences.create(
                context,
                prefsFileName,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }

    private fun deleteCorruptedPrefs() {
        try {
            val prefsFile = File(context.filesDir.parent, "shared_prefs/$prefsFileName.xml")
            if (prefsFile.exists()) {
                prefsFile.delete()
            }
        } catch (e: Exception) {
            // Ignore deletion errors
        }
    }

    actual fun getString(key: String): String? = prefs.getString(key, null)

    actual fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    actual fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }

    actual fun clear() {
        prefs.edit().clear().apply()
    }
}
