package com.example.transactionapp.ui.screen.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.transactionapp.databinding.ActivityLoginBinding
import com.example.transactionapp.domain.api.model.LoginInput
import com.example.transactionapp.ui.viewmodel.auth.Auth
import com.example.transactionapp.ui.viewmodel.model.LoginResponseSealed
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var auth: Auth
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = ViewModelProvider(this)[Auth::class.java]

        binding.loginButton.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
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
                    Toast.makeText(this, data.data.toString(), Toast.LENGTH_SHORT).show()
                }
                is LoginResponseSealed.Error -> {
                    Toast.makeText(this, data.message, Toast.LENGTH_SHORT).show()
                }
                is LoginResponseSealed.Loading -> {
                    Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}