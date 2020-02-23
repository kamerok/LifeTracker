package com.kamer.lifetracker.record

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kamer.lifetracker.R
import kotlinx.android.synthetic.main.fragment_record.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class RecordFragment : Fragment(R.layout.fragment_record) {

    private val viewModel by viewModels<RecordViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                    RecordViewModel(arguments!!.getString("id")!!) as T
            }
        }
    )
    private val adapter by lazy { RecordAdapter(viewModel::onStateClick) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.getState()
            .onEach { adapter.setData(it.fields) }
            .launchIn(viewLifecycleOwner.lifecycle.coroutineScope)
    }

    companion object {
        fun createArgs(id: String): Bundle = bundleOf(
            "id" to id
        )
    }
}
