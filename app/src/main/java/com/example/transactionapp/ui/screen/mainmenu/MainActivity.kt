package com.example.transactionapp.ui.screen.mainmenu

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.transactionapp.R
import com.example.transactionapp.databinding.ActivityMainBinding
import com.example.transactionapp.ui.screen.mainmenu.fragment.Scan
import com.example.transactionapp.ui.screen.mainmenu.fragment.Settings
import com.example.transactionapp.ui.screen.mainmenu.fragment.Statistics
import com.example.transactionapp.ui.screen.mainmenu.fragment.Transaction
import com.example.transactionapp.ui.screen.mainmenu.fragment.TransactionForm
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val frame = R.id.navHostFragment

        var fragment = supportFragmentManager.beginTransaction()

        fragment.replace(frame, Transaction())
        fragment.addToBackStack(null)
        fragment.commit()

        binding.fabAddTransaction.setOnClickListener {
            fragment = supportFragmentManager.beginTransaction()
            fragment.replace(frame, TransactionForm())
            fragment.addToBackStack(null)
            fragment.commit()
        }

        //TODO: Add Animation When Fragment Change

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.IbTransactionBtn -> {
                    fragment = supportFragmentManager.beginTransaction()
                    fragment.replace(frame, Transaction())
                    fragment.addToBackStack(null)
                    fragment.commit()
                    true
                }
                R.id.IbScanBtn -> {
                    fragment = supportFragmentManager.beginTransaction()
                    fragment.replace(frame, Scan())
                    fragment.addToBackStack(null)
                    fragment.commit()
                    true
                }
                R.id.IbSettingsBtn -> {
                    fragment = supportFragmentManager.beginTransaction()
                    fragment.replace(frame, Settings())
                    fragment.addToBackStack(null)
                    fragment.commit()
                    true
                }
                R.id.IbStatisticsBtn -> {
                    fragment = supportFragmentManager.beginTransaction()
                    fragment.replace(frame, Statistics())
                    fragment.addToBackStack(null)
                    fragment.commit()
                    true
                }
                else -> {
                    false
                }
            }
        }
    }
}