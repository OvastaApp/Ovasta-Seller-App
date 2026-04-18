package com.ovasta.sellers.presentation.auth.login.data

import com.ovasta.sellers.data.ApiResponse
import com.ovasta.sellers.data.User

interface ILoginRepository {
    suspend fun login(phone: String, password: String, usertype: Int):  ApiResponse<User>

    suspend fun authenticateWithFirebase(
        firebaseAuthToken: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    )
}