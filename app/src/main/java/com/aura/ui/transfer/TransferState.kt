package com.aura.ui.transfer


/**
 * Sealed interface representing the different states of the transfer process.
 */
sealed interface TransferState {

    /**
     * Represents the initial state before any transfer attempt.
     */
    data object Initial : TransferState

    /**
     * Represents a loading state during the transfer attempt.
     */
    data object Loading : TransferState

    /**
     * Represents a success state when the transfer is successful.
     */
    data object Success : TransferState

    /**
     * Represents an error state with a message.
     * @property message The error message describing the failure.
     */
    data class Error(val message: String) : TransferState

}