package com.aura.ui.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.data.repository.LoginRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.UnknownHostException

/**
 * ViewModel responsible for handling user login logic.
 * Manages user input fields and the state of the login button.
 */
class LoginViewModel(
    private val loginRepository: LoginRepository,
    context: Context
) : ViewModel() {

    companion object {
        private const val PREFS_NAME = "LoginPrefs"
        private const val KEY_USER_IDENTIFIER = "userIdentifier"
        private const val TAG = "LoginViewModel"
    }

    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    //private val sharedPreferences = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)


    // Event to trigger navigation to AccountFragment
    private val _navigateToAccountEvent = MutableSharedFlow<Unit>()
    val navigateToAccountEvent: SharedFlow<Unit> get() = _navigateToAccountEvent

    // MutableStateFlow representing the state
    private val _state = MutableStateFlow<LoginState>(LoginState.Initial)

    // Expose state as StateFlow
    val state: StateFlow<LoginState> get() = _state

    // StateFlow to hold the current value of the user identifier field
    private val _userIdentifier = MutableStateFlow("")
    val userIdentifier: StateFlow<String> get() = _userIdentifier

    // StateFlow to hold the current value of the user password field
    private val _userPassword = MutableStateFlow("")
    val userPassword: StateFlow<String> get() = _userPassword

    // StateFlow to hold the current state of the login button (enabled/disabled)
    private val _isButtonLoginEnabled = MutableStateFlow(false)
    val isButtonLoginEnabled: StateFlow<Boolean> get() = _isButtonLoginEnabled


    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> get() = _errorMessage

    init {
        // Combine the values of identifier and password fields and update the state of the login button
        viewModelScope.launch {
            combine(_userIdentifier, _userPassword) { identifier, password ->
                // Check if both identifier and password fields are not blank
                identifier.isNotBlank() && password.isNotBlank()
            }.collect {
                // Update the login button enabled state
                _isButtonLoginEnabled.value = it
            }
        }
    }

    /**
     * Function to update the identifier field.
     * @param newIdentifier the new value for the identifier field.
     */
    fun onFieldUserIdentifierChanged(newIdentifier: String) {
        _userIdentifier.value = newIdentifier
    }

    /**
     * Function to update the password field.
     * @param newPassword the new value for the password field.
     */
    fun onFieldUserPasswordChanged(newPassword: String) {
        _userPassword.value = newPassword
    }

    /**
     * Function to perform login.
     * Validates the provided username and password,
     * performs a network call, and handles errors.
     */
    fun onButtonLoginClicked() {
        _state.value = LoginState.Loading

        // Perform network call to validate username and password and handle errors
        viewModelScope.launch {
            try {
                val (isLoginSuccessful, loginStatusCode) = validateCredentials(_userIdentifier.value, _userPassword.value)

                // If login is successful, navigate to HomeFragment
                if (isLoginSuccessful) {
                    sharedPreferences.edit().putString(KEY_USER_IDENTIFIER, _userIdentifier.value).apply()
                    Log.d(TAG, "User identifier saved: ${_userIdentifier.value}")
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
    private suspend fun validateCredentials(username: String, password: String) =
        loginRepository.fetchLoginData(username, password)
            .firstOrNull()?.let { result ->
                Pair(result.isLoginSuccessful, result.loginStatusCode)
            } ?: Pair(false, null)

    /**
     * Function to navigate to the user's account screen.
     * Triggers the navigation event to AccountFragment.
     */
    private fun navigateToAccount() {
        // Emitting the navigation event to navigate to UserAccountFragment
        viewModelScope.launch {
            _navigateToAccountEvent.emit(Unit)
        }
    }

    /**
     * Handles different login status codes and updates the state with appropriate error messages.
     * @param loginStatusCode The status code received from the login attempt.
     */
    private fun handleLoginError(loginStatusCode: Int?) {
        val errorMessage = when (loginStatusCode) {
            0 -> "HTTP status code 0: no response from API"
            1 -> "HTTP status code 1: API has not returned HTTP status code"
            2 -> "HTTP status code 2: unexpected error"
            200 -> "HTTP status code 200: incorrect identifiers"
            in 3..99 -> "HTTP status code $loginStatusCode: Unknown Error"
            in 100..199 -> "HTTP status code $loginStatusCode: Information Error"
            in 201..299 -> "HTTP status code $loginStatusCode: Success Error"
            in 300..399 -> "HTTP status code $loginStatusCode: Redirection Error"
            in 400..499 -> "HTTP status code $loginStatusCode: Client Error"
            in 500..599 -> "HTTP status code $loginStatusCode: Server Error"
            in 600..999 -> "HTTP status code $loginStatusCode: Unknown Error"
            else -> "Unexpected Error. Please try again."
        }
        _state.value = LoginState.Error(errorMessage)
    }

}