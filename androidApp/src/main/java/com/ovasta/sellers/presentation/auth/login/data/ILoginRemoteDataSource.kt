package com.ovasta.sellers.presentation.auth.login.data

import com.ovasta.sellers.domain.model.ApiResponse
import com.ovasta.sellers.domain.model.User

interface ILoginRemoteDataSource {
    suspend fun login(phone: String, password: String, userType: Int, fcmToken: String?):  ApiResponse<User>

    suspend fun authenticateWithFirebase(
        firebaseAuthToken: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    )
}