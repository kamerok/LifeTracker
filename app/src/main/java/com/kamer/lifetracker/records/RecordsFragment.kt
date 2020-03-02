package com.kamer.lifetracker.records

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import com.kamer.lifetracker.R
import com.kamer.lifetracker.databinding.FragmentRecordsBinding
import com.kamer.lifetracker.record.RecordFragment
import com.kamer.lifetracker.viewBinding
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class RecordsFragment : Fragment(R.layout.fragment_records) {

    private val viewModel by viewModels<RecordsViewModel>()

    private val binding by viewBinding(FragmentRecordsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            .catch { Log.e("TAG", "onViewCreated: ", it) }
            .launchIn(viewLifecycleOwner.lifecycle.coroutineScope)
    }

}
