package com.example.transactionapp.ui.screen.mainmenu.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.transactionapp.databinding.FragmentTransactionFormBinding

class TransactionForm : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentTransactionFormBinding.inflate(inflater)

        return binding.root
    }
}