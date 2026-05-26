package com.ovasta.sellers.presentation.auth.login.data

import com.google.firebase.auth.FirebaseAuth
import com.ovasta.sellers.domain.model.ApiResponse
import com.ovasta.sellers.domain.model.User
import com.ovasta.sellers.presentation.auth.login.data.model.LoginRequest
import com.ovasta.sellers.data.remote.LoginApiService

class LoginRemoteDataSource(private val loginApi: LoginApiService) :
    ILoginRemoteDataSource {

    override suspend fun login(phone: String, password: String, userType: Int, fcmToken: String?): ApiResponse<User> {
        val loginData = LoginRequest(mobile = phone, password = password, fcmToken = fcmToken)
        return loginApi.login(login = loginData)
    }

    override suspend fun authenticateWithFirebase(
        firebaseAuthToken: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        FirebaseAuth.getInstance().signInWithCustomToken(firebaseAuthToken)
            .addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener {
                onFailure()
            }
    }

}
