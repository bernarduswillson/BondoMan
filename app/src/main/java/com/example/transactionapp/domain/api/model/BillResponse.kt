package com.example.transactionapp.domain.api.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BillResponse(
    @Json(name = "items")
    val items: Items
)