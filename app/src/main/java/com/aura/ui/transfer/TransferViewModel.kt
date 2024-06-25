package com.aura.ui.transfer

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.data.model.AccountResultModel
import com.aura.data.repository.TransferRepository
import com.aura.ui.login.LoginState
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
 * ViewModel responsible for handling logic related to the UserAccount screen.
 *
 * @property loginViewModel Instance of LoginViewModel to access the user identifier.
 * @property accountRepository Repository for fetching account data.
 */
class TransferViewModel(
    private val transferRepository: TransferRepository,
    context: Context
) : ViewModel() {

    companion object {
        private const val PREFS_NAME = "LoginPrefs"
        private const val KEY_USER_IDENTIFIER = "userIdentifier"
        private const val TAG = "AccountViewModel"
        private const val KEY_MAIN_ACCOUNT_BALANCE = "mainAccountBalance" // Nouvelle clé pour le solde du compte principal
    }

    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Event to trigger navigation to TransferFragment
    private val _navigateToTransferEvent = MutableSharedFlow<Unit>()
    val navigateToTransferEvent: SharedFlow<Unit> get() = _navigateToTransferEvent

    // MutableStateFlow representing the state
    private val _state = MutableStateFlow<TransferState>(TransferState.Initial)

    // Expose state as StateFlow
    val state: StateFlow<TransferState> get() = _state

    // Flow for error messages
    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> get() = _errorMessage

    // Access the userIdentifier from LoginViewModel
    private var transferSender: String = ""

    // StateFlow to hold the current value of the transfer recipient field
    private val _transferRecipient = MutableStateFlow("")
    val transferRecipient: StateFlow<String> get() = _transferRecipient

    // StateFlow to hold the current value of the transfer amount field
    private val _transferAmount = MutableStateFlow(00.00)
    val transferAmount: StateFlow<Double> get() = _transferAmount

    // StateFlow to hold the current state of the login button (enabled/disabled)
    private val _isButtonMakeTransferEnabled = MutableStateFlow(false)
    val isButtonMakeTransferEnabled: StateFlow<Boolean> get() = _isButtonMakeTransferEnabled

    init {
        _state.value = TransferState.Initial

        // Load userIdentifier and transfer data asynchronously
        viewModelScope.launch {
            combine(_transferRecipient, _transferAmount) { recipient, amount ->
                // Check if both recipient and amount fields are not blank and null
                recipient.isNotBlank() && amount != null
            }.collect {
                // Update the login button enabled state
                _isButtonMakeTransferEnabled.value = it
            }

            transferSender = getTransferSender().toString()
            Log.d(TAG, "Transfer sender loaded: $transferSender")
            loadAccountData(transferSender)
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
     * Suspended function to get the user identifier from SharedPreferences.
     *
     * @return The user identifier or null if not found.
     */
    private suspend fun getTransferSender(): String? {
        return withContext(Dispatchers.IO) {
            sharedPreferences.getString(KEY_USER_IDENTIFIER, null)
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
                val (isTransferSuccessful, transferStatusCode) = validateTransfer(transferSender, _transferRecipient.value, _transferAmount.value)

                // If transfer is successful, navigate to HomeFragment
                if (isTransferSuccessful) {
                    _state.value = TransferState.Success
                    //navigateToAccount()
                } else {

                    // Handle different login status codes
                    handleTransferError(transferStatusCode)
                }
            } catch (e: IOException) {
                // Handle network errors (e.g., no Internet connection)
                _state.value =
                    LoginState.Error("Connection error. Please check your Internet connection.")
            } catch (e: UnknownHostException) {
                _state.value =
                    LoginState.Error("No Internet connection. Please check your connection.")
            } catch (e: Exception) {
                // Handle other types of errors
                _state.value =
                    LoginState.Error("An error occurred. Please try again.")
            }
        }
    }

    /**
     * Validates the provided username and password by making a network call to fetch login data.
     *
     * This function uses the [loginRepository] to perform the network call, and processes the response to determine
     * if the login was successful and what the status code was.
     *
     * @param username The username provided by the user for login.
     * @param password The password provided by the user for login.
     * @return A pair where the first element is a Boolean indicating whether the login was successful,
     *         and the second element is an Integer representing the login status code. If the network call
     *         does not return a result, the function returns a pair of (false, null).
     */
    private suspend fun validateTransfer(transferSender: String, transferRecipient: String, transferAmount: Double) =
        transferRepository.fetchTransferData(transferSender, transferRecipient, transferAmount)
            .firstOrNull()?.let { result ->
                Pair(result.isLoginSuccessful, result.loginStatusCode)
            } ?: Pair(false, null)

    /**
     * Loads account data for the given user identifier.
     *
     * @param userIdentifier The user identifier to fetch account data for.
     */
    private fun loadAccountData(userIdentifier: String) {

        viewModelScope.launch {
            try {
                // Collect the flow from the repository
                accountRepository.fetchAccountData(userIdentifier).collect { accountsResult ->

                    // Extract data from the accounts result
                    val mainAccountBalance = findMainAccountBalance(accountsResult.accounts)
                    val accountStatusCode = accountsResult.accountStatusCode

                    // Handle the account status code
                    handleTransferError(accountStatusCode, mainAccountBalance)
                }

            } catch (e: IOException) {
                // Handle network errors (e.g., no Internet connection)
                _state.value =
                    AccountState.Error("Connection error. Please check your Internet connection.")
            } catch (e: UnknownHostException) {
                _state.value =
                    AccountState.Error("No Internet connection. Please check your connection.")
            } catch (e: Exception) {
                // Handle other types of errors
                _state.value = AccountState.Error("An error occurred. Please try again.")
            }
        }
    }

    /**
     * Finds the balance of the main account from a list of accounts.
     *
     * @param accounts The list of accounts to search through.
     * @return The balance of the main account if found, otherwise null.
     */
    private fun findMainAccountBalance(accounts: List<AccountResultModel>): Double? {
        // Filter the list to get the main account and get its balance
        val mainAccountBalance = accounts.firstOrNull { it.isAccountMain }

        // Return the balance of the main account if it exists, otherwise return null
        return mainAccountBalance?.accountBalance
    }

    /**
     * Function to navigate to the user's account screen.
     * Triggers the navigation event to UserAccountFragment.
     */
    fun navigateToTransfer() {
        // Emitting the navigation event to navigate to TransferFragment
        viewModelScope.launch {
            _navigateToTransferEvent.emit(Unit)

        }
    }

    /**
     * Reloads the account data when the reload button is clicked.
     */
    fun onButtonReloadClicked() {
        _state.value = AccountState.Loading

        // Observe the userIdentifier from LoginViewModel
        viewModelScope.launch {
            transferSender?.let { loadAccountData(it) }
        }
    }

    /**
     * Handles errors based on the account status code.
     *
     * @param transferStatusCode The status code returned from the account API.
     * @param mainAccountBalance The balance of the main account if found.
     */
    private fun handleTransferError(transferStatusCode: Int, mainAccountBalance: Double?) {
        when (transferStatusCode) {
            0 -> _state.value = TransferState.Error("HTTP status code 0: no response from API")
            1 -> _state.value = TransferState.Error("HTTP status code 1: API has not returned HTTP status code")
            2 -> _state.value = TransferState.Error("HTTP status code 2: unexpected error")
            200 -> _state.value = TransferState.Success
            in 3..99 -> _state.value = TransferState.Error("HTTP status code $transferStatusCode: Unknown Error")
            in 100..199 -> _state.value = TransferState.Error("HTTP status code $transferStatusCode: Information Error")
            in 201..299 -> _state.value = TransferState.Error("HTTP status code $transferStatusCode: Success Error")
            in 300..399 -> _state.value = TransferState.Error("HTTP status code $transferStatusCode: Redirection Error")
            in 400..499 -> _state.value = TransferState.Error("HTTP status code $transferStatusCode: Client Error")
            in 500..599 -> _state.value = TransferState.Error("HTTP status code $transferStatusCode: Server Error")
            in 600..999 -> _state.value = TransferState.Error("HTTP status code $transferStatusCode: Unknown Error")
            else -> _state.value = TransferState.Error("Unexpected Error. Please try again.")
        }
    }

}
