package com.aura.data.model

/**
 * Domain model representing the result of a user login.
 *
 * @property isLoginSuccessful Indicates whether the login was successful.
 * @property loginStatusCode The HTTP status code of the login request.
 */
data class LoginResultModel(
    val isLoginSuccessful: Boolean,
    val loginStatusCode: Int
)