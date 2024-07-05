package com.aura.ui.login

/**
 * Sealed interface representing the different states of the login process.
 */
sealed interface LoginState {

    /**
     * Represents an initial state before any login attempt.
     */
    data object Initial : LoginState

    /**
     * Represents a loading state during the login attempt.
     */
    data object Loading : LoginState

    /**
     * Represents a success state indicating the result of the login attempt.
     */
    data object Success : LoginState

    /**
     * Represents an error state with a message describing the failure.
     *
     * @property message The error message describing why the login attempt failed.
     */
    data class Error(val message: String) : LoginState

}