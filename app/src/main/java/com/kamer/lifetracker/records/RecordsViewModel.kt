package com.kamer.lifetracker.records

import androidx.lifecycle.ViewModel
import com.kamer.lifetracker.DataProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class RecordsViewModel : ViewModel() {

    fun getState(): Flow<ViewState> = DataProvider.database.getEntries().map { entries ->
        val filledDates = entries.mapKeys { it.key.date }
        ViewState(filledDates)
    }

}
