package com.example.transactionapp.ui.screen.mainmenu

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColor
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.transactionapp.R
import com.example.transactionapp.databinding.ActivityMainBinding
import com.example.transactionapp.service.ConnectionStatusService
import com.example.transactionapp.service.TokenService
import com.example.transactionapp.ui.screen.mainmenu.fragment.Scan
import com.example.transactionapp.ui.screen.mainmenu.fragment.Settings
import com.example.transactionapp.ui.screen.mainmenu.fragment.Statistics
import com.example.transactionapp.ui.screen.mainmenu.fragment.Transaction
import com.example.transactionapp.ui.screen.mainmenu.fragment.TransactionForm
import com.example.transactionapp.ui.viewmodel.location.LocationModel
import com.example.transactionapp.ui.viewmodel.location.LocationViewModel
import com.example.transactionapp.ui.viewmodel.navigation.NavigationViewModel
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
    private lateinit var navigationViewModel : NavigationViewModel
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the activity to portrait
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Bind layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize nav controller
        val navController = (supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment).navController

        // Start connection service
        startService(Intent(this, ConnectionStatusService::class.java))
        //TODO: dont forget to startservice token
        //startService(Intent(this, TokenService::class.java))

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
        NewLocationData()
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
                locationViewModel.setLocation(LocationModel(
                    locationName = getCityName(lastLocation.latitude, lastLocation.longitude),
                    latitude = lastLocation.latitude,
                    longitude = lastLocation.longitude
                ))
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