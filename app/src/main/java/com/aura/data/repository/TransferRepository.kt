package com.aura.data.repository

import android.util.Log
import com.aura.data.model.AccountsResultModel
import com.aura.data.network.AccountClient
import com.aura.data.network.TransferClient
import com.aura.ui.transfer.TransferState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

/**
 * Repository responsible for handling user account-related data operations.
 * This class interacts with the [AccountClient] to perform user account API requests.
 */
class TransferRepository(private val dataService: TransferClient) {


}