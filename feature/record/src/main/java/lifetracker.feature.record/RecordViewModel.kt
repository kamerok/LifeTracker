package lifetracker.feature.record

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import lifetracker.common.database.Data
import lifetracker.common.domain.SetPropertyUseCase
import org.threeten.bp.LocalDate


class RecordViewModel(
    private val date: LocalDate,
    private val data: Data,
    private val setPropertyUseCase: SetPropertyUseCase
) : ViewModel() {

    fun getState(): Flow<ViewState> =
        data.observeEntryByDate(date)
            .flatMapLatest { entry ->
                data.getEntryProperties(entry.id)
                    .map { data ->
                        val fields = data.map { value ->
                            RecordField(
                                id = value.id ?: "",
                                name = value.name ?: "",
                                isPositive = value.value
                            )
                        }.sortedBy { it.isPositive != null }
                        ViewState(fields)
                    }
            }

    fun onStateClick(id: String, isPositive: Boolean) {
        viewModelScope.launch {
            try {
                val entryId = data.getEntryByDate(date).id
                val entryProperty = data.getEntryProperty(entryId, id)
                val oldValue = entryProperty?.value
                val newValue = when {
                    oldValue == true && isPositive -> null
                    oldValue == false && !isPositive -> null
                    else -> isPositive
                }
                setPropertyUseCase.set(entryId, id, newValue)
                if (entryProperty != null) {
                    data.updateEntryPropertyValue(entryId, id, newValue)
                } else {
                    data.createEntryProperty(entryId, id, newValue)
                }
            } catch (e: Exception) {
                Log.e("TAG", "onStateClick: ", e)
            }
        }
    }

}
