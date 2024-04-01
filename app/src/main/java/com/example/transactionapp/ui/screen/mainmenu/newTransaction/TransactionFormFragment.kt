package com.example.transactionapp.ui.screen.mainmenu.newTransaction

import LocationAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
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
import androidx.fragment.app.activityViewModels
import com.example.transactionapp.R
import com.example.transactionapp.databinding.FragmentTransactionFormBinding
import com.example.transactionapp.domain.db.model.Transaction
import com.example.transactionapp.helper.GetRandomData
import com.example.transactionapp.ui.viewmodel.location.LocationModel
import com.example.transactionapp.ui.viewmodel.location.LocationViewModel
import com.example.transactionapp.ui.screen.mainmenu.transaction.TransactionViewModel
import com.example.transactionapp.utils.changeDateTypeToStandardDateLocal
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Date

@AndroidEntryPoint
class NewTransactionFragment : Fragment() {
    private val db : TransactionViewModel by activityViewModels()
    private val locationViewModel: LocationViewModel by activityViewModels()
    private lateinit var receiver: BroadcastReceiver
    private val getRandomData = GetRandomData()
    private val locationData = MutableStateFlow<LocationModel?>(null)

    private lateinit var locationAdapter: LocationAdapter

    companion object {
        const val ARG_ITEM_NAME = "item_name"
        const val ARG_ITEM_NOMINAL = "item_nominal"
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Bind layout using layout binding
        val binding = FragmentTransactionFormBinding.inflate(layoutInflater)

        if (arguments?.getString(ARG_ITEM_NAME) != null){
            binding.titleInput.text = Editable.Factory.getInstance().newEditable(arguments?.getString(
                ARG_ITEM_NAME
            ))
        }
        if (arguments?.getLong(ARG_ITEM_NOMINAL) != null){
            binding.amountInput.text = Editable.Factory.getInstance().newEditable(arguments?.getLong(
                ARG_ITEM_NOMINAL
            ).toString())
        }

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
            binding.locationInput.text = it.locationName
            locationData.value = it
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
                        location = binding.locationInput.text.toString(),
                        lat = locationData.value?.latitude?:0.0,
                        long = locationData.value?.longitude?:0.0,
                    )
                )
                db.changeAddStatus(true)
                Toast.makeText(requireContext(), "Transaction Added", Toast.LENGTH_SHORT).show()
            }
        }

        db.atomicTransaction.observe(viewLifecycleOwner){
            binding.titleInput.text = Editable.Factory.getInstance().newEditable(it.title)
            binding.amountInput.text = Editable.Factory.getInstance().newEditable(it.nominal.toString())
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

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onStart() {
        super.onStart()
        locationAdapter = LocationAdapter({ requireActivity() }, locationViewModel)
        locationAdapter.startLocationUpdates()
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