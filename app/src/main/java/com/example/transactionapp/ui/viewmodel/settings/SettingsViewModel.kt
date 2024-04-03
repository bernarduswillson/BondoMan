package com.example.transactionapp.ui.viewmodel.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel: ViewModel() {

    // Name
    private val _name = MutableLiveData<String>()
    val name : LiveData<String>
        get() = _name

    // Initialize variables
    init {
        _name.value = "John Pukul"
    }
}