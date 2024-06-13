package com.aura.data.model

/**
 * Domain model representing the result of a user login.
 * @property isLoginSuccessful Indicates whether the login was successful.
 */
data class LoginResultModel(
    val isLoginSuccessful: Boolean,
    val loginStatusCode: Int
)