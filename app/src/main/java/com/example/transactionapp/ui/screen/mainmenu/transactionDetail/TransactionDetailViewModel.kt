package com.example.transactionapp.ui.screen.mainmenu.transactionDetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transactionapp.domain.db.model.Transaction
import com.example.transactionapp.domain.db.repo.TransactionDatabaseRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    private val transactionDatabaseRepoImpl: TransactionDatabaseRepoImpl
): ViewModel() {
    // Transaction detail (TransactionDetailViewModel)
    private val _transactionById: MutableLiveData<Transaction> = MutableLiveData()
    val transactionById: LiveData<Transaction>
        get() = _transactionById

    // Get transaction by id (TransactionDetailViewModel)
    fun getTransactionById(id: Int){
        viewModelScope.launch {
            val response = transactionDatabaseRepoImpl.getTransactionById(id)
            _transactionById.postValue(response)
        }
    }

    // Add transaction state (NewTransactionVideModel)
    private val _addTransactionStatus: MutableLiveData<Boolean> = MutableLiveData()
    val addTransactionStatus: LiveData<Boolean>
        get() = _addTransactionStatus

    // Delete transaction state (TransactionDetailViewModel)
    private val _deleteTransactionStatus: MutableLiveData<Boolean> = MutableLiveData()
    val deleteTransactionStatus: LiveData<Boolean>
        get() = _deleteTransactionStatus

    // Update transaction state (TransactionDetailViewModel)
    private val _updateTransactionStatus: MutableLiveData<Boolean> = MutableLiveData()
    val updateTransactionStatus: LiveData<Boolean>
        get() = _updateTransactionStatus

    // Change new state (NewTransactionViewModel)
    fun changeAddStatus(status: Boolean){
        _addTransactionStatus.postValue(status)
    }

    // Delete transaction (TransactionDetailViewModel)
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionDatabaseRepoImpl.deleteTransaction(transaction)
            _deleteTransactionStatus.postValue(true)
        }
    }

    // Update transaction (TransactionDetailViewModel)
    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionDatabaseRepoImpl.updateTransaction(transaction)
            _updateTransactionStatus.postValue(true)
        }
    }

    // Reset delete transaction state (TransactionDetailViewModel)
    fun resetDeleteTransactionStatus(){
        _deleteTransactionStatus.postValue(false)
    }

    // Reset update transaction state (TransactionDetailViewModel)
    fun resetUpdateTransactionStatus(){
        _updateTransactionStatus.postValue(false)
    }
}