package com.kamer.lifetracker.record

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.navArgs
import com.kamer.lifetracker.R
import com.kamer.lifetracker.databinding.FragmentRecordBinding
import com.kamer.lifetracker.viewBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class RecordFragment : Fragment(R.layout.fragment_record) {

    private val args: RecordFragmentArgs by navArgs()
    private val viewModel by viewModels<RecordViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                    RecordViewModel(args.date) as T
            }
        }
    )

    private val binding by viewBinding(FragmentRecordBinding::bind)
    private val adapter by lazy { RecordAdapter(viewModel::onStateClick) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.adapter = adapter

        viewModel.getState()
            .onEach { adapter.setData(it.fields) }
            .launchIn(viewLifecycleOwner.lifecycle.coroutineScope)
    }
}
