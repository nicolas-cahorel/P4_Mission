package com.aura.data.apiResponse

import com.aura.data.model.LoginResultModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Data class representing the API response for a login request.
 * @property apiResponseBody Indicates whether the login was successful.
 * @property apiResponseStatusCode The HTTP status code of the response.
 */
@JsonClass(generateAdapter = true)
data class LoginApiResponse(
    @Json(name = "granted") val apiResponseBody: Boolean,
) {
    /**
     * Converts this LoginApiResponse object to a LoginResultModel object.
     * @param apiResponseStatusCode The HTTP status code of the response.
     * @return LoginResultModel object.
     */
    fun toDomainModel(apiResponseStatusCode: Int): LoginResultModel {
        return LoginResultModel(apiResponseBody, apiResponseStatusCode)
    }
}
