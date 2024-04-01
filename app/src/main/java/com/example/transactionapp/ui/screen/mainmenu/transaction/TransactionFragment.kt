package com.example.transactionapp.ui.screen.mainmenu.transaction

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.transactionapp.R
import com.example.transactionapp.databinding.FragmentTransactionBinding
import com.example.transactionapp.utils.changeNominalToIDN
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

@AndroidEntryPoint
class TransactionFragment : Fragment() {
    private val transactionViewModel: TransactionViewModel by activityViewModels()
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the nav controller
        navController = Navigation.findNavController(view)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Bind layout using layout binding
        val binding = FragmentTransactionBinding.inflate(layoutInflater)

        // Observe transaction list
        transactionViewModel.dateAll.observe(requireActivity()) { transactionList ->
            val transactionAdapter = TransactionAdapter(transactionList, this::onTransactionClicked)
            val recyclerViewTransaction: RecyclerView = binding.transactionHistoryRecyclerView
            recyclerViewTransaction.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            recyclerViewTransaction.adapter = transactionAdapter
        }

        // Observe total balance
        transactionViewModel.balance.observe(requireActivity()){
            binding.balanceText.text = if (it<0) "- "+changeNominalToIDN(abs(it)) else changeNominalToIDN(it)
        }

        // Observe cashflow
        transactionViewModel.cashFlow.observe(requireActivity()){
            binding.cashflowText.text = changeNominalToIDN(it)
            binding.cashflowText.setTextColor(if (it<0) ContextCompat.getColor(requireContext(),R.color.R3) else ContextCompat.getColor(requireContext(), R.color.G3))
        }

        // Observe growth
        transactionViewModel.growth.observe(requireActivity()){
            binding.growthText.text = it.toString()+"%"
            if (it>0) binding.growthText.text = "+"+binding.growthText.text
            binding.growthText.setTextColor(if (it<0) ContextCompat.getColor(requireContext(),R.color.R3) else ContextCompat.getColor(requireContext(), R.color.G3))
        }

        // Observe navigate to transaction detail clicked
        transactionViewModel.navigateToTransactionDetail.observe(requireActivity(), Observer { transactionId ->
            transactionId?.let {
                this.findNavController().navigate(TransactionFragmentDirections.actionTransactionFragmentToTransactionDetailsFragment(transactionId))
                transactionViewModel.onTransactionDetailNavigated()
            }
        })

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        // Set the orientation to portrait
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    fun onTransactionClicked(transactionId: Int) {
        transactionViewModel.onTransactionClicked(transactionId)
    }
}