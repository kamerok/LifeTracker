package com.kamer.lifetracker.records

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kamer.lifetracker.R
import com.kamer.lifetracker.record.RecordFragment
import kotlinx.android.synthetic.main.fragment_records.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class RecordsFragment : Fragment(R.layout.fragment_records) {

    private val viewModel by viewModels<RecordsViewModel>()
    private val adapter = MonthsAdapter {
        requireActivity().supportFragmentManager.commit {
            replace(
                R.id.fragmentContainer,
                RecordFragment::class.java,
                RecordFragment.createArgs(it.id)
            )
            addToBackStack(null)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.getState()
            .onEach { adapter.setData(it.months) }
            .catch { Log.e("TAG", "onViewCreated: ", it) }
            .launchIn(viewLifecycleOwner.lifecycle.coroutineScope)
    }

}
