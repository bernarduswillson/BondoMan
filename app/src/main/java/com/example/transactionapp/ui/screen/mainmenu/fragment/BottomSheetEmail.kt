package com.example.transactionapp.ui.screen.mainmenu.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.transactionapp.databinding.FragmentBottomSheetBinding
import com.example.transactionapp.domain.db.model.Transaction
import com.example.transactionapp.ui.screen.mainmenu.transaction.TransactionViewModel
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
            transactionList.addAll(it)
        }

        //TODO: change email to your email
        binding.xlsxButton.setOnClickListener {
            sendExcelToEmail(transactionList, requireContext(), "xlsx", "fahrianafdholi077@gmail.com")
            dismiss()
        }

        binding.xlsButton.setOnClickListener {
            sendExcelToEmail(transactionList, requireContext(), "xls", "fahrianafdholi077@gmail.com")
            dismiss()
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        db.getTransactions("desc")
    }

}