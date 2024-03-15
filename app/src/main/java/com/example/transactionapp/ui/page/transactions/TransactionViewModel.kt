package com.example.transactionapp.ui.page.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TransactionViewModel : ViewModel() {

    // Balance amount
    private val _balance = MutableLiveData<Int>()
    var balance: LiveData<Int>
        get() = _balance
        set(value) {_balance}

    // Cashflow amount
    private val _cashflow = MutableLiveData<Int>()
    var cashflow: LiveData<Int>
        get() = _cashflow
        set(value) {_cashflow}

    // Growth amount
    private val _growth = MutableLiveData<Float>()
    var growth: LiveData<Float>
        get() = _growth
        set(value) {_growth}
}