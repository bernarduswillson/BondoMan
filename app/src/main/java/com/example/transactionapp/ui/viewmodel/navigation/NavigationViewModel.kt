package com.example.transactionapp.ui.viewmodel.navigation

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.transactionapp.R

class NavigationViewModel : ViewModel() {

    // Fragment name
    private val _fragmentName = MutableLiveData<String>("transaction")
    val fragmentName : LiveData<String>
        get() = _fragmentName

    // Navigate
    fun navigate(value: String) {
        _fragmentName.value = value
        Log.d("NavViewModel", _fragmentName.value!!)
    }
}