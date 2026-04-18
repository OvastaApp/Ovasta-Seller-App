package com.ovasta.sellers.presentation.auth.login.data

import com.ovasta.sellers.data.ApiResponse
import com.ovasta.sellers.data.User

interface ILoginRemoteDataSource {
    suspend fun login(phone: String, password: String, userType: Int):  ApiResponse<User>

    suspend fun authenticateWithFirebase(
        firebaseAuthToken: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    )
}