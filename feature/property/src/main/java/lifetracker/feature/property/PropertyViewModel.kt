package lifetracker.feature.property

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import lifetracker.common.database.Data


class PropertyViewModel(
    private val id: String,
    private val database: Data
) : ViewModel() {

    fun getState(): Flow<ViewState> =
        flow { emit(database.getProperty(id)) }
            .combine(database.getPropertyEntries(id)) { property, propertyEntries ->
                val filledDates = propertyEntries
                    .map { it.date to it.value }
                    .toMap()
                    .filter { it.value == true }
                    .mapValues { it.value!! }
                ViewState(property.name, filledDates)
            }

}
