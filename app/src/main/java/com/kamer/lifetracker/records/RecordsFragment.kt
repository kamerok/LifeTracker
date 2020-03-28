package com.kamer.lifetracker.records

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import com.kamer.lifetracker.R
import com.kamer.lifetracker.databinding.FragmentRecordsBinding
import com.kamer.lifetracker.viewBinding
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class RecordsFragment : Fragment(R.layout.fragment_records) {

    private val viewModel: RecordsViewModel by viewModels()

    private val binding by viewBinding(FragmentRecordsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().setTitle(R.string.app_name)
        binding.calendarView.onDateClickListener { date ->
            findNavController().navigate(RecordsFragmentDirections.record(date))
        }

        viewModel.getState()
            .onEach { binding.calendarView.setData(it.filledDates) }
            .catch { Log.e("TAG", "onViewCreated: ", it) }
            .launchIn(viewLifecycleOwner.lifecycle.coroutineScope)
    }

}
