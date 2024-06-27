package com.aura.data.model

import com.squareup.moshi.JsonClass

/**
 * Data class representing the credentials used for user login.
 *
 * @property id The identifier (e.g., username or email) of the user.
 * @property password The password associated with the user's account.
 */
@JsonClass(generateAdapter = true)
data class LoginCredentials(
    val id: String,
    val password: String
)