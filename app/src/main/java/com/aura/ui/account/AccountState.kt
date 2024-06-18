package com.aura.ui.account

/**
 * Sealed interface representing the different states of the account.
 */
sealed interface AccountState {

    /**
     * Represents a loading state.
     */
    data object Loading : AccountState

    /**
     * Represents a success state with a balance.
     * @property balance The balance amount of the account.
     */
    data class Success(val balance: Double) : AccountState

    /**
     * Represents an error state with a message.
     * @property message The error message describing the failure.
     */
    data class Error(val message: String) : AccountState

}