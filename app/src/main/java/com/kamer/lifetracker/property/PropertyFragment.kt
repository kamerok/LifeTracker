package com.kamer.lifetracker.property

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import com.kamer.lifetracker.R
import com.kamer.lifetracker.databinding.FragmentPropertyBinding
import com.kamer.lifetracker.record.RecordFragment
import com.kamer.lifetracker.viewBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class PropertyFragment : Fragment(R.layout.fragment_property) {

    private val viewModel: PropertyViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                PropertyViewModel(arguments!!["id"].toString()) as T
        }
    }

    private val binding by viewBinding(FragmentPropertyBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.calendarView.onDateClickListener { date ->
            requireActivity().supportFragmentManager.commit {
                replace(
                    R.id.fragment_container,
                    RecordFragment::class.java,
                    RecordFragment.createArgs(date)
                )
                addToBackStack(null)
            }
        }

        viewModel.getState()
            .onEach { binding.calendarView.setData(it.filledDates) }
            .launchIn(viewLifecycleOwner.lifecycle.coroutineScope)
    }

    companion object {
        fun createArgs(id: String) = bundleOf("id" to id)
    }

}
