package com.example.transactionapp.ui.viewmodel.navigation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NavigationViewModel : ViewModel() {

    // fragmentName
    private val _fragmentName = MutableLiveData<String>("transaction")
    val fragmentName : LiveData<String>
        get() = _fragmentName

    // Navigate
    fun navigate(value: String) {
        _fragmentName.value = value
    }
}