package com.example.transactionapp.service

import android.content.Intent
import androidx.lifecycle.LifecycleService
import com.example.transactionapp.domain.DaggerAppComponent
import com.example.transactionapp.helper.changeEmailSharedPref
import com.example.transactionapp.helper.getTokenSharedPref
import com.example.transactionapp.ui.screen.login.LoginActivity
import com.example.transactionapp.ui.viewmodel.auth.Auth
import com.example.transactionapp.ui.viewmodel.model.TokenResponseSealed
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class TokenService: LifecycleService(){

    @Inject lateinit var auth: Auth
    private var expiredTime: Long = 0
    override fun onCreate() {
        super.onCreate()

        DaggerAppComponent.create().inject(this)

        val token = getTokenSharedPref(this)

        auth.validateToken("Bearer $token")

        auth.tokenResponse.observe(this){
            when(it){
                is TokenResponseSealed.Success -> {
                    expiredTime = it.data.exp - System.currentTimeMillis()/1000
                    GlobalScope.launch {
                        Thread.sleep(expiredTime*1000 + 1000)
                        auth.validateToken("Bearer $token")
                    }
                }
                is TokenResponseSealed.Error -> {
                    changeEmailSharedPref(this@TokenService, "")
                    changeEmailSharedPref(this@TokenService, "")
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    val intents = Intent(this@TokenService, TokenService::class.java)
                    intents.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    stopService(intent)
                }
                else -> {}
            }
        }
    }

}