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


data class ScanResult(
    val title: String = "",
    val nominal: Long = 0,
)
@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionDatabaseRepoImpl: TransactionDatabaseRepoImpl
): ViewModel() {
    private val _dateAll: MutableLiveData<List<TransactionDateList>> = MutableLiveData()
    private val _transaction: MutableLiveData<List<Transaction>> = MutableLiveData()
    private val _transactionById: MutableLiveData<Transaction> = MutableLiveData()
    private val _balance: MutableLiveData<Long> = MutableLiveData()

    private val _listOfIncome: MutableLiveData<List<Long>> = MutableLiveData()
    private val _sumOfIncome: MutableLiveData<Long> = MutableLiveData()
    private val _listOfExpense: MutableLiveData<List<Long>> = MutableLiveData()
    private val _sumOfExpense: MutableLiveData<Long> = MutableLiveData()
    private val _sumOfSaving: MutableLiveData<Long> = MutableLiveData()
    private val _listOfSaving: MutableLiveData<List<Long>> = MutableLiveData()

    private val _cashFlow: MutableLiveData<Long> = MutableLiveData()
    private val _growth: MutableLiveData<Long> = MutableLiveData()
    private val _isRandom: MutableLiveData<Boolean> = MutableLiveData()

    private val _atomicTransaction: MutableLiveData<ScanResult> = MutableLiveData()

    // STATUS
    private val _addTransactionStatus: MutableLiveData<Boolean> = MutableLiveData()
    private val _deleteTransactionStatus: MutableLiveData<Boolean> = MutableLiveData()
    private val _updateTransactionStatus: MutableLiveData<Boolean> = MutableLiveData()
    private val _cameraStatus: MutableLiveData<Boolean> = MutableLiveData()

    val transaction: LiveData<List<Transaction>>
        get() = _transaction

    val dateAll: LiveData<List<TransactionDateList>>
        get() = _dateAll

    val transactionById: LiveData<Transaction>
        get() = _transactionById

    val balance: LiveData<Long>
        get() = _balance

    val listOfIncome: LiveData<List<Long>>
        get() = _listOfIncome

    val sumOfIncome: LiveData<Long>
        get() = _sumOfIncome

    val listOfExpense: LiveData<List<Long>>
        get() = _listOfExpense

    val sumOfExpense: LiveData<Long>
        get() = _sumOfExpense

    val listOfSaving: LiveData<List<Long>>
        get() = _listOfSaving
    val sumOfSaving: LiveData<Long>
        get() = _sumOfSaving

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

    val isRandom: LiveData<Boolean>
        get() = _isRandom

    val cameraStatus: LiveData<Boolean>
        get() = _cameraStatus

    val atomicTransaction: LiveData<ScanResult>
        get() = _atomicTransaction

    fun changeIsRandom(status: Boolean){
        _isRandom.postValue(status)
    }

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
                    createdAt = Date(),
                    lat = it.lat,
                    long = it.long
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
                Log.d("TransactionViewModel", "getTransactions: ${it}")
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

    fun getTransactionById(id: Int){
        viewModelScope.launch {
            val response = transactionDatabaseRepoImpl.getTransactionById(id)
            _transactionById.postValue(response)
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

    fun getStatisticByMonth(date: Date){
        viewModelScope.launch {
            val thisMonth = if (date.month < 10) "0${date.month}" else (date.month).toString()
            Log.d("TransactionViewModel", "getStatisticMonthhhh: $thisMonth")
            val response = transactionDatabaseRepoImpl.getTransactionsByMonth(thisMonth)
            val income = mutableListOf<Long>()
            val expense = mutableListOf<Long>()
            val saving = mutableListOf<Long>()
            var sumIncome = 0L
            var sumExpense = 0L
            var sumSaving = 0L

            response.forEach {
                when(it.category){
                    "Income" -> {
                        income.add(it.nominal)
                        sumIncome += it.nominal
                    }
                    "Expense" -> {
                        expense.add(it.nominal)
                        sumExpense += it.nominal
                    }
                    "Savings" -> {
                        saving.add(it.nominal)
                        sumSaving += it.nominal
                    }
                }
            }

            Log.d("TransactionViewModel", "getStatisticByMonth: $income")
            Log.d("TransactionViewModel", "getStatisticByMonth: $expense")
            Log.d("TransactionViewModel", "getStatisticByMonth: $saving")

            _listOfIncome.postValue(income)
            _sumOfIncome.postValue(sumIncome)
            _listOfExpense.postValue(expense)
            _sumOfExpense.postValue(sumExpense)
            _listOfSaving.postValue(saving)
            _sumOfSaving.postValue(sumSaving)
        }
    }

    fun getCashFlowAndGrowthByMonth (date: Date) {
        viewModelScope.launch {
            val thisMonth = if (date.month < 10) "0${date.month}" else (date.month).toString()
            val lastMonth = if (date.month - 1 < 10) "0${date.month - 1}" else (date.month - 1).toString()
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

    fun changeCameraStatus(status: Boolean){
        _cameraStatus.postValue(status)
    }

    fun setAtomicTransaction(transaction: ScanResult){
        _atomicTransaction.postValue(transaction)
    }

    fun removeObserveAllData(lifecycleOwner: LifecycleOwner){
        dateAll.removeObservers(lifecycleOwner)
        transaction.removeObservers(lifecycleOwner)
        balance.removeObservers(lifecycleOwner)
        cashFlow.removeObservers(lifecycleOwner)
        growth.removeObservers(lifecycleOwner)
    }

}