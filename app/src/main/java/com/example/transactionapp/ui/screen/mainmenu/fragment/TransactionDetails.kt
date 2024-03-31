
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.transactionapp.R
import com.example.transactionapp.databinding.FragmentTransactionDetailsBinding
import com.example.transactionapp.domain.db.model.Transaction
import com.example.transactionapp.ui.viewmodel.location.LocationModel
import com.example.transactionapp.ui.viewmodel.location.LocationViewModel
import com.example.transactionapp.ui.viewmodel.transaction.TransactionViewModel
import com.example.transactionapp.utils.changeDateTypeToStandardDateLocal
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Locale

class TransactionDetails : Fragment() {

    private val db : TransactionViewModel by activityViewModels()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val locationViewModel: LocationViewModel by activityViewModels()
    private val locationData = MutableStateFlow<LocationModel?>(null)

    companion object {
        const val ARG_TRANSACTION_ID = "transaction_id"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentTransactionDetailsBinding.inflate(layoutInflater)

        val transactionId = requireArguments().getInt(ARG_TRANSACTION_ID)

        db.getTransactionById(transactionId)
        db.transactionById.observe(viewLifecycleOwner) {
            binding.titleInput.setText(it.title)

            binding.dateInput.text = changeDateTypeToStandardDateLocal(it.createdAt)

            binding.categoryInput.text = it.category

            binding.amountInput.setText(it.nominal.toString())

            locationViewModel.location.observe(viewLifecycleOwner){
                binding.locationInput.text = it.locationName
                locationData.value = it
            }

//            binding.locationInput.text = it.location
        }

        binding.saveTransactionButton.setOnClickListener {
            if (binding.titleInput.text.toString() == ""){
                return@setOnClickListener
            }
            if (binding.amountInput.text.toString() == ""){
                return@setOnClickListener
            }

            if (binding.titleInput.text.toString() != "" && binding.amountInput.text.toString() != ""){
                db.updateTransaction(
                    Transaction(
                        id = transactionId,
                        title = binding.titleInput.text.toString(),
                        category = binding.categoryInput.text.toString(),
                        nominal = binding.amountInput.text.toString().toLong(),
                        createdAt = db.transactionById.value?.createdAt!!,
                        location = binding.locationInput.text.toString(),
//                        lat = db.transactionById.value?.lat!!,
//                        long = db.transactionById.value?.long!!
                        lat = locationData.value?.latitude?:0.0,
                        long = locationData.value?.longitude?:0.0,
                    )
                )
                db.changeAddStatus(true)
                Toast.makeText(requireContext(), "Transaction Updated", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressed()
            }
        }

        binding.deleteTransactionButton.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setMessage("Are you sure you want to delete this transaction?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    db.deleteTransaction(db.transactionById.value!!)
                    db.changeAddStatus(true)
                    Toast.makeText(requireContext(), "Transaction Deleted", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.cancel()
                }
            val alert: AlertDialog = builder.create()
            alert.show()
        }

        binding.locationInput.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${db.transactionById.value?.lat},${db.transactionById.value?.long}"))
            startActivity(intent)
        }

        return binding.root
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
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        if (ActivityCompat.checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
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
                locationViewModel.setLocation(
                    LocationModel(
                    locationName = getCityName(lastLocation.latitude, lastLocation.longitude),
                    latitude = lastLocation.latitude,
                    longitude = lastLocation.longitude
                )
                )
            }
        }
    }

    private fun getCityName(lat: Double,long: Double):String{
        var cityName = ""
        val geoCoder = Geocoder(requireContext(), Locale.getDefault())
        val Address = geoCoder.getFromLocation(lat,long,3)

        cityName = Address!!.get(0).subAdminArea
        return cityName
    }
}