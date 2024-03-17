package com.example.transactionapp.ui.screen.mainmenu.fragment

import android.os.Bundle
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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Transaction : Fragment() {
    private val db: TransactionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentTransactionBinding.inflate(layoutInflater)
        val recyclerViewTransaction: RecyclerView = binding.transactionHistoryRecyclerView
        recyclerViewTransaction.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        db.dateAll.observe(requireActivity()){
            val transactionAdapter = TransactionAdapter(it)
            recyclerViewTransaction.adapter = transactionAdapter
        }
        return binding.root
    }
}

//        db.insertTransaction(com.example.transactionapp.domain.db.model.Transaction(
//            title = "kotnol",
//            category = "kotnol",
//            nominal = 10000,
//            location = "kotlnot",
//            createdAt = Date()
//        ))

//        binding.transactionText.setOnClickListener {
//            db.insertTransaction(com.example.transactionapp.domain.db.model.Transaction(
//                title = "kontol",
//                category = "kontol",
//                nominal = 10000,
//                location = "tolol",
//                createdAt = Date()
//            ))
//            db.dateAll.observe(requireActivity()){
//                for (i in it){
//                    Log.d("Transaction", i.toString())
//                }
//            }
//        }