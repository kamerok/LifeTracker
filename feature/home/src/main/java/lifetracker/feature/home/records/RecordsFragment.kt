package lifetracker.feature.home.records

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import lifetracker.common.database.Data
import lifetracker.feature.home.R
import lifetracker.feature.home.databinding.FragmentRecordsBinding
import org.threeten.bp.LocalDate


class RecordsFragment(
    private val data: Data,
    private val onDateSelected: (LocalDate) -> Unit
) : Fragment(R.layout.fragment_records) {

    private val viewModel: RecordsViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                RecordsViewModel(data) as T
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(FragmentRecordsBinding.bind(view)) {
            requireActivity().setTitle(requireContext().applicationContext.applicationInfo.labelRes)
            calendarView.onDateClickListener(onDateSelected)

            viewModel.getState()
                .onEach { calendarView.setData(it.filledDates) }
                .catch { Log.e("TAG", "onViewCreated: ", it) }
                .launchIn(viewLifecycleOwner.lifecycle.coroutineScope)
        }
    }

}
