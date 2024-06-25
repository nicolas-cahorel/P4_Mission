package com.aura.data.repository

import android.util.Log
import com.aura.data.model.AccountsResultModel
import com.aura.data.model.LoginResultModel
import com.aura.data.model.TransferResultModel
import com.aura.data.network.AccountClient
import com.aura.data.network.TransferClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

/**
 * Repository responsible for handling user account-related data operations.
 * This class interacts with the [AccountClient] to perform user account API requests.
 */
class TransferRepository(private val dataService: TransferClient) {

    /**
     * Fetches transfer data based on the provided sender, recipient and amount.
     * @param username The username used for authentication.
     * @param password The password associated with the username.
     * @return A [Flow] emitting [LoginResultModel] objects based on the API response.
     */
    fun fetchTransferData(transferSender: String, transferRecipient: String, transferAmount : Double): Flow<TransferResultModel> = flow <TransferResultModel>{
        val transferApiResponse = dataService.getUserAccount(transferSender, transferRecipient, transferAmount)

        val transferApiStatusCode = transferApiResponse.code()
        val transferApiResponseBody = transferApiResponse.body()

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
        emit(transferApiResult)

    }.catch { error ->
        Log.e("TransferRepository", error.message ?: "No exception message")
        // You can also emit some error state or handle the error in another way
    }
}