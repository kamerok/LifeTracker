package com.kamer.lifetracker.records

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kamer.lifetracker.DataProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class RecordsViewModel : ViewModel() {

    init {
        viewModelScope.launch {
            try {
                DataProvider.updateData()
            } catch (e: Exception) {
                Log.e("TAG", "omg: ", e)
            }
        }
    }

    fun getState(): Flow<ViewState> = DataProvider.database.getEntries().map { entries ->
        val filledDates = entries.mapKeys { it.key.date }
        ViewState(filledDates)
    }

}
