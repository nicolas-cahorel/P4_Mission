package com.aura.data.model

/**
 * Represents a user.
 * @property id The user's ID.
 * @property firstname The user's first name.
 * @property lastname The user's last name.
 * @property password The user's password.
 * @property accounts A list of the user's accounts.
 */
data class User(
    val id: String,
    val firstname: String,
    val lastname: String,
    val password: String,
    val accounts: List<Account>,
)
