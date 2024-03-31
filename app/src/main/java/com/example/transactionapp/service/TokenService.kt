package com.example.transactionapp.service

import android.content.Intent
import androidx.lifecycle.LifecycleService
import com.example.transactionapp.domain.DaggerAppComponent
import com.example.transactionapp.ui.screen.login.LoginActivity
import com.example.transactionapp.ui.viewmodel.auth.Auth
import com.example.transactionapp.ui.viewmodel.model.TokenResponseSealed
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class TokenService: LifecycleService(){

    @Inject lateinit var auth: Auth
    private var expiredTime: Long = 0

    //TODO: Add Token Shared Preference
    override fun onCreate() {
        super.onCreate()

        DaggerAppComponent.create().inject(this)

//        auth.validateToken("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuaW0iOiIxMzUyMTAzMSIsImlhdCI6MTcxMTY2MzAxMywiZXhwIjoxNzExNjYzMzEzfQ.SaHoQ3oHAQJ8izPh3ZuEgvs7jnuq-qmeqbg_N4iY6eQ")


        auth.tokenResponse.observe(this){
            when(it){
                is TokenResponseSealed.Success -> {
                    expiredTime = it.data.exp - System.currentTimeMillis()/1000
                    GlobalScope.launch {
                        Thread.sleep(expiredTime*1000)
//                        auth.validateToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuaW0iOiIxMzUyMTAzMSIsImlhdCI6MTcxMTY1OTYxOSwiZXhwIjoxNzExNjU5OTE5fQ.ORrmOh3dxNFHpKiiWBzTZNx-qxsvmY7Y8ys238ErsKU")

                    }
                }
                is TokenResponseSealed.Error -> {
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