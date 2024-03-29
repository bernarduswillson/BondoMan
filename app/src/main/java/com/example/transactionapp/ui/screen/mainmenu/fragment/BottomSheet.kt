package com.example.transactionapp.ui.screen.mainmenu.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.transactionapp.databinding.FragmentBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BottomSheet : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentBottomSheetBinding.inflate(layoutInflater)

        binding.xlsxButton.setOnClickListener {
            // TODO: Create XLSX File
        }

        binding.xlsButton.setOnClickListener {
            // TODO: Create XLS File
        }

        return binding.root
    }

}