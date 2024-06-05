package com.aura.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for handling user login logic.
 * Manages user input fields and the state of the login button.
 */
class LoginViewModel : ViewModel() {

    // Event to trigger navigation to HomeFragment
    private val _navigateToHomeEvent = MutableSharedFlow<Unit>()
    val navigateToHomeEvent: SharedFlow<Unit> get() = _navigateToHomeEvent

    /**
     * Function to navigate to the home screen.
     * Triggers the navigation event to HomeFragment.
     */
    fun navigateToHome() {
        // Emitting the navigation event to navigate to HomeFragment
        viewModelScope.launch {
            _navigateToHomeEvent.emit(Unit)
        }
    }

    // StateFlow to hold the current value of the user identifier field
    private val _identifier = MutableStateFlow("")
    val userIdentifier: StateFlow<String> get() = _identifier

    // StateFlow to hold the current value of the user password field
    private val _password = MutableStateFlow("")
    val userPassword: StateFlow<String> get() = _password

    // StateFlow to hold the current state of the login button (enabled/disabled)
    private val _isLoginEnabled = MutableStateFlow(false)
    val isButtonLoginEnabled: StateFlow<Boolean> get() = _isLoginEnabled

    init {
        // Combine the values of identifier and password fields and update the state of the login button
        viewModelScope.launch {
            combine(_identifier, _password) { identifier, password ->
                // Check if both identifier and password fields are not blank
                identifier.isNotBlank() && password.isNotBlank()
            }.collect {
                // Update the login button enabled state
                _isLoginEnabled.value = it
            }
        }
    }

    /**
     * Function to update the identifier field.
     * @param newIdentifier the new value for the identifier field.
     */
    fun onFieldUserIdentifierChanged(newIdentifier: String) {
        _identifier.value = newIdentifier
    }

    /**
     * Function to update the password field.
     * @param newPassword the new value for the password field.
     */
    fun onFieldUserPasswordChanged(newPassword: String) {
        _password.value = newPassword
    }

}