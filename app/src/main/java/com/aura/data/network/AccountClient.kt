package com.aura.data.network

import com.aura.data.apiResponse.AccountApiResponse
import com.aura.data.apiResponse.AccountsApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit interface for defining user account related API endpoints.
 */
interface AccountClient {

    /**
     * Makes a GET request to retrieve user account information based on the provided ID.
     *
     * @param userId The identifier associated with the user's account.
     * @return A Retrofit [Response] wrapping an [AccountsApiResponse].
     */
    @GET("/accounts/{id}")
    suspend fun getUserAccount(
        @Path("id") userId: String
    ): Response<List<AccountApiResponse>>
}