package com.example.transactionapp.ui.screen.mainmenu

import CameraAdapter
import LocationAdapter
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.transactionapp.R
import com.example.transactionapp.databinding.ActivityMainBinding
import com.example.transactionapp.service.ConnectionStatusService
import com.example.transactionapp.service.TokenService
import com.example.transactionapp.ui.viewmodel.location.LocationViewModel
import com.example.transactionapp.ui.viewmodel.navigation.NavigationViewModel
import com.example.transactionapp.ui.viewmodel.transaction.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var db : TransactionViewModel
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var navigationViewModel : NavigationViewModel

    private lateinit var locationAdapter: LocationAdapter
    private lateinit var cameraAdapter: CameraAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the activity to portrait
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Bind layout
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize nav controller
        val navController = (supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment).navController

        // Start connection service
        startService(Intent(this, ConnectionStatusService::class.java))
        startService(Intent(this, TokenService::class.java))

        // Initialize ViewModel
        db = ViewModelProvider(this)[TransactionViewModel::class.java]
        locationViewModel = ViewModelProvider(this)[LocationViewModel::class.java]
        navigationViewModel = ViewModelProvider(this)[NavigationViewModel::class.java]
        db.getAllDate()
        db.getTransactions("all")
        db.getCashFlowAndGrowthByMonth(Date())
        db.getStatisticByMonth(Date())

        // Navigation Button Listener
        binding.ibTransactionBtn.setOnClickListener {
            db.getAllDate()
            db.getTransactions("all")
            db.getCashFlowAndGrowthByMonth(Date())
            navigationViewModel.navigate("transaction")
            navController.navigate(R.id.transaction)
        }

        binding.ibScanBtn.setOnClickListener {
            navigationViewModel.navigate("scan")
            navController.navigate(R.id.scan)
        }

        binding.ibAddBtn.setOnClickListener {
            navigationViewModel.navigate("newTransaction")
            navController.navigate(R.id.transactionForm)
        }

        binding.ibStatisticsBtn.setOnClickListener {
            navigationViewModel.navigate("statistics")
            navController.navigate(R.id.statistics)
        }

        binding.ibSettingsBtn.setOnClickListener {
            navigationViewModel.navigate("settings")
            navController.navigate(R.id.settings)
        }

        // Navigation components observer
        navigationViewModel.fragmentName.observe(this) {
            binding.ibTransactionBtn.setImageResource(R.drawable.transaction_inactive_ic)
            binding.ibScanBtn.setImageResource(R.drawable.scan_inactive_ic)
            binding.ibStatisticsBtn.setImageResource(R.drawable.statistic_inactive_ic)
            binding.ibSettingsBtn.setImageResource(R.drawable.setting_inactive_ic)
            when (it) {
                "transaction" -> {
                    binding.ibTransactionBtn.setImageResource(R.drawable.transaction_active_ic)
                    binding.tvHeader.setText(R.string.title_transactions)
                }
                "scan" -> {
                    binding.ibScanBtn.setImageResource(R.drawable.scan_active_ic)
                    binding.tvHeader.setText(R.string.title_scan)
                }
                "newTransaction" -> {
                    binding.tvHeader.setText(R.string.title_new_transaction)
                }
                "statistics" -> {
                    binding.ibStatisticsBtn.setImageResource(R.drawable.statistic_active_ic)
                    binding.tvHeader.setText(R.string.title_statistics)
                }
                "settings" -> {
                    binding.ibSettingsBtn.setImageResource(R.drawable.setting_active_ic)
                    binding.tvHeader.setText(R.string.title_settings)
                }
            }
        }

        db.addTransactionStatus.observe(this){
            if (it){
                db.getAllDate()
                db.getTransactions("all")
                db.getCashFlowAndGrowthByMonth(Date())
                db.getStatisticByMonth(Date())
                
                navController.navigate(R.id.transaction)
                db.resetAddTransactionStatus()
                db.changeAddStatus(false)
            }
        }

        db.cameraStatus.observe(this){
            if (it){
                navController.navigate(R.id.transactionForm)
                db.changeCameraStatus(false)
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onStart() {
        super.onStart()
        db = ViewModelProvider(this)[TransactionViewModel::class.java]
        db.getAllDate()
        locationAdapter = LocationAdapter({ this }, locationViewModel)
        locationAdapter.requestLocationUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        db.removeObserveAllData(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1002 && resultCode == RESULT_OK) {
            Toast.makeText(this, "Email sent", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "Email not sent", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LocationAdapter.LOCATION_PERMISSION_REQUEST_CODE) {
            if (locationAdapter.hasLocationPermission()) {
                locationAdapter.startLocationUpdates()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}