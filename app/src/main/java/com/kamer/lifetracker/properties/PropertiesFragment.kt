package com.kamer.lifetracker.properties

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import com.kamer.lifetracker.R
import com.kamer.lifetracker.property.PropertyFragment
import kotlinx.android.synthetic.main.item_month.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class PropertiesFragment : Fragment(R.layout.fragment_properties) {

    private val viewModel: PropertiesViewModel by viewModels()

    private val adapter = PropertyAdapter { id ->
        requireActivity().supportFragmentManager.commit {
            replace(
                R.id.fragmentContainer,
                PropertyFragment::class.java,
                PropertyFragment.createArgs(id)
            )
            addToBackStack(null)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.adapter = adapter

        viewModel.getState()
            .onEach { adapter.setData(it.properties) }
            .launchIn(viewLifecycleOwner.lifecycle.coroutineScope)
    }

}
