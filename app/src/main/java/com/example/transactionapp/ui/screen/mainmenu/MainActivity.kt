package com.example.transactionapp.ui.screen.mainmenu

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.transactionapp.R
import com.example.transactionapp.databinding.ActivityMainBinding
import com.example.transactionapp.service.ConnectionStatusService
import com.example.transactionapp.service.TokenService
import com.example.transactionapp.ui.screen.mainmenu.fragment.Scan
import com.example.transactionapp.ui.screen.mainmenu.fragment.Settings
import com.example.transactionapp.ui.screen.mainmenu.fragment.Statistics
import com.example.transactionapp.ui.screen.mainmenu.fragment.Transaction
import com.example.transactionapp.ui.screen.mainmenu.fragment.TransactionForm
import com.example.transactionapp.ui.viewmodel.location.LocationViewModel
import com.example.transactionapp.ui.viewmodel.transaction.TransactionViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var db : TransactionViewModel
    private lateinit var locationViewModel: LocationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startService(Intent(this, ConnectionStatusService::class.java))
        //TODO: dont forget to startservice token
//        startService(Intent(this, TokenService::class.java))

        db = ViewModelProvider(this)[TransactionViewModel::class.java]
        locationViewModel = ViewModelProvider(this)[LocationViewModel::class.java]
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
            binding.bottomNavigationView.selectedItemId = R.id.empty
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

        db.addTransactionStatus.observe(this){
            if (it){
                db.getAllDate()
                db.getTransactions("all")
                db.getCashFlowAndGrowthByMonth(Date())
                fragment = supportFragmentManager.beginTransaction()
                fragment.replace(frame, Transaction())
                fragment.addToBackStack(null)
                fragment.commit()
                db.resetAddTransactionStatus()
                db.changeAddStatus(false)
                binding.bottomNavigationView.selectedItemId = R.id.IbTransactionBtn
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onStart() {
        super.onStart()
        db = ViewModelProvider(this)[TransactionViewModel::class.java]
        db.getAllDate()
        NewLocationData()
    }

    override fun onDestroy() {
        super.onDestroy()
        db.removeObserveAllData(this)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 1010){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                NewLocationData()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun NewLocationData(){
        var locationRequest = com.google.android.gms.location.LocationRequest()
        locationRequest.priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient!!.requestLocationUpdates(
                locationRequest,locationCallback, Looper.myLooper()
            )
            return
        }

    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var lastLocation: Location? = locationResult.lastLocation
            if (lastLocation != null) {
                locationViewModel.setLocation(getCityName(lastLocation.latitude, lastLocation.longitude))
            }
        }
    }

    private fun getCityName(lat: Double,long: Double):String{
        var cityName = ""
        val geoCoder = Geocoder(this, Locale.getDefault())
        val Address = geoCoder.getFromLocation(lat,long,3)

        cityName = Address!!.get(0).subAdminArea
        return cityName
    }
}