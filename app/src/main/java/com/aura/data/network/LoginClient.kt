package com.aura.data.network

import com.aura.data.model.LoginCredentials
import com.aura.data.response.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginClient {
    @POST("/login")
    suspend fun postUserCredentialsForLogin(
        @Body credentials: LoginCredentials
        //@Query(value = "id") identifiant: String,
        //@Query(value = "password") motdepasse: String
    ): Response<ApiResponse>
}
