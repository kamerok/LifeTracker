package lifetracker.feature.property

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import lifetracker.common.database.Data
import lifetracker.feature.property.databinding.FragmentPropertyBinding
import org.threeten.bp.LocalDate


class PropertyFragment(
    private val data: Data,
    private val onDateSelected: (LocalDate) -> Unit
) : Fragment(R.layout.fragment_property) {

    private val viewModel: PropertyViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                PropertyViewModel(
                    id = requireArguments().getString(ARG_ID)!!,
                    database = data
                ) as T
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(FragmentPropertyBinding.bind(view)) {
            calendarView.onDateClickListener(onDateSelected)

            viewModel.getState()
                .onEach {
                    requireActivity().title = it.name
                    calendarView.setData(it.filledDates)
                }
                .launchIn(viewLifecycleOwner.lifecycle.coroutineScope)
        }
    }

    companion object {
        private const val ARG_ID = "id"

        fun createArgs(id: String) = bundleOf(ARG_ID to id)
    }

}
