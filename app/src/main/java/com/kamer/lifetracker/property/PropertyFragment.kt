package com.kamer.lifetracker.property

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.kamer.lifetracker.R
import com.kamer.lifetracker.databinding.FragmentPropertyBinding
import com.kamer.lifetracker.viewBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class PropertyFragment : Fragment(R.layout.fragment_property) {

    private val args: PropertyFragmentArgs by navArgs()
    private val viewModel: PropertyViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                PropertyViewModel(args.id) as T
        }
    }

    private val binding by viewBinding(FragmentPropertyBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.calendarView.onDateClickListener { date ->
            findNavController().navigate(PropertyFragmentDirections.record(date))
        }

        viewModel.getState()
            .onEach {
                requireActivity().title = it.name
                binding.calendarView.setData(it.filledDates)
            }
            .launchIn(viewLifecycleOwner.lifecycle.coroutineScope)
    }

}
