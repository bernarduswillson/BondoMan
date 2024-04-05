package com.example.transactionapp.ui.screen.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import com.example.transactionapp.R
import com.example.transactionapp.helper.getTokenSharedPref
import com.example.transactionapp.service.ConnectionStatusService
import com.example.transactionapp.ui.screen.login.LoginActivity
import com.example.transactionapp.ui.screen.mainmenu.MainActivity
import com.example.transactionapp.ui.viewmodel.auth.Auth
import com.example.transactionapp.ui.viewmodel.model.TokenResponseSealed
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val auth: Auth by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        startService(Intent(this, ConnectionStatusService::class.java))
        val token = getTokenSharedPref(this)

        auth.validateToken("Bearer $token")

        Handler().postDelayed({
            auth.tokenResponse.observe(this@SplashActivity){
                when(it){
                    is TokenResponseSealed.Success -> {
                        val intent = Intent(this@SplashActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        return@observe
                    }
                    is TokenResponseSealed.Error -> {
                        val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                        return@observe
                    }
                    else -> {}
                }
            }
        }, 2500)
    }
}