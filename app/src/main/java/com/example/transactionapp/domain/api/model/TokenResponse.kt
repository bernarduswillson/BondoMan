package com.example.transactionapp.domain.api.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TokenResponse(
    @Json(name = "exp")
    val exp: Int,
    @Json(name = "iat")
    val iat: Int,
    @Json(name = "nim")
    val nim: String
)