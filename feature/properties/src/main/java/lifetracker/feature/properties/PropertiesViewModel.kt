package lifetracker.feature.properties

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import lifetracker.common.database.Data


class PropertiesViewModel(private val data: Data) : ViewModel() {

    fun getState(): Flow<ViewState> = data.getProperties().map { list ->
        ViewState(
            list
                .sortedBy { it.isArchived }
                .map { UiProperty(it.id, it.name, it.isArchived) }
        )
    }

}
