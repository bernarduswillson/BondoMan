package com.example.transactionapp.ui.screen.mainmenu.transaction

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transactionapp.R
import com.example.transactionapp.domain.db.model.Transaction
import com.example.transactionapp.domain.db.repo.TransactionDatabaseRepoImpl
import com.example.transactionapp.ui.viewmodel.model.TransactionDate
import com.example.transactionapp.ui.viewmodel.model.TransactionDateList
import com.example.transactionapp.utils.changeDateTypeToStandardDateLocal
import com.example.transactionapp.utils.changeNominalToIDN
import dagger.hilt.android.lifecycle.HiltViewModel
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

    // All dates in transaction list
    private val _dateAll: MutableLiveData<List<TransactionDateList>> = MutableLiveData()
    val dateAll: LiveData<List<TransactionDateList>>
        get() = _dateAll

    // List of transactions
    private val _transaction: MutableLiveData<List<Transaction>> = MutableLiveData()
    val transaction: LiveData<List<Transaction>>
        get() = _transaction

    // List of income (StatisticViewModel)
    private val _listOfIncome: MutableLiveData<List<Long>> = MutableLiveData()
    val listOfIncome: LiveData<List<Long>>
        get() = _listOfIncome

    // List of expense (StatisticViewModel)
    private val _listOfExpense: MutableLiveData<List<Long>> = MutableLiveData()
    val listOfExpense: LiveData<List<Long>>
        get() = _listOfExpense

    // List of saving
    private val _listOfSaving: MutableLiveData<List<Long>> = MutableLiveData()
    val listOfSaving: LiveData<List<Long>>
        get() = _listOfSaving

    // Total income (StatisticViewModel)
    private val _sumOfIncome: MutableLiveData<Long> = MutableLiveData()
    val sumOfIncome: LiveData<Long>
        get() = _sumOfIncome

    // Total expense (StatisticViewModel)
    private val _sumOfExpense: MutableLiveData<Long> = MutableLiveData()
    val sumOfExpense: LiveData<Long>
        get() = _sumOfExpense

    // Total saving (StatisticViewModel)
    private val _sumOfSaving: MutableLiveData<Long> = MutableLiveData()
    val sumOfSaving: LiveData<Long>
        get() = _sumOfSaving

    // Balance
    private val _balance: MutableLiveData<Long> = MutableLiveData()
    val balance: LiveData<Long>
        get() = _balance

    // Cashflow
    private val _cashFlow: MutableLiveData<Long> = MutableLiveData()
    val cashFlow: LiveData<Long>
        get() = _cashFlow

    // Growth
    private val _growth: MutableLiveData<Long> = MutableLiveData()
    val growth: LiveData<Long>
        get() = _growth

    // Scan data (ScanViewModel)
    private val _atomicTransaction: MutableLiveData<ScanResult> = MutableLiveData()
    val atomicTransaction: LiveData<ScanResult>
        get() = _atomicTransaction

    // Randomize transaction data (SettingViewModel)
    private val _isRandom: MutableLiveData<Boolean> = MutableLiveData()
    val isRandom: LiveData<Boolean>
        get() = _isRandom

    // Add transaction state (NewTransactionVideModel)
    private val _addTransactionStatus: MutableLiveData<Boolean> = MutableLiveData()
    val addTransactionStatus: LiveData<Boolean>
        get() = _addTransactionStatus

    // Camera state ?? (ScanTransactionViewModel)
    private val _cameraStatus: MutableLiveData<Boolean> = MutableLiveData()
    val cameraStatus: LiveData<Boolean>
        get() = _cameraStatus

    // Navigate to detail state
    private val _navigateToTransactionDetail: MutableLiveData<Int?> = MutableLiveData()
    val navigateToTransactionDetail: LiveData<Int?>
        get() = _navigateToTransactionDetail

    // Set random state (SettingViewModel)
    fun changeIsRandom(status: Boolean){
        _isRandom.postValue(status)
    }

    // Insert new transaction (NewTransactionViewModel)
    fun insertTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionDatabaseRepoImpl.insertTransaction(transaction)
        }
    }

    // Insert scanned transaction (ScanViewModel)
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

    // Get transaction
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

    // Get all date
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

    // Get statistic (StatisticViewModel)
    fun getStatisticByMonth(date: Date){
        viewModelScope.launch {
            val thisMonth = if (date.month + 1 < 10) "0${date.month + 1}" else (date.month + 1).toString()
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

    // Get cashflow and growth by month
    fun getCashFlowAndGrowthByMonth (date: Date) {
        viewModelScope.launch {
            val thisMonth = if (date.month + 1 < 10) "0${date.month + 1}" else (date.month + 1).toString()
            val lastMonth = if (date.month < 10) "0${date.month}" else (date.month).toString()
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

            Log.d("TransactionViewModel", "getCashFlowAndGrowthThisMonth: $transactionThisMonth")
            Log.d("TransactionViewModel", "getCashFlowAndGrowthLastMonth: $transactionLastMonth")

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

    // Reset add transaction state (NewTransactionViewModel)
    fun resetAddTransactionStatus(){
        _addTransactionStatus.postValue(false)
    }

    // Change new state (NewTransactionViewModel)
    fun changeAddStatus(status: Boolean){
        _addTransactionStatus.postValue(status)
    }

    // Change camera state (ScanTransactionViewModel)
    fun changeCameraStatus(status: Boolean){
        _cameraStatus.postValue(status)
    }

    // Set scan result (ScanTransactionViewModel)
    fun setAtomicTransaction(transaction: ScanResult){
        _atomicTransaction.postValue(transaction)
    }

    // Remove all observer data
    fun removeObserveAllData(lifecycleOwner: LifecycleOwner){
        dateAll.removeObservers(lifecycleOwner)
        transaction.removeObservers(lifecycleOwner)
        balance.removeObservers(lifecycleOwner)
        cashFlow.removeObservers(lifecycleOwner)
        growth.removeObservers(lifecycleOwner)
    }

    fun onTransactionClicked(transactionId: Int) {
        _navigateToTransactionDetail.value = transactionId
    }

    fun onTransactionDetailNavigated() {
        _navigateToTransactionDetail.value = null
    }

}