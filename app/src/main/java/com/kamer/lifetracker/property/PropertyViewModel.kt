package com.kamer.lifetracker.property

import androidx.lifecycle.ViewModel
import com.kamer.lifetracker.DataProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class PropertyViewModel(private val id: String) : ViewModel() {

    fun getState(): Flow<ViewState> = DataProvider.database.getPropertyEntries(id)
        .map { propertyEntries ->
            val filledDates = propertyEntries
                .map { it.date to it.value }
                .toMap()
                .filter { it.value == true }
                .mapValues { it.value!! }
            ViewState(filledDates)
        }

}
