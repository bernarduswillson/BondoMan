package com.example.transactionapp.domain.api.repo

import com.example.transactionapp.domain.api.model.BillResponse
import com.example.transactionapp.domain.api.model.LoginInput
import com.example.transactionapp.domain.api.model.LoginResponse
import com.example.transactionapp.domain.api.model.TokenResponse
import okhttp3.MultipartBody

interface TransactionAPIRepo {
    suspend fun login(input: LoginInput): LoginResponse
    suspend fun validateToken(token: String): TokenResponse
    suspend fun postBill(token: String, bill: MultipartBody.Part): BillResponse
}