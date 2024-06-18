package com.aura.data.network

import com.aura.data.apiResponse.LoginApiResponse
import com.aura.data.model.LoginCredentials
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit interface for defining login related API endpoints.
 */
interface LoginClient {

    /**
     * Makes a POST request to the "/login" endpoint with user credentials.
     * @param credentials The user's login credentials.
     * @return A Retrofit [Response] wrapping an [LoginApiResponse].
     */
    @POST("/login")
    suspend fun postCredentialsForLogin(
        @Body credentials: LoginCredentials
    ): Response<LoginApiResponse>
}
