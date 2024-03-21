package com.example.transactionapp.ui.viewmodel.location

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LocationViewModel: ViewModel() {
    private val _location = MutableLiveData<String>()

    val location: MutableLiveData<String>
        get() = _location

    fun setLocation(location: String){
        _location.postValue(location)
    }

}