package com.aura.data.model

/**
 * Domain model representing the result of a user transfer.
 *
 * @property isTransferSuccessful Indicates whether the transfer was successful.
 * @property transferStatusCode The HTTP status code of the transfer request.
 */
data class TransferResultModel(
    val isTransferSuccessful: Boolean,
    val transferStatusCode: Int
)