package com.aura.ui.account

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.data.model.AccountResultModel
import com.aura.data.repository.AccountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.UnknownHostException

/**
 * ViewModel responsible for handling logic related to the User Account screen.
 *
 * This ViewModel interacts with [AccountRepository] to fetch account data based on
 * the user identifier stored in [SharedPreferences]. It manages the UI state using
 * [MutableStateFlow] and broadcasts error messages through [MutableSharedFlow].
 *
 * @property accountRepository Repository for fetching account data.
 * @property sharedPreferences SharedPreferences for storing user data.
 */
class AccountViewModel(
    private val accountRepository: AccountRepository,
    private val sharedPreferences: SharedPreferences,
) : ViewModel() {

    companion object {
        private const val KEY_USER_IDENTIFIER = "userIdentifier"
        private const val KEY_MAIN_ACCOUNT_BALANCE = "mainAccountBalance"
    }

    // Event to trigger navigation to TransferFragment
    private val _navigateToTransferEvent = MutableSharedFlow<Unit>()
    val navigateToTransferEvent: SharedFlow<Unit> get() = _navigateToTransferEvent

    // MutableStateFlow representing the state
    private val _state = MutableStateFlow<AccountState>(AccountState.Loading)
    val state: StateFlow<AccountState> get() = _state

    // Flow for error messages
    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> get() = _errorMessage

    // Access the userIdentifier from SharedPreferences
    private var userIdentifier: String? = null

    init {
        _state.value = AccountState.Loading

        // Load userIdentifier and account data asynchronously
        viewModelScope.launch {
            userIdentifier = getUserIdentifier()
            if (userIdentifier != null) {
                loadAccountData(userIdentifier!!)
            } else {
                _state.value = AccountState.Error("User identifier not found.")
            }
        }
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
     * Loads account data for the given user identifier.
     *
     * @param userIdentifier The user identifier to fetch account data for.
     */
    fun loadAccountData(userIdentifier: String) {

        viewModelScope.launch {
            try {
                // Collect the flow from the repository
                accountRepository.fetchAccountData(userIdentifier).collect { accountsResult ->

                    // Extract data from the accounts result
                    val accounts = accountsResult.accounts
                    val mainAccountBalance = if (accounts.isNotEmpty()) {
                        findMainAccountBalance(accountsResult.accounts)
                    } else {
                        null // or handle appropriately if accounts being empty is unexpected
                    }
                    val accountStatusCode = accountsResult.accountStatusCode

                    // Handle the account status code
                    handleAccountState(accountStatusCode, mainAccountBalance)
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
                println("Exception: ${e.message}")
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
     * Function to navigate to the TransferFragment.
     *
     * Triggers the navigation event to TransferFragment via [navigateToTransferEvent].
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

        // Observe the userIdentifier from SharedPreferences
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
    private fun handleAccountState(accountStatusCode: Int, mainAccountBalance: Double?) {
        when (accountStatusCode) {
            0 -> _state.value = AccountState.Error("HTTP status code 0: no response from API")
            1 -> _state.value =
                AccountState.Error("HTTP status code 1: API has not returned HTTP status code")

            2 -> _state.value = AccountState.Error("HTTP status code 2: unexpected error")
            200 -> if (mainAccountBalance != null) {
                _state.value = AccountState.Success(mainAccountBalance)
                // Store main account balance in SharedPreferences
                sharedPreferences.edit()
                    .putFloat(KEY_MAIN_ACCOUNT_BALANCE, mainAccountBalance.toFloat()).apply()
            } else {
                _state.value = AccountState.Error("Main account not found.")
            }

            in 3..99 -> _state.value =
                AccountState.Error("HTTP status code $accountStatusCode: Unknown Error")

            in 100..199 -> _state.value =
                AccountState.Error("HTTP status code $accountStatusCode: Information Error")

            in 201..299 -> _state.value =
                AccountState.Error("HTTP status code $accountStatusCode: Success Error")

            in 300..399 -> _state.value =
                AccountState.Error("HTTP status code $accountStatusCode: Redirection Error")

            in 400..499 -> _state.value =
                AccountState.Error("HTTP status code $accountStatusCode: Client Error")

            in 500..599 -> _state.value =
                AccountState.Error("HTTP status code $accountStatusCode: Server Error")

            in 600..999 -> _state.value =
                AccountState.Error("HTTP status code $accountStatusCode: Unknown Error")

            else -> _state.value = AccountState.Error("Unexpected Error. Please try again.")
        }
    }
}