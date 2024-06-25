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
     * Represents a success state with a balance.
     * @property balance The balance amount of the account.
     */
    data class Success(val balance: Double) : TransferState

    /**
     * Represents an error state with a message.
     * @property message The error message describing the failure.
     */
    data class Error(val message: String) : TransferState

}