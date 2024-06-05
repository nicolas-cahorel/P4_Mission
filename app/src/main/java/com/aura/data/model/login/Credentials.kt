package com.aura.data.model.login

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the credentials required to log in to the application.
 * @property id The user's ID.
 * @property password The user's password.
 */
@Serializable
data class Credentials(
    @SerialName("id") val id: String,
    @SerialName("password") val password: String
)
