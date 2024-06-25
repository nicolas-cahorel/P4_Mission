package com.aura.data.network

import com.aura.data.apiResponse.TransferApiResponse
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Retrofit interface for defining user account related API endpoints.
 */
interface TransferClient {

    /**
     * Makes a GET request to retrieve user account information based on the provided ID.
     * @param userId The identifier associated with the user's account.
     * @return A Retrofit [Response] wrapping an [TransferApiResponse].
     */
    @POST("/transfer")
    suspend fun getUserAccount(
        @Path("sender") userId: String,
        @Path("recipient") recipientId: String,
        @Path("amount") transferAmount: Double
    ): Response<TransferApiResponse>
}