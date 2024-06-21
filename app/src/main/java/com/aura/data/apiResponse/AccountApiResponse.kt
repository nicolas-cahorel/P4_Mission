package com.aura.data.apiResponse

import com.aura.data.model.AccountResultModel
import com.aura.data.model.AccountsResultModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Data class representing the API response for a user account request.
 * @property apiResponseBody The list of user account API responses.
 */
@JsonClass(generateAdapter = true)
data class AccountsApiResponse(
    @Json(name = "accounts") val apiResponseBody: List<AccountApiResponse>,
) {
    /**
     * Converts the API response to a domain model.
     * @param apiResponseStatusCode The HTTP status code of the response.
     * @return The domain model representation of the user accounts.
     */
    fun toDomainModel(apiResponseStatusCode: Int): AccountsResultModel {
        return AccountsResultModel(
            accountStatusCode = apiResponseStatusCode,
            accounts = apiResponseBody.map {
                AccountResultModel(
                    it.apiResponseId,
                    it.apiResponseMain,
                    it.apiResponseBalance
                )
            }
        )
    }
}

/**
 * Data class representing an individual user account API response.
 * @property apiResponseId The identifier of the user's account.
 * @property apiResponseMain Indicates if it is the user's main account.
 * @property apiResponseBalance The balance of the user's account.
 */
@JsonClass(generateAdapter = true)
data class AccountApiResponse(
    @Json(name = "id") val apiResponseId: String,
    @Json(name = "main") val apiResponseMain: Boolean,
    @Json(name = "balance") val apiResponseBalance: Double
)