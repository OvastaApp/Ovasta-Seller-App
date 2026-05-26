package com.ovasta.sellers.data.platform

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

actual class FirestoreProvider {
    private val firestore = FirebaseFirestore.getInstance()

    actual suspend fun saveUserData(userId: String, data: Map<String, Any>) {
        firestore.collection("users").document(userId).set(data).await()
    }

    actual suspend fun getUserData(userId: String): Map<String, Any>? {
        return firestore.collection("users").document(userId).get().await().data
    }
}
