package com.aura.data.apiResponse

import com.aura.data.model.TransferResultModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Data class representing the API response for a login request.
 * @property apiResponseBody Indicates whether the login was successful.
 * @property apiResponseStatusCode The HTTP status code of the response.
 */
@JsonClass(generateAdapter = true)
data class TransferApiResponse(
    @Json(name = "result") val apiResponseBody: Boolean,
) {
    /**
     * Converts this TransferApiResponse object to a TransferResultModel object.
     * @param apiResponseStatusCode The HTTP status code of the response.
     * @return TransferResultModel object.
     */
    fun toDomainModel(apiResponseStatusCode: Int): TransferResultModel {
        return TransferResultModel(apiResponseBody, apiResponseStatusCode)
    }
}
