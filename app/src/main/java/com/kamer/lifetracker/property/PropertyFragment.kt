package com.kamer.lifetracker.property

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import com.kamer.lifetracker.R
import com.kamer.lifetracker.records.MonthsAdapter
import kotlinx.android.synthetic.main.item_month.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class PropertyFragment : Fragment(R.layout.fragment_property) {

    private val viewModel: PropertyViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                PropertyViewModel(arguments!!["id"].toString()) as T
        }
    }
    private val adapter = MonthsAdapter {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.adapter = adapter
        viewModel.getState()
            .onEach { adapter.setData(it.months) }
            .launchIn(viewLifecycleOwner.lifecycle.coroutineScope)
    }

    companion object {
        fun createArgs(id: String) = bundleOf("id" to id)
    }

}
