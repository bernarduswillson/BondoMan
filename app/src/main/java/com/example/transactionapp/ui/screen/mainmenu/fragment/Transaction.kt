package com.example.transactionapp.ui.screen.mainmenu.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.transactionapp.databinding.FragmentTransactionBinding
import com.example.transactionapp.ui.screen.mainmenu.adapter.TransactionAdapter
import com.example.transactionapp.ui.viewmodel.transaction.TransactionViewModel
import com.example.transactionapp.utils.changeNominalToIDN
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

@AndroidEntryPoint
class Transaction : Fragment() {
    private val db: TransactionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentTransactionBinding.inflate(layoutInflater)


        db.dateAll.observe(requireActivity()){
            Log.d("TransactionFragment", "onCreateView: $it")
            val transactionAdapter = TransactionAdapter(it)
            val recyclerViewTransaction: RecyclerView = binding.transactionHistoryRecyclerView
            recyclerViewTransaction.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            recyclerViewTransaction.adapter = transactionAdapter
        }
        db.balance.observe(requireActivity()){
            binding.balanceText.text = if (it<0) "- "+changeNominalToIDN(abs(it)) else changeNominalToIDN(it)
        }
        db.cashFlow.observe(requireActivity()){
            binding.cashflowText.text = changeNominalToIDN(it)
        }
        db.growth.observe(requireActivity()){
            binding.growthText.text = it.toString()+"%"
        }

        return binding.root
    }
}