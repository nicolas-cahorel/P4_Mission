package com.aura.data.model

import com.squareup.moshi.JsonClass

/**
 * Data class representing the request body for initiating a transfer.
 *
 * This class encapsulates the necessary details required to perform a transfer of funds between accounts.
 *
 * @property sender The identifier of the account from which the funds will be transferred.
 * @property recipient The identifier of the account to which the funds will be transferred.
 * @property amount The amount of funds to transfer.
 */
@JsonClass(generateAdapter = true)
data class TransferRequestBody(
    val sender: String,
    val recipient: String,
    val amount: Double
)