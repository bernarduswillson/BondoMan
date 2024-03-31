package com.example.transactionapp.ui.viewmodel.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transactionapp.domain.api.model.LoginInput
import com.example.transactionapp.domain.api.repo.TransactionAPIRepoImpl
import com.example.transactionapp.ui.viewmodel.model.BillResponseSealed
import com.example.transactionapp.ui.viewmodel.model.LoginResponseSealed
import com.example.transactionapp.ui.viewmodel.model.TokenResponseSealed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.Part
import javax.inject.Inject

@HiltViewModel
class Auth @Inject constructor(
    private val transactionAPIRepoImpl: TransactionAPIRepoImpl
): ViewModel(){

    private val _loginResponse = MutableLiveData<LoginResponseSealed>()
    private val _billResponse = MutableLiveData<BillResponseSealed?>()
    private val _tokenResponse = MutableLiveData<TokenResponseSealed>()

    val loginResponse: LiveData<LoginResponseSealed>
        get() = _loginResponse

    val billResponse: LiveData<BillResponseSealed?>
        get() = _billResponse

    val tokenResponse: LiveData<TokenResponseSealed>
        get() = _tokenResponse

    fun login(input: LoginInput){
        viewModelScope.launch(Dispatchers.IO) {
            _loginResponse.postValue(LoginResponseSealed.Loading(true))
            try {
                val response = transactionAPIRepoImpl.login(input)
                _loginResponse.postValue(LoginResponseSealed.Success(response))
            } catch (e: Exception) {
                _loginResponse.postValue(LoginResponseSealed.Error(e.message.toString()))
            }
        }
    }

    fun postBill(token: String, bill: MultipartBody.Part){
        viewModelScope.launch(Dispatchers.IO) {
            _billResponse.postValue(BillResponseSealed.Loading(true))
            try {
                val response = transactionAPIRepoImpl.postBill(token, bill)
                _billResponse.postValue(BillResponseSealed.Success(response))
            } catch (e: Exception) {
                _billResponse.postValue(BillResponseSealed.Error(e.message.toString()))
            }
        }
    }

    fun validateToken(token: String){
        viewModelScope.launch(Dispatchers.IO) {
            _tokenResponse.postValue(TokenResponseSealed.Loading(true))
            try {
                val response = transactionAPIRepoImpl.validateToken(token)
                _tokenResponse.postValue(TokenResponseSealed.Success(response))
            } catch (e: Exception) {
                _tokenResponse.postValue(TokenResponseSealed.Error(e.message.toString()))
            }
        }
    }

    fun resetBillResponse(){
        _billResponse.postValue(null)
    }
}