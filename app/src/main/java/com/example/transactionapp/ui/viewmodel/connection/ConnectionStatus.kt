package com.example.transactionapp.ui.viewmodel.connection

import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.MutableLiveData
import com.example.transactionapp.ui.viewmodel.model.Status

class ConnectionStatus(context: Context) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _statusConnection = MutableLiveData<Status>()

    val statusConnection: MutableLiveData<Status>
        get() = _statusConnection

    init {
            connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: android.net.Network) {
                    _statusConnection.postValue(Status.Available)
                }

                override fun onLost(network: android.net.Network) {
                    _statusConnection.postValue(Status.Lost)
                }

                override fun onLosing(network: android.net.Network, maxMsToLive: Int) {
                    _statusConnection.postValue(Status.Losing)
                }

                override fun onUnavailable() {
                    _statusConnection.postValue(Status.Unavailable)
                }
            })
    }
}