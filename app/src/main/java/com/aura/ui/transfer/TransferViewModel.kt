package com.aura.ui.transfer

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.data.repository.TransferRepository
import com.aura.ui.account.AccountViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.UnknownHostException

/**
 * ViewModel responsible for handling logic related to the Transfer screen.
 *
 * @property transferRepository Repository for fetching transfer data.
 * @property Context Application context for accessing SharedPreferences.
 */
class TransferViewModel(
    private val transferRepository: TransferRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    companion object {
        private const val KEY_USER_IDENTIFIER = "userIdentifier"
        private const val TAG = "TransferViewModel"
        private const val KEY_MAIN_ACCOUNT_BALANCE =
            "mainAccountBalance" // Key for main account balance
    }

    // Event to trigger navigation to AccountFragment
    private val _navigateToAccountEvent = MutableSharedFlow<Unit>()
    val navigateToAccountEvent: SharedFlow<Unit> get() = _navigateToAccountEvent

    // MutableStateFlow representing the state
    private val _state = MutableStateFlow<TransferState>(TransferState.Initial)
    val state: StateFlow<TransferState> get() = _state

    // Flow for error messages
    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> get() = _errorMessage


    // StateFlow to hold the current value of the transfer recipient field
    private val _transferRecipient = MutableStateFlow("")
    private val transferRecipient: StateFlow<String> get() = _transferRecipient

    // StateFlow to hold the current value of the transfer amount field
    private val _transferAmount = MutableStateFlow(0.0)
    private val transferAmount: StateFlow<Double> get() = _transferAmount

    // StateFlow to hold the current state of the transfer button (enabled/disabled)
    private val _isButtonMakeTransferEnabled = MutableStateFlow(false)
    val isButtonMakeTransferEnabled: StateFlow<Boolean> get() = _isButtonMakeTransferEnabled

    // Access the transferSender from SharedPreferences
    private var transferSender: String = ""

    // Access the mainAccountBalance from SharedPreferences
    private var mainAccountBalance: Double = 0.0

    init {
        Log.d(TAG, "init")

        // Load userIdentifier and transfer data asynchronously
        viewModelScope.launch {

            transferSender = getTransferSender().toString()
            Log.d(TAG, "Transfer sender loaded: $transferSender")
            mainAccountBalance = getMainAccountBalance()
            Log.d(TAG, "Main account balance loaded: $mainAccountBalance")

            combine(_transferRecipient, _transferAmount) { recipient, amount ->
                // Check if both recipient and amount fields are not blank and null
                recipient.isNotBlank() && amount != null
            }.collect {
                // Update the login button enabled state
                _isButtonMakeTransferEnabled.value = it
            }

            _state.value = TransferState.Initial
        }
    }

    /**
     * Function to update the transfer recipient field.
     * @param newRecipient the new value for the identifier field.
     */
    fun onFieldTransferRecipientChanged(newRecipient: String) {
        _transferRecipient.value = newRecipient
    }

    /**
     * Function to update the transfer amount field.
     * @param newAmount the new value for the password field.
     */
    fun onFieldTransferAmountChanged(newAmount: Double) {
        _transferAmount.value = newAmount
    }


    /**
     * Suspended function to get the transfer sender from SharedPreferences.
     *
     * @return The transfer sender or null if not found.
     */
    private suspend fun getTransferSender(): String? {
        return withContext(Dispatchers.IO) {
            sharedPreferences.getString(KEY_USER_IDENTIFIER, null)
        }
    }

    /**
     * Suspended function to get the main account balance from SharedPreferences.
     *
     * @return The main account balance or 0.0 if not found.
     */
    private suspend fun getMainAccountBalance(): Double {
        return withContext(Dispatchers.IO) {
            sharedPreferences.getFloat(KEY_MAIN_ACCOUNT_BALANCE, 0.0F).toDouble()
        }
    }

    /**
     * Function to perform transfer.
     * performs a network call, and handles errors.
     */
    fun onButtonMakeTransferClicked() {
        _state.value = TransferState.Loading

        // Perform network call to validate transfer and handle errors
        viewModelScope.launch {
            try {
                val isTransferPossible: Boolean = checkDataBeforeTransfer(
                    transferSender,
                    mainAccountBalance,
                    transferRecipient.value,
                    transferAmount.value
                )

                if (isTransferPossible) {
                    val (isTransferSuccessful, transferStatusCode) = makeTransfer(
                        transferSender,
                        transferRecipient.value,
                        transferAmount.value
                    )


                    // If transfer is successful, navigate to AccountFragment
                    if (isTransferSuccessful) {
                        _state.value = TransferState.Success
                        navigateToAccount()
                    } else {

                        // Handle different transfer status codes
                        handleTransferState(transferStatusCode)
                    }
                }
            } catch (e: IOException) {
                // Handle network errors (e.g., no Internet connection)
                _state.value =
                    TransferState.Error("Connection error. Please check your Internet connection.")
            } catch (e: UnknownHostException) {
                _state.value =
                    TransferState.Error("No Internet connection. Please check your connection.")
            } catch (e: Exception) {
                // Handle other types of errors
                _state.value =
                    TransferState.Error("An error occurred. Please try again.")

            }
        }
    }

    /**
     * Checks the data before performing the transfer.
     *
     * @param transferSender The sender of the transfer.
     * @param mainAccountBalance The main account balance.
     * @param transferRecipient The recipient of the transfer.
     * @param transferAmount The amount to be transferred.
     * @return True if the data is valid for the transfer, false otherwise.
     */
    private fun checkDataBeforeTransfer(
        transferSender: String?,
        mainAccountBalance: Double,
        transferRecipient: String?,
        transferAmount: Double
    ): Boolean {
        if (transferSender.isNullOrEmpty()) {
            _state.value =
                TransferState.Error("The sender of this transfer has not been found, please try again.")
            return false
        }
        if (mainAccountBalance == 0.0) {
            _state.value =
                TransferState.Error("The main account for this transfer has not been found or balance is null, please try again.")
            return false
        }
        if (transferRecipient.isNullOrEmpty()) {
            _state.value =
                TransferState.Error("The recipient for this transfer has not been found, please try again.")
            return false
        }
        if (transferAmount == 0.0) {
            _state.value =
                TransferState.Error("The amount of this transfer has not been found or is null, please try again.")
            return false
        }
        if (transferAmount > mainAccountBalance) {
            _state.value =
                TransferState.Error("The main account balance is too low for this transfer, please try again.")
            return false
        }
        if (transferSender == transferRecipient) {
            _state.value =
                TransferState.Error("The recipient must be different than the sender, please try again.")
            return false
        }

        // If all checks pass, the transfer is possible
        return true
    }

    /**
     * Makes the transfer by calling the repository and processing the response.
     *
     * @param transferSender The sender of the transfer.
     * @param transferRecipient The recipient of the transfer.
     * @param transferAmount The amount to be transferred.
     * @return A pair where the first element is a Boolean indicating whether the transfer was successful,
     *         and the second element is an Integer representing the transfer status code.
     */
    private suspend fun makeTransfer(
        transferSender: String,
        transferRecipient: String,
        transferAmount: Double
    ) =
        transferRepository.fetchTransferData(transferSender, transferRecipient, transferAmount)
            .firstOrNull()?.let { result ->
                Pair(result.isTransferSuccessful, result.transferStatusCode)
            } ?: Pair(false, 3)

    /**
     * Function to navigate to the user's account screen.
     * Triggers the navigation event to AccountFragment.
     */
    private fun navigateToAccount() {
        // Emitting the navigation event to navigate to AccountFragment
        viewModelScope.launch {
            _navigateToAccountEvent.emit(Unit)
        }
    }


    /**
     * Handles errors based on the transfer status code.
     *
     * @param transferStatusCode The status code returned from the transfer API.
     */
    private fun handleTransferState(transferStatusCode: Int) {
        when (transferStatusCode) {
            0 -> _state.value = TransferState.Error("HTTP status code 0: no response from API")
            1 -> _state.value =
                TransferState.Error("HTTP status code 1: API has not returned HTTP status code")

            2 -> _state.value = TransferState.Error("HTTP status code 2: unexpected error")
            3 -> _state.value =
                TransferState.Error("HTTP status code 3: error in function makeTransfer")

            200 -> _state.value = TransferState.Success
            in 3..99 -> _state.value =
                TransferState.Error("HTTP status code $transferStatusCode: Unknown Error")

            in 100..199 -> _state.value =
                TransferState.Error("HTTP status code $transferStatusCode: Information Error")

            in 201..299 -> _state.value =
                TransferState.Error("HTTP status code $transferStatusCode: Success Error")

            in 300..399 -> _state.value =
                TransferState.Error("HTTP status code $transferStatusCode: Redirection Error")

            in 400..499 -> _state.value =
                TransferState.Error("HTTP status code $transferStatusCode: Client Error")

            in 500..599 -> _state.value =
                TransferState.Error("HTTP status code $transferStatusCode: Server Error")

            in 600..999 -> _state.value =
                TransferState.Error("HTTP status code $transferStatusCode: Unknown Error")

            else -> _state.value = TransferState.Error("Unexpected Error. Please try again.")
        }
    }

}