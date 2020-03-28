package com.kamer.lifetracker.property

import androidx.lifecycle.ViewModel
import com.kamer.lifetracker.DataProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import lifetracker.database.Data


class PropertyViewModel(
    private val id: String,
    private val database: Data = DataProvider.database
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
