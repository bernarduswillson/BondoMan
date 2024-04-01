package com.example.transactionapp.ui.screen.mainmenu

import CameraAdapter
import LocationAdapter
import android.content.Intent
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
import com.example.transactionapp.ui.viewmodel.location.LocationViewModel
import com.example.transactionapp.ui.viewmodel.transaction.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var db : TransactionViewModel
    private lateinit var locationViewModel: LocationViewModel

    private lateinit var locationAdapter: LocationAdapter
    private lateinit var cameraAdapter: CameraAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navController = (supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment).navController

        startService(Intent(this, ConnectionStatusService::class.java))
        //TODO: dont forget to startservice token
//        startService(Intent(this, TokenService::class.java))

        db = ViewModelProvider(this)[TransactionViewModel::class.java]
        locationViewModel = ViewModelProvider(this)[LocationViewModel::class.java]
        db.getAllDate()
        db.getTransactions("all")
        db.getCashFlowAndGrowthByMonth(Date())
        db.getStatisticByMonth(Date())

//        val frame = R.id.navHostFragment
//
//        var fragment = supportFragmentManager.beginTransaction()
//
//        fragment.replace(frame, Transaction())
//        fragment.addToBackStack(null)
//        fragment.commit()
//
        binding.fabAddTransaction.setOnClickListener {

            navController.navigate(R.id.transactionForm)
            binding.bottomNavigationView.selectedItemId = R.id.empty
        }

        //TODO: Add Animation When Fragment Change

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.IbTransactionBtn -> {
                    db.getAllDate()
                    db.getTransactions("all")
                    db.getCashFlowAndGrowthByMonth(Date())

                    navController.navigate(R.id.transaction)
                    true
                }
                R.id.IbScanBtn -> {

                    navController.navigate(R.id.scan)
                    true
                }
                R.id.IbSettingsBtn -> {

                    navController.navigate(R.id.settings)
                    true
                }
                R.id.IbStatisticsBtn -> {

                    navController.navigate(R.id.statistics)
                    true
                }
                else -> {
                    false
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
                binding.bottomNavigationView.selectedItemId = R.id.IbTransactionBtn
            }
        }

        db.cameraStatus.observe(this){
            if (it){
                navController.navigate(R.id.transactionForm)
                db.changeCameraStatus(false)
                binding.bottomNavigationView.selectedItemId = R.id.empty
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

        cameraAdapter = CameraAdapter(this, this, previewView = null, imageCaptureCallback = null)
        cameraAdapter.bindCameraUseCases()
    }

    override fun onDestroy() {
        super.onDestroy()
        db.removeObserveAllData(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1002) {
            Toast.makeText(this, "Email sent", Toast.LENGTH_SHORT).show()
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