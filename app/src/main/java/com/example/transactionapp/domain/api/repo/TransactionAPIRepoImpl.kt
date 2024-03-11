package com.example.transactionapp.domain.api.repo

import com.example.transactionapp.domain.api.TransactionAPI
import com.example.transactionapp.domain.api.model.BillResponse
import com.example.transactionapp.domain.api.model.LoginInput
import com.example.transactionapp.domain.api.model.LoginResponse
import com.example.transactionapp.domain.api.model.TokenResponse
import okhttp3.MultipartBody
import javax.inject.Inject

class TransactionAPIRepoImpl @Inject constructor(
    private val transactionAPI: TransactionAPI
) : TransactionAPIRepo {
    override suspend fun login(input: LoginInput): LoginResponse = transactionAPI.login(input)

    override suspend fun validateToken(token: String): TokenResponse = transactionAPI.validateToken(token)

    override suspend fun postBill(token: String, bill: MultipartBody.Part): BillResponse = transactionAPI.postBill(token, bill)
}