package com.aura.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents an account.
 * @property id The account's ID.
 * @property main Whether the account is the user's main account.
 * @property balance The account's balance.
 */
@Serializable
data class Account(
    @SerialName("id") val id: String,
    @SerialName("main") val main: Boolean,
    @SerialName("balance") var balance: Double
)