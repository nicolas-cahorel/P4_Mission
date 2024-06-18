package com.aura.ui.login

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
class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    // Event to trigger navigation to HomeFragment
    private val _navigateToAccountEvent = MutableSharedFlow<Unit>()
    val navigateToAccountEvent: SharedFlow<Unit> get() = _navigateToAccountEvent

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
        val userID = _userIdentifier.value
        val password = _userPassword.value

        // Perform network call to validate username and password and handle errors
        viewModelScope.launch {
            try {
                val (isLoginSuccessful, loginStatusCode) = validateCredentials(userID, password)

                // If login is successful, navigate to HomeFragment
                if (isLoginSuccessful) {
                    navigateToAccount()
                } else {

                    // The body of API response is empty, handle HTTP status code
                    when (loginStatusCode) {

                        // 0 indicates no API response
                        0 -> _errorMessage.emit("HTTP status code 0: no response from API")
                        // 1 indicates that the HTTP status code of API response is null, check API response body
                        1 -> _errorMessage.emit("HTTP status code 1: API has not returned HTTP status code")
                        // 2 unexpected error happened, check values in LoginRepository
                        2 -> _errorMessage.emit("HTTP status code 2: unexpected error")
                        // 200 indicates that identifiers are incorrect when isLoginSuccessful = false
                        200 -> _errorMessage.emit("HTTP status code 200: incorrect identifiers")
                        else -> {
                            when {
                                loginStatusCode in 3..99 -> _errorMessage.emit("HTTP status code $loginStatusCode: Unknown Error")
                                loginStatusCode in 100..199 -> _errorMessage.emit("HTTP status code $loginStatusCode: Information Error")
                                loginStatusCode in 201..299 -> _errorMessage.emit("HTTP status code $loginStatusCode: Success Error")
                                loginStatusCode in 300..399 -> _errorMessage.emit("HTTP status code $loginStatusCode: Redirection Error")
                                loginStatusCode in 400..499 -> _errorMessage.emit("HTTP status code $loginStatusCode: Client Error")
                                loginStatusCode in 500..599 -> _errorMessage.emit("HTTP status code $loginStatusCode: Server Error")
                                loginStatusCode in 600..999 -> _errorMessage.emit("HTTP status code $loginStatusCode: Unknown Error")

                                // Handle other cases
                                else -> _errorMessage.emit("Unexpected Error. Please try again.")
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                // Handle network errors (e.g., no Internet connection)
                _errorMessage.emit("Connection error. Please check your Internet connection.")
            } catch (e: UnknownHostException) {
                _errorMessage.emit("No Internet connection. Please check your connection.")
            } catch (e: Exception) {
                // Handle other types of errors
                _errorMessage.emit("An error occurred. Please try again.")
            }
        }
    }



    // Function to validate credentials
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
}