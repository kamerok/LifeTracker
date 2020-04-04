package com.kamer.lifetracker.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.kamer.lifetracker.DataProvider
import com.kamer.lifetracker.R
import com.kamer.lifetracker.databinding.FragmentHomeBinding
import com.kamer.lifetracker.viewBinding
import kotlinx.coroutines.launch


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
        lifecycle.coroutineScope.launch {
            try {
                DataProvider.updateData()
            } catch (e: Exception) {
                Log.e("TAG", "omg: ", e)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.bottomNavigationView.setupWithNavController(
            binding.fragmentContainer.findNavController()
        )
    }

}
