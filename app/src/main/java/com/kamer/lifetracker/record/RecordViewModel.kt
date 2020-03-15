package com.kamer.lifetracker.record

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kamer.lifetracker.DataProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate


class RecordViewModel(private val date: LocalDate) : ViewModel() {

    fun getState(): Flow<ViewState> =
        DataProvider.database.observeEntryByDate(date)
            .flatMapLatest { entry ->
                DataProvider.database.getEntryProperties(entry.id)
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
                val entryId = DataProvider.database.getEntryByDate(date).id
                val entryProperty =
                    DataProvider.database.getEntryProperty(entryId, id)
                val oldValue = entryProperty?.value
                val newValue = when {
                    oldValue == true && isPositive -> null
                    oldValue == false && !isPositive -> null
                    else -> isPositive
                }
                DataProvider.updateData(entryId, id, newValue)
                if (entryProperty != null) {
                    DataProvider.database.updateEntryPropertyValue(entryId, id, newValue)
                } else {
                    DataProvider.database.createEntryProperty(entryId, id, newValue)
                }
            } catch (e: Exception) {
                Log.e("TAG", "onStateClick: ", e)
            }
        }
    }

}
