package com.kamer.lifetracker.properties

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import com.kamer.lifetracker.R
import kotlinx.android.synthetic.main.item_month.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class PropertiesFragment : Fragment(R.layout.fragment_properties) {

    private val viewModel: PropertiesViewModel by viewModels()

    private val adapter by lazy { PropertyAdapter {} }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.adapter = adapter

        viewModel.getState()
            .onEach { adapter.setData(it.properties) }
            .launchIn(viewLifecycleOwner.lifecycle.coroutineScope)
    }

}
