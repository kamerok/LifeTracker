package lifetracker.feature.feed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import lifetracker.common.database.Data
import lifetracker.feature.feed.databinding.FragmentFeedBinding
import org.threeten.bp.LocalDate


class FeedFragment(
    private val data: Data,
    private val onDateSelected: (LocalDate) -> Unit
) : Fragment(R.layout.fragment_feed) {

    private val viewModel: FeedViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                FeedViewModel(data = data) as T
        }
    }

    private val adapter by lazy { FeedAdapter(onDateSelected) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(FragmentFeedBinding.bind(view)) {
            requireActivity().setTitle(requireContext().applicationInfo.labelRes)
            recyclerView.adapter = adapter

            viewModel.getState()
                .onEach { adapter.setData(it.items) }
                .launchIn(viewLifecycleOwner.lifecycle.coroutineScope)
        }
    }

}
