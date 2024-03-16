package com.example.transactionapp.ui.viewmodel.transaction

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transactionapp.domain.db.model.Transaction
import com.example.transactionapp.domain.db.repo.TransactionDatabaseRepoImpl
import com.example.transactionapp.ui.viewmodel.model.TransactionDate
import com.example.transactionapp.ui.viewmodel.model.TransactionDateList
import com.example.transactionapp.utils.changeDateTypeToStandardDateLocal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import java.util.TreeMap
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionDatabaseRepoImpl: TransactionDatabaseRepoImpl
): ViewModel() {
    private val _dateAll: MutableLiveData<List<TransactionDateList>> = MutableLiveData()
    private val _transaction: MutableLiveData<List<Transaction>> = MutableLiveData()

    val transaction: LiveData<List<Transaction>>
        get() = _transaction

    val dateAll: LiveData<List<TransactionDateList>>
        get() = _dateAll

    fun insertTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionDatabaseRepoImpl.insertTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionDatabaseRepoImpl.deleteTransaction(transaction)
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionDatabaseRepoImpl.updateTransaction(transaction)
        }
    }

    fun getTransactions(type: String) {
        viewModelScope.launch {
            _transaction.postValue(
                when (type) {
                    "desc" -> transactionDatabaseRepoImpl.getAllTransactionsDesc()
                    "asc" -> transactionDatabaseRepoImpl.getAllTransactionsAsc()
                    else -> transactionDatabaseRepoImpl.getAllTransactions()
                }
            )
        }
    }

    fun getAllDate(){
        viewModelScope.launch {

            val data = TreeMap<String, MutableList<TransactionDate>>()
            val listData: MutableList<TransactionDateList> = mutableListOf()
            val response = transactionDatabaseRepoImpl.getAllTransactionsAsc()
            response.forEach {
                if (!data.containsKey(changeDateTypeToStandardDateLocal(it.createdAt))){
                    data[changeDateTypeToStandardDateLocal(it.createdAt)] = mutableListOf()
                }
                data[changeDateTypeToStandardDateLocal(it.createdAt)]?.add(
                    TransactionDate(
                        id = it.id,
                        category = it.category,
                        nominal = it.nominal,
                        title = it.title,
                        location = it.location,
                        createdAt = changeDateTypeToStandardDateLocal(it.createdAt)
                    )
                )

            }

            for((key, value) in data){
                listData.add(
                    TransactionDateList(
                        date = key,
                        listTransaction = value
                    )
                )
            }


            _dateAll.postValue(listData)

        }
    }
}