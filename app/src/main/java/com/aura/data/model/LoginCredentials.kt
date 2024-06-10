package com.aura.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginCredentials(
    val id: String,
    val password: String
)
