package com.example.transactionapp.ui.viewmodel.model

import com.example.transactionapp.domain.api.model.BillResponse
import com.example.transactionapp.domain.api.model.LoginResponse
import com.example.transactionapp.domain.api.model.TokenResponse

sealed class LoginResponseSealed {
    data class Success(val data: LoginResponse): LoginResponseSealed()
    data class Loading(val boolean: Boolean): LoginResponseSealed()
    data class Error(val message: String): LoginResponseSealed()
}

sealed class BillResponseSealed {
    data class Success(val data: BillResponse) : BillResponseSealed()
    data class Loading(val boolean: Boolean) : BillResponseSealed()
    data class Error(val message: String) : BillResponseSealed()
}

sealed class TokenResponseSealed {
    data class Success(val data: TokenResponse) : TokenResponseSealed()
    data class Loading(val boolean: Boolean) : TokenResponseSealed()
    data class Error(val message: String) : TokenResponseSealed()
}


enum class Status {
    Available, Unavailable, Losing, Lost
}