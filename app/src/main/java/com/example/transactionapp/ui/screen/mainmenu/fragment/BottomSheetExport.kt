package com.example.transactionapp.ui.screen.mainmenu.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.activityViewModels
import com.example.transactionapp.R
import com.example.transactionapp.databinding.FragmentBottomSheetBinding
import com.example.transactionapp.domain.db.model.Transaction
import com.example.transactionapp.ui.viewmodel.transaction.TransactionViewModel
import com.example.transactionapp.utils.saveExcelFile
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BottomSheetExport : BottomSheetDialogFragment() {
    private val db: TransactionViewModel by activityViewModels()
    private lateinit var transactionList: MutableList<Transaction>
    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBottomSheetBinding.inflate(layoutInflater)

        binding.tvSaveTo.text = "Save to local as"

        transactionList = mutableListOf()
        db.transaction.observe(requireActivity()) {
            transactionList.addAll(it)
        }

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
                saveFile(it)
            }
            dismiss()
        }

        return binding.root
    }

    private fun saveFile(format: String) {
        saveExcelFile(transactionList, format, requireContext())
        Toast.makeText(
            requireContext(),
            "File saved to ${requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString()}",
            Toast.LENGTH_SHORT
        ).show()
        dismiss()
    }

    override fun onStart() {
        super.onStart()
        db.getTransactions("desc")
    }

}