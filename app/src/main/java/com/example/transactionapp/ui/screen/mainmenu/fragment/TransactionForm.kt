package com.example.transactionapp.ui.screen.mainmenu.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationRequest
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import com.example.transactionapp.R
import com.example.transactionapp.databinding.FragmentTransactionFormBinding
import com.example.transactionapp.domain.db.model.Transaction
import com.example.transactionapp.helper.GetRandomData
import com.example.transactionapp.ui.viewmodel.location.LocationViewModel
import com.example.transactionapp.ui.viewmodel.transaction.TransactionViewModel
import com.example.transactionapp.utils.changeDateTypeToStandardDateLocal
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class TransactionForm : Fragment() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val db : TransactionViewModel by activityViewModels()
    private val locationViewModel: LocationViewModel by activityViewModels()
    private lateinit var receiver: BroadcastReceiver
    private val getRandomData = GetRandomData()

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentTransactionFormBinding.inflate(layoutInflater)

        val categories = arrayOf("Income", "Expense", "Savings")
        val arrayAdp = ArrayAdapter(requireActivity(), R.layout.selected_dropdown_item, categories)
        arrayAdp.setDropDownViewResource(R.layout.dropdown_item)
        binding.categoryInput.adapter = arrayAdp

        binding.categoryInput.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.d("TransactionForm", "onItemSelected: ${categories[position]}")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("TransactionForm", "onNothingSelected: ")
            }
        }

        binding.dateInput.text = changeDateTypeToStandardDateLocal(Date())

        locationViewModel.location.observe(viewLifecycleOwner){
            binding.locationInput.text = it
        }


        binding.newTransactionButton.setOnClickListener {
            if (binding.titleInput.text.toString() == ""){
                Toast.makeText(requireContext(), "Fill the title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (binding.amountInput.text.toString() == ""){
                Toast.makeText(requireContext(), "Fill the amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (binding.titleInput.text.toString() != "" && binding.amountInput.text.toString() != ""){
                db.insertTransaction(
                    Transaction(
                        title = binding.titleInput.text.toString(),
                        category = binding.categoryInput.selectedItem.toString(),
                        nominal = binding.amountInput.text.toString().toLong(),
                        createdAt = Date(),
                        location = binding.locationInput.text.toString()
                    )
                )
                db.changeAddStatus(true)
                Toast.makeText(requireContext(), "Transaction Added", Toast.LENGTH_SHORT).show()
            }
        }

        db.isRandom.observe(viewLifecycleOwner){
            if (it){
                binding.titleInput.text = Editable.Factory.getInstance().newEditable(getRandomData.getRandomTitle())
                binding.amountInput.text = Editable.Factory.getInstance().newEditable(getRandomData.getRandomNominal().toString())
                binding.categoryInput.setSelection(arrayAdp.getPosition(getRandomData.getRandomCategory()))
            }
        }


        val filter = IntentFilter("IsRandom")
        receiver = object : BroadcastReceiver(){
            override fun onReceive(p0: Context?, p1: Intent?) {
                db.changeIsRandom(p1?.getBooleanExtra("isRandom", false)!!)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireActivity().registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        RequestPermission()
    }

    fun RequestPermission(){
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),
            1010
        )
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
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (ActivityCompat.checkSelfPermission(requireActivity(),android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(requireActivity(),android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
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
        val geoCoder = Geocoder(requireActivity(), Locale.getDefault())
        val Address = geoCoder.getFromLocation(lat,long,3)

        cityName = Address!!.get(0).subAdminArea
        return cityName
    }

}