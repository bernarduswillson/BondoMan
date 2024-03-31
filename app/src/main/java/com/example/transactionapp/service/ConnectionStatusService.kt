package com.example.transactionapp.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.IBinder
import android.widget.Toast

class ConnectionStatusService: Service() {
    private lateinit var connectivityManager: ConnectivityManager

    override fun onCreate() {
        super.onCreate()

        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {

            override fun onLost(network: android.net.Network) {
                super.onLost(network)
                Toast.makeText(this@ConnectionStatusService, "Connection Lost", Toast.LENGTH_SHORT).show()
            }

            override fun onLosing(network: android.net.Network, maxMsToLive: Int) {
                super.onLosing(network, maxMsToLive)
                Toast.makeText(this@ConnectionStatusService, "Connection Lost", Toast.LENGTH_SHORT).show()
            }

            override fun onUnavailable() {
                super.onUnavailable()
                Toast.makeText(this@ConnectionStatusService, "Connection Lost", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}