package com.example.transactionapp.ui.screen.mainmenu.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.transactionapp.R
import com.example.transactionapp.databinding.FragmentEditProfileBinding
import com.example.transactionapp.ui.viewmodel.profile.ProfileViewModel

class EditProfile : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using binding
        val binding = FragmentEditProfileBinding.inflate(layoutInflater)

        // Get view model
        val editProfileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        // Toggle twibbon handler
        binding.btnToggleTwibbon.setOnClickListener {
            editProfileViewModel.toggleTwibbon()
        }

        // Shutter button handler
        binding.btnCameraButton.setOnClickListener {
            editProfileViewModel.onTakePicture()
        }

        // Twibbon state observer
        editProfileViewModel.isTwibbonActive.observe(requireActivity()) {
            if (it) {
                binding.btnToggleTwibbon.setText(R.string.btn_enable_twibbon)
                binding.ivTwibbon.visibility = View.VISIBLE
            } else {
                binding.btnToggleTwibbon.setText(R.string.btn_disable_twibbon)
                binding.ivTwibbon.visibility = View.INVISIBLE
            }
        }

        return binding.root
    }
}