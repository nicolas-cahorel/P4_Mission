package com.aura.data.model

/**
 * Domain model representing the result of a user's account information.
 * @property accountStatusCode The HTTP status code of the user account request.
 * @property accounts A list of user account results.
 */
data class AccountsResultModel(
    val accountStatusCode: Int,
    val accounts: List<AccountResultModel>
)

/**
 * Domain model representing an individual user's account information.
 * @property accountId The unique identifier of the user's account.
 * @property isAccountMain Indicates whether the user's account is the main account.
 * @property accountBalance The balance amount in the user's account.
 */
data class AccountResultModel(
    val accountId: String,
    val isAccountMain: Boolean,
    val accountBalance: Double
)