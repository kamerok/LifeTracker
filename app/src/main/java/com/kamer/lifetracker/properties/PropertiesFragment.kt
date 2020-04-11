package com.kamer.lifetracker.properties

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import com.kamer.lifetracker.R
import com.kamer.lifetracker.databinding.FragmentPropertiesBinding
import lifetracker.feature.property.PropertyFragment
import com.kamer.lifetracker.viewBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class PropertiesFragment : Fragment(R.layout.fragment_properties) {

    private val viewModel: PropertiesViewModel by viewModels()

    private val binding by viewBinding(FragmentPropertiesBinding::bind)
    private val adapter = PropertyAdapter { id ->
        findNavController().navigate(R.id.property, PropertyFragment.createArgs(id))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.adapter = adapter
        requireActivity().setTitle(R.string.app_name)

        viewModel.getState()
            .onEach { adapter.setData(it.properties) }
            .launchIn(viewLifecycleOwner.lifecycle.coroutineScope)
    }

}
