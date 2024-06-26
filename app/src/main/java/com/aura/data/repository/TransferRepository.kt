package com.aura.data.repository

import android.util.Log
import com.aura.data.model.TransferResultModel
import com.aura.data.network.TransferClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

/**
 * Repository responsible for handling transfer-related data operations.
 * This class interacts with the [TransferClient] to perform transfer API requests.
 */
class TransferRepository(private val dataService: TransferClient) {

    /**
     * Fetches transfer data based on the provided sender, recipient, and amount.
     * @param transferSender The sender of the transfer.
     * @param transferRecipient The recipient of the transfer.
     * @param transferAmount The amount to be transferred.
     * @return A [Flow] emitting [TransferResultModel] objects based on the API response.
     */
    fun fetchTransferData(
        transferSender: String,
        transferRecipient: String,
        transferAmount: Double
    ): Flow<TransferResultModel> = flow {

        // Make the API call to get transfer data
        val transferApiResponse =
            dataService.postTransfer(transferSender, transferRecipient, transferAmount)

        // Get the status code and response body from the API response
        val transferApiStatusCode = transferApiResponse.code()
        val transferApiResponseBody = transferApiResponse.body()

        // Determine the transfer result based on the response body and status code
        val transferApiResult = when {
            // Case 1: Both response body and status code are not null
            transferApiResponseBody != null && transferApiStatusCode != null -> {
                transferApiResponseBody.toDomainModel(transferApiStatusCode)
            }
            // Case 2: Response body is null but status code is not null
            transferApiResponseBody == null && transferApiStatusCode != null -> {
                TransferResultModel(false, transferApiStatusCode)
            }
            // Case 3: Response body is not null but status code is null
            transferApiResponseBody != null && transferApiStatusCode == null -> {
                transferApiResponseBody.toDomainModel(1)
            }
            // Case 4: Both response body and status code are null
            transferApiResponseBody == null && transferApiStatusCode == null -> {
                TransferResultModel(false, 0)
            }
            // Fallback case to handle unexpected scenarios
            else -> {
                TransferResultModel(false, 2)
            }
        }
        // Emit the transfer result
        emit(transferApiResult)

    }.catch { error ->
        // Log the error message
        Log.e("TransferRepository", error.message ?: "No exception message")
    }
}