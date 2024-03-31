package com.example.transactionapp.domain.api

import com.example.transactionapp.domain.api.model.BillResponse
import com.example.transactionapp.domain.api.model.LoginInput
import com.example.transactionapp.domain.api.model.LoginResponse
import com.example.transactionapp.domain.api.model.TokenResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Part

interface TransactionAPI {

    @POST("/api/auth/login")
    suspend fun login(@Body input : LoginInput): LoginResponse

    @POST("/api/auth/token")
    suspend fun validateToken(@Header("Authorization") token: String): TokenResponse

    @POST("/api/bill/upload")
    suspend fun postBill(
        @Header("Authorization") token: String,
        @Part bill: MultipartBody.Part
    ): BillResponse
}