package com.aura.data.response

import com.aura.data.model.LoginResultModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Data class representing the API response for a login request.
 * @property apiResponseBody Indicates whether the login was successful.
 * @property apiResponseStatusCode The HTTP status code of the response.
 */
@JsonClass(generateAdapter = true)
data class ApiLoginResponse(
    @Json(name = "granted") val apiResponseBody: Boolean,
    @Json (name = "statusCode") val apiResponseStatusCode: Int
) {
    /**
     * Converts this ApiResponse object to a UserLoginResult object.
     * @return UserLoginResult object.
     */
    fun toDomainModel(apiResponseStatusCode: Int): LoginResultModel {
        return LoginResultModel(apiResponseBody, apiResponseStatusCode)
    }
}
