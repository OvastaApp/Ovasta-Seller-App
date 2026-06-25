package com.ovasta.sellers.data.platform

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.io.File

actual class SecureStorage(private val context: Context) {
    private val prefsFileName = "ovasta_secure_prefs"

    private val prefs: SharedPreferences = createEncryptedPreferences()

    private fun buildMasterKey(): MasterKey =
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

    private fun createEncryptedPreferences(): SharedPreferences {
        return try {
            createPrefs(buildMasterKey())
        } catch (e: Exception) {
            // A corrupted keystore or encrypted prefs file can throw a variety of
            // exceptions (GeneralSecurityException / AEADBadTagException, IOException,
            // KeyStoreException). None are recoverable in place, so wipe the encrypted
            // prefs AND the master key and recreate from scratch. Without this the app
            // crashes on startup, since SecureStorage is created eagerly during Koin
            // initialization (UnauthorizedHandler -> SettingsRepository -> SecureStorage).
            deleteCorruptedPrefs()
            deleteMasterKey()
            try {
                createPrefs(buildMasterKey())
            } catch (e2: Exception) {
                // Even after wiping the keystore and prefs file, encryption can keep
                // failing (e.g. an unrecoverable AndroidKeyStore / StrongBox state).
                // SecureStorage is created eagerly during Koin init, so throwing here
                // crashes the whole app on startup. Fall back to plain SharedPreferences
                // as a last resort: degraded security beats an unstartable app.
                context.getSharedPreferences("${prefsFileName}_fallback", Context.MODE_PRIVATE)
            }
        }
    }

    private fun createPrefs(masterKey: MasterKey): SharedPreferences =
        EncryptedSharedPreferences.create(
            context,
            prefsFileName,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

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

    private fun deleteMasterKey() {
        try {
            val keyStore = java.security.KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            if (keyStore.containsAlias(MasterKey.DEFAULT_MASTER_KEY_ALIAS)) {
                keyStore.deleteEntry(MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            }
        } catch (e: Exception) {
            // Ignore keystore errors
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
