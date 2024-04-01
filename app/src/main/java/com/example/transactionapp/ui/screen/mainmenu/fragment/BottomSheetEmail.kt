package com.example.transactionapp.ui.screen.mainmenu.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.transactionapp.databinding.FragmentBottomSheetBinding
import com.example.transactionapp.domain.db.model.Transaction
import com.example.transactionapp.helper.getEmailSharedPref
import com.example.transactionapp.ui.viewmodel.transaction.TransactionViewModel
import com.example.transactionapp.utils.sendExcelToEmail
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetEmail: BottomSheetDialogFragment() {
    private val db: TransactionViewModel by activityViewModels()
    private lateinit var transactionList: MutableList<Transaction>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBottomSheetBinding.inflate(layoutInflater)
        transactionList = mutableListOf()
        db.transaction.observe(requireActivity()){
            transactionList.clear()
            transactionList.addAll(it)
        }

        val email = getEmailSharedPref(requireContext())
        binding.xlsxButton.setOnClickListener {
            sendExcelToEmail(transactionList, requireContext(), "xlsx", email)
            dismiss()
        }

        binding.xlsButton.setOnClickListener {
            sendExcelToEmail(transactionList, requireContext(), "xls", email)
            dismiss()
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        db.getTransactions("desc")
    }

}