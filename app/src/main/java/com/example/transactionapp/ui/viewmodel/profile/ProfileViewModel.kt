package com.example.transactionapp.ui.viewmodel.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {
    private val _name = MutableLiveData<String>()
    val name : LiveData<String>
        get() = _name

    init {
        _name.value = "John Pukul"
    }
}