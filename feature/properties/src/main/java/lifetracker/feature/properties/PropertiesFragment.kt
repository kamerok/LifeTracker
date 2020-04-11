package lifetracker.feature.properties

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
import lifetracker.feature.properties.databinding.FragmentPropertiesBinding


class PropertiesFragment(
    private val data: Data,
    private val onPropertySelected: (String) -> Unit
) : Fragment(R.layout.fragment_properties) {

    private val viewModel: PropertiesViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                PropertiesViewModel(data) as T
        }
    }

    private val adapter by lazy { PropertyAdapter(onPropertySelected) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(FragmentPropertiesBinding.bind(view)) {
            recyclerView.adapter = adapter
            requireActivity().setTitle(requireContext().applicationInfo.labelRes)

            viewModel.getState()
                .onEach { adapter.setData(it.properties) }
                .launchIn(viewLifecycleOwner.lifecycle.coroutineScope)
        }
    }

}
