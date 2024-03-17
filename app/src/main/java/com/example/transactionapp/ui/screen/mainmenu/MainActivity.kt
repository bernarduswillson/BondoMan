package com.example.transactionapp.ui.screen.mainmenu

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.transactionapp.R
import com.example.transactionapp.databinding.ActivityMainBinding
import com.example.transactionapp.ui.screen.mainmenu.fragment.Scan
import com.example.transactionapp.ui.screen.mainmenu.fragment.Settings
import com.example.transactionapp.ui.screen.mainmenu.fragment.Statistics
import com.example.transactionapp.ui.screen.mainmenu.fragment.Transaction
import com.example.transactionapp.ui.screen.mainmenu.fragment.TransactionForm
import com.example.transactionapp.ui.viewmodel.transaction.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var db : TransactionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = ViewModelProvider(this)[TransactionViewModel::class.java]
        db.getAllDate()
        db.getTransactions("all")
        db.getCashFlowAndGrowthByMonth(Date())

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
                    db.getAllDate()
                    db.getTransactions("all")
                    db.getCashFlowAndGrowthByMonth(Date())
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

    override fun onStart() {
        super.onStart()
        db = ViewModelProvider(this)[TransactionViewModel::class.java]
        db.getAllDate()
    }
}