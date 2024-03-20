package com.example.transactionapp.ui.screen.mainmenu.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.transactionapp.R
import com.example.transactionapp.databinding.FragmentSettingsBinding
import com.example.transactionapp.databinding.FragmentTransactionBinding

class Settings : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSettingsBinding.inflate(layoutInflater)
        val bottomSheet = BottomSheet()

        binding.saveLayout.setOnClickListener {
            bottomSheet.show(parentFragmentManager, "bottomSheet")
        }


        return binding.root
    }
}