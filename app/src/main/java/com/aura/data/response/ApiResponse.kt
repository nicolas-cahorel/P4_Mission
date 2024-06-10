package com.aura.data.response

import com.aura.data.model.LoginResultModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Data class representing the API response for a login request.
 * @property isLoginSuccessful Indicates whether the login was successful.
 */
@JsonClass(generateAdapter = true)
data class ApiResponse(
    @Json(name = "granted") val isLoginSuccessful: Boolean
) {
    /**
     * Converts this ApiResponse object to a UserLoginResult object.
     * @return UserLoginResult object.
     */
    fun toDomainModel(): LoginResultModel {
        return LoginResultModel(isLoginSuccessful)
    }
}
