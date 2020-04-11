package lifetracker.feature.record

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
import lifetracker.common.domain.SetPropertyUseCase
import lifetracker.common.domain.SpreadsheetService
import lifetracker.feature.record.databinding.FragmentRecordBinding
import org.threeten.bp.LocalDate


class RecordFragment(
    private val data: Data,
    private val spreadsheetService: SpreadsheetService
) : Fragment(R.layout.fragment_record) {

    private val date by lazy { requireArguments().getSerializable(ARG_DATE) as LocalDate }

    private val viewModel by viewModels<RecordViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                    RecordViewModel(
                        date = date,
                        data = data,
                        setPropertyUseCase = SetPropertyUseCase(data, spreadsheetService)
                    ) as T
            }
        }
    )

    private val adapter by lazy { RecordAdapter(viewModel::onStateClick) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(FragmentRecordBinding.bind(view)) {
            requireActivity().title = date.toString()
            recyclerView.adapter = adapter

            viewModel.getState()
                .onEach { adapter.setData(it.fields) }
                .launchIn(viewLifecycleOwner.lifecycle.coroutineScope)
        }
    }

    companion object {
        private const val ARG_DATE = "date"

        fun createArgs(date: LocalDate): Bundle = bundleOf(ARG_DATE to date)
    }
}
