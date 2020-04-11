package com.kamer.lifetracker.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.coroutineScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.kamer.lifetracker.DataProvider
import com.kamer.lifetracker.R
import com.kamer.lifetracker.databinding.FragmentHomeBinding
import com.kamer.lifetracker.viewBinding
import kotlinx.coroutines.launch
import lifetracker.feature.record.RecordFragment
import lifetracker.feature.records.RecordsFragment


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
        childFragmentManager.fragmentFactory = object : FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String): Fragment =
                when (className) {
                    RecordFragment::class.qualifiedName -> RecordFragment(
                        DataProvider.database,
                        DataProvider.spreadSheetService
                    )
                    RecordsFragment::class.qualifiedName -> RecordsFragment(DataProvider.database) {
                        binding.fragmentContainer.findNavController()
                            .navigate(R.id.record, RecordFragment.createArgs(it))
                    }
                    else -> super.instantiate(classLoader, className)
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
