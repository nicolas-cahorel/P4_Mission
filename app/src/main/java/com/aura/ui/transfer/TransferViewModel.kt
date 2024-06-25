package com.aura.ui.transfer

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.data.model.AccountResultModel
import com.aura.data.repository.TransferRepository
import com.aura.ui.login.LoginState
import com.aura.ui.login.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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
    private var userIdentifier: String? = null

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

            userIdentifier = getUserIdentifier()
            if (userIdentifier != null) {
                Log.d(TAG, "User identifier loaded: $userIdentifier")
                loadAccountData(userIdentifier!!)
            } else {
                Log.e(TAG, "User identifier not found.")
                _state.value = TransferState.Error("User identifier not found.")
            }
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
    private suspend fun getUserIdentifier(): String? {
        return withContext(Dispatchers.IO) {
            sharedPreferences.getString(KEY_USER_IDENTIFIER, null)
        }
    }

    /**
     * Function to perform tansfer.
     * performs a network call, and handles errors.
     */
    fun onButtonMakeTransferClicked() {
        _state.value = TransferState.Loading

        // Perform network call to validate username and password and handle errors
        viewModelScope.launch {
            try {
                val (isLoginSuccessful, loginStatusCode) = validateCredentials(_userIdentifier.value, _userPassword.value)

                // If login is successful, navigate to HomeFragment
                if (isLoginSuccessful) {
                    sharedPreferences.edit().putString(LoginViewModel.KEY_USER_IDENTIFIER, _userIdentifier.value).apply()
                    Log.d(LoginViewModel.TAG, "User identifier saved: ${_userIdentifier.value}")
                    _state.value = LoginState.Success
                    navigateToAccount()
                } else {

                    // Handle different login status codes
                    handleLoginError(loginStatusCode)
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
                    handleAccountLoadingSuccess(accountStatusCode, mainAccountBalance)
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
            userIdentifier?.let { loadAccountData(it) }
        }
    }

    /**
     * Handles errors based on the account status code.
     *
     * @param accountStatusCode The status code returned from the account API.
     * @param mainAccountBalance The balance of the main account if found.
     */
    private fun handleAccountLoadingSuccess(accountStatusCode: Int, mainAccountBalance: Double?) {
        when (accountStatusCode) {
            0 -> _state.value = AccountState.Error("HTTP status code 0: no response from API")
            1 -> _state.value = AccountState.Error("HTTP status code 1: API has not returned HTTP status code")
            2 -> _state.value = AccountState.Error("HTTP status code 2: unexpected error")
            200 -> if (mainAccountBalance != null) {
                _state.value = AccountState.Success(mainAccountBalance)
                // Store main account balance in SharedPreferences
                sharedPreferences.edit().putFloat(KEY_MAIN_ACCOUNT_BALANCE, mainAccountBalance.toFloat()).apply()
            } else {
                _state.value = AccountState.Error("Main account not found.")
            }
            in 3..99 -> _state.value = AccountState.Error("HTTP status code $accountStatusCode: Unknown Error")
            in 100..199 -> _state.value = AccountState.Error("HTTP status code $accountStatusCode: Information Error")
            in 201..299 -> _state.value = AccountState.Error("HTTP status code $accountStatusCode: Success Error")
            in 300..399 -> _state.value = AccountState.Error("HTTP status code $accountStatusCode: Redirection Error")
            in 400..499 -> _state.value = AccountState.Error("HTTP status code $accountStatusCode: Client Error")
            in 500..599 -> _state.value = AccountState.Error("HTTP status code $accountStatusCode: Server Error")
            in 600..999 -> _state.value = AccountState.Error("HTTP status code $accountStatusCode: Unknown Error")
            else -> _state.value = AccountState.Error("Unexpected Error. Please try again.")
        }
    }

}
