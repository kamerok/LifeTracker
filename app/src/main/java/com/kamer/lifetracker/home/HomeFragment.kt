package com.kamer.lifetracker.home

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.kamer.lifetracker.NaveGraphDirections
import com.kamer.lifetracker.R
import com.kamer.lifetracker.databinding.FragmentHomeBinding
import com.kamer.lifetracker.viewBinding


class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (!binding.fragmentContainer.findNavController().popBackStack()) {
                isEnabled = false
                requireActivity().onBackPressed()
                isEnabled = true
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //without this nav controller is null
        //may lead to crash if executes after onDestroyView
        binding.fragmentContainer.post {
            binding.bottomNavigationView.setupWithNavController(binding.fragmentContainer.findNavController())
        }

        if (GoogleSignIn.getLastSignedInAccount(requireContext()) == null) {
            findNavController().navigate(NaveGraphDirections.login())
        }
    }

}
