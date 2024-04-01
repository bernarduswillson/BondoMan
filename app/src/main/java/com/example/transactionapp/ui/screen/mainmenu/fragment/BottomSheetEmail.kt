package com.example.transactionapp.ui.screen.mainmenu.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.transactionapp.R
import com.example.transactionapp.databinding.FragmentBottomSheetBinding
import com.example.transactionapp.domain.db.model.Transaction
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

        binding.tvSaveTo.text = "Share to email as"
        binding.icon.setImageResource(
            R.drawable.share_ic
        )

        transactionList = mutableListOf()
        db.transaction.observe(requireActivity()){
            transactionList.addAll(it)
        }

        //TODO: change email to your email
        binding.xlsxButton.setOnClickListener {
            binding.radioGroup.check(R.id.xlsxButton)
            binding.xlsxButton.background = resources.getDrawable(R.drawable.rounded_retake_button)
            binding.xlsxButton.setTextColor(resources.getColor(R.color.G5))

            binding.xlsButton.background = resources.getDrawable(R.drawable.rounded_outline_button)
            binding.xlsButton.setTextColor(resources.getColor(R.color.N5))
        }

        binding.xlsButton.setOnClickListener {
            binding.radioGroup.check(R.id.xlsButton)
            binding.xlsButton.background = resources.getDrawable(R.drawable.rounded_retake_button)
            binding.xlsButton.setTextColor(resources.getColor(R.color.G5))

            binding.xlsxButton.background = resources.getDrawable(R.drawable.rounded_outline_button)
            binding.xlsxButton.setTextColor(resources.getColor(R.color.N5))
        }

        binding.saveButton.setOnClickListener {
            val selectedFormat = when (binding.radioGroup.checkedRadioButtonId) {
                R.id.xlsxButton -> "xlsx"
                R.id.xlsButton -> "xls"
                else -> null
            }
            selectedFormat?.let {
                sendExcelToEmail(transactionList, requireContext(), it, "fahrianafdholi077@gmail.com")
            }
            dismiss()
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        db.getTransactions("desc")
    }

}