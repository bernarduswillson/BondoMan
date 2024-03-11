package com.example.transactionapp.domain.api.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Item(
    @Json(name = "name")
    val name: String,
    @Json(name = "price")
    val price: Double,
    @Json(name = "qty")
    val qty: Int
)