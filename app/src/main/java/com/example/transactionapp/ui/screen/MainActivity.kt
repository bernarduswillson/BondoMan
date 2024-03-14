package com.example.transactionapp.ui.screen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.transactionapp.R
import com.example.transactionapp.domain.api.model.LoginInput
import com.example.transactionapp.ui.viewmodel.auth.Auth
import com.example.transactionapp.ui.viewmodel.connection.ConnectionStatus
import com.example.transactionapp.ui.viewmodel.model.TokenResponseSealed
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var auth: Auth
    private lateinit var statusConnectionStatus: ConnectionStatus

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginButton: Button = findViewById(R.id.login_button)
        val statusText: TextView = findViewById(R.id.status_text)

        auth = ViewModelProvider(this)[Auth::class.java]
        statusConnectionStatus = ConnectionStatus(this)

        loginButton.setOnClickListener {
//            auth.validateToken(
//                "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuaW0iOiIxMzUyMTAzMSIsImlhdCI6MTcxMDI2OTExMSwiZXhwIjoxNzEwMjY5NDExfQ.iNnGYSduRAlMIVByhLuQtQlFsM_nox5kYEZz9wciCU4"
//            )
//            checkToken()
        }

        auth.tokenResponse.observe(this){
            when(val data = it){
                is TokenResponseSealed.Success -> {
                    println("Token: ${data.data}")
                }
                is TokenResponseSealed.Error -> {
                    println("Error: ${data.message}")
                }
                is TokenResponseSealed.Loading -> {
                    println("Loading: ")
                }
            }
        }

        statusConnectionStatus.statusConnection.observe(this){
            statusText.text = it.toString()
        }
    }
}