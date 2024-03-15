package com.example.transactionapp.helper

import android.content.Context
import android.net.ConnectivityManager
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.transactionapp.R
import com.example.transactionapp.ui.viewmodel.model.Status
import com.google.android.material.snackbar.Snackbar

class ConnectionStatus(context: Context, view: View) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _statusConnection = MutableLiveData<Status>()

    val statusConnection: MutableLiveData<Status>
        get() = _statusConnection

    init {
            connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: android.net.Network) {
                    super.onAvailable(network)
                    _statusConnection.postValue(Status.Available)
                    Snackbar.make(view, "Comeback Online", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(context.getColor(R.color.G3))
                        .setTextColor(context.getColor(R.color.N1))
                        .show()
                }

                override fun onLost(network: android.net.Network) {
                    super.onLost(network)
                    _statusConnection.postValue(Status.Lost)
                    Snackbar.make(view, "Connection Lost", Snackbar.LENGTH_SHORT)
                        .show()
                }

                override fun onLosing(network: android.net.Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)
                    _statusConnection.postValue(Status.Losing)
                    Toast.makeText(context, "Connection Lost", Toast.LENGTH_SHORT).show()
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    _statusConnection.postValue(Status.Unavailable)
                    Toast.makeText(context, "Connection Unavailable", Toast.LENGTH_SHORT).show()
                }
            })
    }
}