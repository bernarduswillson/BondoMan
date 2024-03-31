package com.example.transactionapp.ui.screen.mainmenu.fragment

import TransactionDetails
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.transactionapp.R
import com.example.transactionapp.databinding.FragmentTransactionBinding
import com.example.transactionapp.ui.screen.mainmenu.adapter.TransactionAdapter
import com.example.transactionapp.ui.viewmodel.transaction.TransactionViewModel
import com.example.transactionapp.utils.changeNominalToIDN
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

@AndroidEntryPoint
class Transaction : Fragment() {
    private val db: TransactionViewModel by activityViewModels()
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentTransactionBinding.inflate(layoutInflater)

        db.dateAll.observe(requireActivity()) { transactionList ->
            Log.d("TransactionFragment", "onCreateView: $transactionList")
            val transactionAdapter = TransactionAdapter(transactionList, this::onItemClickHandler)
            val recyclerViewTransaction: RecyclerView = binding.transactionHistoryRecyclerView
            recyclerViewTransaction.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            recyclerViewTransaction.adapter = transactionAdapter
        }
        db.balance.observe(requireActivity()){
            binding.balanceText.text = if (it<0) "- "+changeNominalToIDN(abs(it)) else changeNominalToIDN(it)
        }
        db.cashFlow.observe(requireActivity()){
            binding.cashflowText.text = changeNominalToIDN(it)
            binding.cashflowText.setTextColor(if (it<0) ContextCompat.getColor(requireContext(),R.color.R3) else ContextCompat.getColor(requireContext(), R.color.G3))
        }
        db.growth.observe(requireActivity()){
            binding.growthText.text = it.toString()+"%"
            if (it>0) binding.growthText.text = "+"+binding.growthText.text
            binding.growthText.setTextColor(if (it<0) ContextCompat.getColor(requireContext(),R.color.R3) else ContextCompat.getColor(requireContext(), R.color.G3))
        }

        return binding.root
    }


    private fun onItemClickHandler(id: Int){
        Log.d("TransactionFragment", "onItemClickHandler: $id")


        val args = Bundle()
        args.putInt(TransactionDetails.ARG_TRANSACTION_ID, id)

        navController.navigate(R.id.transactionDetails, args)
    }
}