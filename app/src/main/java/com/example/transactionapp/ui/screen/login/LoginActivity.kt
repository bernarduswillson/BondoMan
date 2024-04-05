package com.example.transactionapp.ui.screen.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.transactionapp.databinding.ActivityLoginBinding
import com.example.transactionapp.domain.api.model.LoginInput
import com.example.transactionapp.helper.changeEmailSharedPref
import com.example.transactionapp.helper.changeTokenSharedPref
import com.example.transactionapp.service.TokenService
import com.example.transactionapp.ui.screen.connection.LostConnectionActivity
import com.example.transactionapp.ui.screen.mainmenu.MainActivity
import com.example.transactionapp.ui.viewmodel.auth.Auth
import com.example.transactionapp.ui.viewmodel.model.LoginResponseSealed
import com.example.transactionapp.utils.isInternetAvailable
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var auth: Auth
    private lateinit var intent: Intent
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = ViewModelProvider(this)[Auth::class.java]

        binding.loginButton.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            if (!isInternetAvailable(this)) {
                val intent = Intent(this, LostConnectionActivity::class.java)
                startActivity(intent)
                return@setOnClickListener
            }
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText( this, "Please fill required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.login(LoginInput(email, password))
            }
        }

        auth.loginResponse.observe(this) {
            when (val data = it) {
                is LoginResponseSealed.Success -> {

                    changeEmailSharedPref(this@LoginActivity, binding.editTextEmail.text.toString())
                    changeTokenSharedPref(this@LoginActivity, data.data.token)

                    intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                }
                is LoginResponseSealed.Error -> {
                    Toast.makeText(this, data.message, Toast.LENGTH_SHORT).show()
                }
                is LoginResponseSealed.Loading -> {}
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, TokenService::class.java))
    }
}