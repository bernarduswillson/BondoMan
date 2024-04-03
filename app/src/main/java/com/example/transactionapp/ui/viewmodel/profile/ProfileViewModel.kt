package com.example.transactionapp.ui.viewmodel.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {

    // Toggle button state
    private val _isTwibbonActive = MutableLiveData<Boolean>()
    val isTwibbonActive : LiveData<Boolean>
        get() = _isTwibbonActive

    // TODO: Create a Live Data for the picture src (Aing g ngerti camera bew, klo perlu buat variable, buat aja. Tapi buatnya klo bisa di sini)

    // Initialize data value
    init {
        _isTwibbonActive.value = true
    }

    // Toggle twibbon handler
    fun toggleTwibbon() {
        _isTwibbonActive.value = !_isTwibbonActive.value!!
    }

    // Take picture handler
    fun onTakePicture() {
        // TODO: Implement take picture
    }
}