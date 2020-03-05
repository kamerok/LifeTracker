package com.kamer.lifetracker.feed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import com.kamer.lifetracker.R
import com.kamer.lifetracker.databinding.FragmentFeedBinding
import com.kamer.lifetracker.record.RecordFragment
import com.kamer.lifetracker.viewBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class FeedFragment : Fragment(R.layout.fragment_feed) {

    private val viewModel: FeedViewModel by viewModels()

    private val binding by viewBinding(FragmentFeedBinding::bind)
    private val adapter = FeedAdapter { date ->
        requireActivity().supportFragmentManager.commit {
            replace(
                R.id.fragment_container,
                RecordFragment::class.java,
                RecordFragment.createArgs(date)
            )
            addToBackStack(null)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.adapter = adapter

        viewModel.getState()
            .onEach { adapter.setData(it.items) }
            .launchIn(viewLifecycleOwner.lifecycle.coroutineScope)
    }

}