package com.aura.data.network

import com.aura.data.apiResponse.LoginApiResponse
import com.aura.data.apiResponse.TransferApiResponse
import com.aura.data.model.TransferRequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Retrofit interface for defining transfer-related API endpoints.
 */
interface TransferClient {

    /**
     * Makes a POST request to initiate a transfer between accounts.
     *
     * @param requestBody The body containing sender, recipient, and amount for the transfer.
     * @return A Retrofit [Response] wrapping a [TransferApiResponse].
     */
    @POST("/transfer")
    suspend fun postRequestForTransfer(
        @Body requestBody : TransferRequestBody
    ): Response<TransferApiResponse>
}