package com.kamer.lifetracker.records

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kamer.lifetracker.R
import kotlinx.android.synthetic.main.fragment_records.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class RecordsFragment : Fragment(R.layout.fragment_records) {

    private val viewModel by viewModels<RecordsViewModel>()
    private val adapter = RecordsAdapter {
        Toast.makeText(requireContext(), it.id, Toast.LENGTH_SHORT).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.getState()
            .onEach { adapter.setData(it.records) }
            .launchIn(viewLifecycleOwner.lifecycle.coroutineScope)
    }

}
