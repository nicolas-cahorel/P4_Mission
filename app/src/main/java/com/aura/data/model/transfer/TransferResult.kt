package com.aura.data.model.transfer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the result of a transfer.
 * @property result Whether the transfer was successful.
 */
@Serializable
data class TransferResult(
    @SerialName("result") val result: Boolean,
)
