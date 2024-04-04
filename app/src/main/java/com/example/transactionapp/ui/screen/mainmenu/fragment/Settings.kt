package com.example.transactionapp.ui.screen.mainmenu.fragment

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.R
import androidx.navigation.fragment.findNavController
import com.example.transactionapp.databinding.FragmentSettingsBinding
import com.example.transactionapp.helper.changeEmailSharedPref
import com.example.transactionapp.helper.changeTokenSharedPref
import com.example.transactionapp.ui.screen.login.LoginActivity
import com.example.transactionapp.ui.viewmodel.settings.SettingsViewModel
import com.example.transactionapp.ui.viewmodel.transaction.TransactionViewModel

class Settings : Fragment() {
    private val db: TransactionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout with binding
        val binding = FragmentSettingsBinding.inflate(layoutInflater)
        val bottomSheetExport = BottomSheetExport()
        val bottomSheetEmail = BottomSheetEmail()

        // Get view model
        val settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        // Profile handler
        binding.sivProfilePicture.setOnClickListener {
            this.findNavController().navigate(SettingsDirections.actionSettingsToEditProfile())
        }

        // Save handler
        binding.saveLayout.setOnClickListener {
            bottomSheetExport.show(parentFragmentManager, "bottomSheet")
        }

        // Share handler
        binding.shareLayout.setOnClickListener {
            bottomSheetEmail.show(parentFragmentManager, "bottomSheet")
        }

        // Randomize handler
        binding.switchRandomize.setOnCheckedChangeListener { compoundButton, b ->
            val intent = Intent("IsRandom")
            intent.putExtra("isRandom", b)
            requireActivity().sendBroadcast(intent)
        }

        binding.logoutLayout.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setMessage("Are you sure you want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    Toast.makeText(requireContext(), "Successfully Logout", Toast.LENGTH_SHORT)
                        .show()

                    val intent = Intent(requireActivity(), LoginActivity::class.java)
                    changeTokenSharedPref(requireActivity(), "")
                    changeEmailSharedPref(requireActivity(), "")
                    startActivity(intent)
                    requireActivity().finish()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.cancel()
                }
            val alert: AlertDialog = builder.create()
            alert.show()
        }

            // Observer
        settingsViewModel.name.observe(requireActivity()) {
            binding.tvName.text = it
        }

        db.isRandom.observe(requireActivity()){
            binding.switchRandomize.isChecked = it
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // Set the orientation to portrait
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}