package com.example.transactionapp.ui.screen.mainmenu.transactionDetail
import LocationAdapter
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.transactionapp.databinding.FragmentTransactionDetailsBinding
import com.example.transactionapp.domain.db.model.Transaction
import com.example.transactionapp.ui.viewmodel.location.LocationModel
import com.example.transactionapp.ui.viewmodel.location.LocationViewModel
import com.example.transactionapp.ui.screen.mainmenu.transaction.TransactionViewModel
import com.example.transactionapp.utils.changeDateTypeToStandardDateLocal
import kotlinx.coroutines.flow.MutableStateFlow

class TransactionDetailsFragment : Fragment() {

    private val db : TransactionViewModel by activityViewModels()
    private val locationViewModel: LocationViewModel by activityViewModels()
    private val locationData = MutableStateFlow<LocationModel?>(null)

    private lateinit var locationAdapter: LocationAdapter

    companion object {
        const val ARG_TRANSACTION_ID = "transaction_id"
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Bind layout using layout binding
        val binding = FragmentTransactionDetailsBinding.inflate(layoutInflater)

        // Get an instance of transaction from transaction id argument
        val transactionId = TransactionDetailsFragmentArgs.fromBundle(requireArguments()).transactionId
        Log.i("Args", transactionId.toString())
        db.getTransactionById(transactionId)
        db.transactionById.observe(viewLifecycleOwner) {
            binding.titleInput.setText(it.title)

            binding.dateInput.text = changeDateTypeToStandardDateLocal(it.createdAt)

            binding.categoryInput.text = it.category

            binding.amountInput.setText(it.nominal.toString())

            binding.locationInput.text = it.location
            locationData.value?.latitude ?: it.lat
            locationData.value?.longitude ?: it.long

            binding.updateButton.setOnClickListener {
                locationAdapter = LocationAdapter({ requireActivity() }, locationViewModel)
                locationAdapter.startLocationUpdates()

                locationViewModel.location.observe(viewLifecycleOwner){
                    binding.locationInput.text = it.locationName
                    locationData.value = it
                }
            }
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
        if (requestCode == LocationAdapter.LOCATION_PERMISSION_REQUEST_CODE) {
            if (locationAdapter.hasLocationPermission()) {
                locationAdapter.startLocationUpdates()
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}