package com.example.transactionapp.ui.viewmodel.transaction

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transactionapp.R
import com.example.transactionapp.domain.api.model.Items
import com.example.transactionapp.domain.db.model.Transaction
import com.example.transactionapp.domain.db.repo.TransactionDatabaseRepoImpl
import com.example.transactionapp.ui.viewmodel.model.TransactionDate
import com.example.transactionapp.ui.viewmodel.model.TransactionDateList
import com.example.transactionapp.utils.changeDateTypeToStandardDateLocal
import com.example.transactionapp.utils.changeNominalToIDN
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
    private val _balance: MutableLiveData<Long> = MutableLiveData()
    private val _cashFlow: MutableLiveData<Long> = MutableLiveData()
    private val _growth: MutableLiveData<Long> = MutableLiveData()

    // STATUS
    private val _addTransactionStatus: MutableLiveData<Boolean> = MutableLiveData()
    private val _deleteTransactionStatus: MutableLiveData<Boolean> = MutableLiveData()
    private val _updateTransactionStatus: MutableLiveData<Boolean> = MutableLiveData()

    val transaction: LiveData<List<Transaction>>
        get() = _transaction

    val dateAll: LiveData<List<TransactionDateList>>
        get() = _dateAll

    val balance: LiveData<Long>
        get() = _balance

    val cashFlow: LiveData<Long>
        get() = _cashFlow

    val growth: LiveData<Long>
        get() = _growth

    val addTransactionStatus: LiveData<Boolean>
        get() = _addTransactionStatus

    val deleteTransactionStatus: LiveData<Boolean>
        get() = _deleteTransactionStatus

    val updateTransactionStatus: LiveData<Boolean>
        get() = _updateTransactionStatus

    fun insertTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionDatabaseRepoImpl.insertTransaction(transaction)
        }
    }

    fun insertBillTransaction(items: List<Transaction>){
        viewModelScope.launch {
            items.forEach {
                val transaction = Transaction(
                    title = it.title,
                    category = "Expense",
                    nominal = it.nominal,
                    location = it.location,
                    createdAt = Date()
                )
                transactionDatabaseRepoImpl.insertTransaction(transaction)
            }
            changeAddStatus(true)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionDatabaseRepoImpl.deleteTransaction(transaction)
            _deleteTransactionStatus.postValue(true)
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionDatabaseRepoImpl.updateTransaction(transaction)
            _updateTransactionStatus.postValue(true)
        }
    }

    fun getTransactions(type: String) {
        var sum = 0L
        viewModelScope.launch {
            val response =
                when (type) {
                    "desc" -> transactionDatabaseRepoImpl.getAllTransactionsDesc()
                    "asc" -> transactionDatabaseRepoImpl.getAllTransactionsAsc()
                    else -> transactionDatabaseRepoImpl.getAllTransactions()
                }

            _transaction.postValue(response)
            response.forEach {
                if(it.category == "Expense"){
                    sum -= it.nominal
                }
                if(it.category == "Income"){
                    sum += it.nominal
                }
            }
            Log.d("TransactionViewModel", "getTransactions: $sum")
            _balance.postValue(sum)
        }
    }

    fun getAllDate(){
        viewModelScope.launch {

            val data = TreeMap<String, MutableList<TransactionDate>>()
            val listData: MutableList<TransactionDateList> = mutableListOf()
            val response = transactionDatabaseRepoImpl.getAllTransactionsDesc()
            val keySort = mutableListOf<String>()
            response.forEach {
                if(!keySort.contains(changeDateTypeToStandardDateLocal(it.createdAt))){
                    keySort.add(changeDateTypeToStandardDateLocal(it.createdAt))
                }
                if (!data.containsKey(changeDateTypeToStandardDateLocal(it.createdAt))){
                    data[changeDateTypeToStandardDateLocal(it.createdAt)] = mutableListOf()
                }
                data[changeDateTypeToStandardDateLocal(it.createdAt)]?.add(
                    TransactionDate(
                        id = it.id,
                        category = it.category,
                        nominal = changeNominalToIDN(it.nominal),
                        title = it.title,
                        location = it.location,
                        createdAt = changeDateTypeToStandardDateLocal(it.createdAt),
                        icon = when(it.category){
                            "Expense" -> R.drawable.expense_ic
                            "Income" -> R.drawable.income_ic
                            "Saving" -> R.drawable.saving_ic
                            else -> R.drawable.saving_ic
                        },
                        colorText = when(it.category){
                            "Expense" -> R.color.R3
                            "Income" -> R.color.G3
                            "Saving" -> R.color.B4
                            else -> R.color.B4
                        },
                    )
                )

            }

            for(value in keySort){
                listData.add(
                    TransactionDateList(
                        date = value,
                        listTransaction = data[value] ?: mutableListOf()
                    )
                )
            }

            _dateAll.postValue(listData)
        }
    }

    fun getCashFlowAndGrowthByMonth (date: Date) {
        viewModelScope.launch {
            val thisMonth = if (date.month + 1 < 10) "0${date.month + 1}" else (date.month + 1).toString()
            val lastMonth = if (date.month < 10) "0${date.month}" else date.month.toString()
            val transactionThisMonth = transactionDatabaseRepoImpl.getTransactionsByMonth(thisMonth)
            val transactionLastMonth = transactionDatabaseRepoImpl.getTransactionsByMonth(lastMonth)

            var incomeThisMonth = 0L
            var expenseThisMonth = 0L
            var investmentThisMonth = 0L

            var incomeLastMonth = 0L
            var expenseLastMonth = 0L
            var investmentLastMonth = 0L

            var cashFlow = 0L
            var growth = 0L

            Log.d("TransactionViewModel", "getCashFlowAndGrowthByMonth: $transactionThisMonth")
            Log.d("TransactionViewModel", "getCashFlowAndGrowthByMonth: $transactionLastMonth")

            transactionThisMonth.forEach {
                when(it.category){
                    "Expense" -> expenseThisMonth += it.nominal
                    "Income" -> incomeThisMonth += it.nominal
                    "Savings" -> investmentThisMonth += it.nominal
                }
            }

            transactionLastMonth.forEach {
                when(it.category){
                    "Expense" -> expenseLastMonth += it.nominal
                    "Income" -> incomeLastMonth += it.nominal
                    "Savings" -> investmentLastMonth += it.nominal
                }
            }

            val balanceLastMonth = incomeLastMonth + investmentLastMonth - expenseLastMonth

            cashFlow = incomeThisMonth - expenseThisMonth - investmentThisMonth
            if (balanceLastMonth != 0L) {
                growth = ((incomeThisMonth - expenseThisMonth - investmentThisMonth) / balanceLastMonth) * 100
            }

            _cashFlow.postValue(cashFlow)
            _growth.postValue(growth)
        }
    }


    fun resetAddTransactionStatus(){
        _addTransactionStatus.postValue(false)
    }

    fun resetDeleteTransactionStatus(){
        _deleteTransactionStatus.postValue(false)
    }

    fun resetUpdateTransactionStatus(){
        _updateTransactionStatus.postValue(false)
    }

    fun changeAddStatus(status: Boolean){
        _addTransactionStatus.postValue(status)
    }

    fun removeObserveAllData(lifecycleOwner: LifecycleOwner){
        dateAll.removeObservers(lifecycleOwner)
        transaction.removeObservers(lifecycleOwner)
        balance.removeObservers(lifecycleOwner)
        cashFlow.removeObservers(lifecycleOwner)
        growth.removeObservers(lifecycleOwner)
    }

}