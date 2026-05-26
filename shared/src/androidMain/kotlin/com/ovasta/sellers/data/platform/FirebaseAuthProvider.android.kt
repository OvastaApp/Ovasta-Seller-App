package com.ovasta.sellers.data.platform

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

actual class FirebaseAuthProvider {
    private val auth = FirebaseAuth.getInstance()

    actual suspend fun signInAnonymously() {
        auth.signInAnonymously().await()
    }

    actual suspend fun getCurrentUserId(): String? = auth.currentUser?.uid

    actual suspend fun signOut() {
        auth.signOut()
    }
}
