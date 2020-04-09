package lifetracker.feature.home.records

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import lifetracker.common.database.Data


class RecordsViewModel(private val data: Data) : ViewModel() {

    fun getState(): Flow<ViewState> = data.getEntries().map { entries ->
        val filledDates = entries.mapKeys { it.key.date }
        ViewState(filledDates)
    }

}
