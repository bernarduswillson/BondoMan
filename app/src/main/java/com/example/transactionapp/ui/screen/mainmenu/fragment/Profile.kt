package com.example.transactionapp.ui.screen.mainmenu.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.transactionapp.R
import com.example.transactionapp.databinding.FragmentProfileBinding
import com.example.transactionapp.ui.viewmodel.profile.ProfileViewModel

class Profile : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout using layout binding
        val binding = FragmentProfileBinding.inflate(layoutInflater)

        // Get the view model
        val profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        // Observe
        profileViewModel.name.observe(requireActivity()) {
            binding.tvName.text = it
            Log.d("Name", it)
        }

        return binding.root
    }
}