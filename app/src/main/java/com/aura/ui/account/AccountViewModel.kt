package com.aura.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.data.model.AccountResultModel
import com.aura.data.repository.AccountRepository
import com.aura.ui.login.LoginViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.UnknownHostException

/**
 * ViewModel responsible for handling logic related to the UserAccount screen.
 *
 * @property loginViewModel Instance of LoginViewModel to access the user identifier.
 * @property accountRepository Repository for fetching account data.
 */
class UserAccountViewModel(
    private val loginViewModel: LoginViewModel,
    private val accountRepository: AccountRepository
) : ViewModel() {

    // Event to trigger navigation to TransferFragment
    private val _navigateToTransferEvent = MutableSharedFlow<Unit>()
    val navigateToTransferEvent: SharedFlow<Unit> get() = _navigateToTransferEvent

    // MutableStateFlow representing the state
    private val _state = MutableStateFlow<AccountState>(AccountState.Loading)

    // Expose state as StateFlow
    val state: StateFlow<AccountState> get() = _state

    // Flow for error messages
    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> get() = _errorMessage

    // Access the userIdentifier from LoginViewModel
    private val userIdentifier: StateFlow<String> get() = loginViewModel.userIdentifier

    init {
        _state.value = AccountState.Loading

        // Observe the userIdentifier from LoginViewModel
        viewModelScope.launch {
            userIdentifier.collect { userIdentifier ->
                loadAccountData(userIdentifier)
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


                    // Handle the account status code
                    when (val accountStatusCode = accountsResult.accountStatusCode) {

                        // 0 indicates no API response
                        0 -> _state.value =
                            AccountState.Error("HTTP status code 0: no response from API")
                        // 1 indicates that the HTTP status code of API response is null, check API response body
                        1 -> _state.value =
                            AccountState.Error("HTTP status code 1: API has not returned HTTP status code")
                        // 2 unexpected error happened, check values in LoginRepository
                        2 -> _state.value =
                            AccountState.Error("HTTP status code 2: unexpected error")
                        // 200 indicates success, handle display of account here
                        200 -> if (mainAccountBalance != null) {
                            _state.value = AccountState.Success(mainAccountBalance)
                        } else {
                            _state.value = AccountState.Error("Main account not found.")
                        }

                        else -> {
                            // Handle other HTTP status codes
                            when (accountStatusCode) {
                                in 3..99 -> _state.value =
                                    AccountState.Error("HTTP status code $accountStatusCode: Unknown Error")

                                in 100..199 -> _state.value =
                                    AccountState.Error(
                                        "HTTP status code $accountStatusCode: Information Error"
                                    )

                                in 201..299 -> _state.value =
                                    AccountState.Error(
                                        "HTTP status code $accountStatusCode: Success Error"
                                    )

                                in 300..399 -> _state.value =
                                    AccountState.Error(
                                        "HTTP status code $accountStatusCode: Redirection Error"
                                    )

                                in 400..499 -> _state.value =
                                    AccountState.Error(
                                        "HTTP status code $accountStatusCode: Client Error"
                                    )

                                in 500..599 -> _state.value =
                                    AccountState.Error(
                                        "HTTP status code $accountStatusCode: Server Error"
                                    )

                                in 600..999 -> _state.value =
                                    AccountState.Error(
                                        "HTTP status code $accountStatusCode: Unknown Error"
                                    )

                                else -> _state.value =
                                    AccountState.Error("Unexpected Error. Please try again.")
                            }
                        }
                    }
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
        val mainAccount = accounts.firstOrNull { it.isAccountMain }

        // Return the balance of the main account if it exists, otherwise return null
        return mainAccount?.accountBalance
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
            val userId = userIdentifier.first()
            loadAccountData(userId)
        }
    }
}
