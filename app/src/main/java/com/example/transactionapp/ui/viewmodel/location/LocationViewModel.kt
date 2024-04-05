package com.example.transactionapp.ui.viewmodel.location

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class LocationModel(
    val locationName: String,
    var latitude: Double,
    val longitude: Double
)

class LocationViewModel: ViewModel() {
    private val _location = MutableLiveData<LocationModel>()

    val location: MutableLiveData<LocationModel>
        get() = _location

    fun setLocation(location: LocationModel){
        _location.postValue(location)
    }

}