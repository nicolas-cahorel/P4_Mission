package com.aura.ui.transfer

import com.aura.ui.login.LoginState

/**
 * Sealed interface representing the different states of the account.
 */
sealed interface TransferState {

    data object Initial : TransferState

    /**
     * Represents a loading state during the login attempt.
     */
    /**
     * Represents a loading state.
     */
    data object Loading : TransferState

    /**
     * Represents a success state.
     */
    data object Success : TransferState

    /**
     * Represents an error state with a message.
     * @property message The error message describing the failure.
     */
    data class Error(val message: String) : TransferState

}