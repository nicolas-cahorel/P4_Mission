package com.aura.data.network

import com.aura.data.apiResponse.TransferApiResponse
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Retrofit interface for defining transfer-related API endpoints.
 */
interface TransferClient {

    /**
     * Makes a POST request to initiate a transfer between accounts.
     *
     * @param sender The identifier of the sender's account.
     * @param recipient The identifier of the recipient's account.
     * @param amount The amount to be transferred.
     * @return A Retrofit [Response] wrapping a [TransferApiResponse].
     */
    @POST("/transfer")
    suspend fun postTransfer(
        @Path("sender") sender: String,
        @Path("recipient") recipient: String,
        @Path("amount") amount: Double
    ): Response<TransferApiResponse>
}