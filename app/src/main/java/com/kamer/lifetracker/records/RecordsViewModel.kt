package com.kamer.lifetracker.records

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kamer.lifetracker.DataProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate


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
        val dateToUiDate = entries.map { (entry, isFilled) ->
            val date = entry.date
            date to UiDay.RealDay(
                id = entry.id,
                text = date.dayOfMonth.toString(),
                isToday = date == LocalDate.now(),
                isFilled = isFilled
            )
        }.also { println(it.map { it.first }) }
        val months = dateToUiDate.groupBy { it.first.month }
            .mapValues {
                val datesToDays = it.value
                val startDate = datesToDays.first().first
                (1 until startDate.dayOfWeek.value).map { UiDay.DummyDay }
                    .plus(datesToDays.map { it.second })
            }
            .also { println(it.keys) }
            .map { (month, days) ->
                UiMonth(
                    name = month.name,
                    days = days
                )
            }
        ViewState(months)
    }

}
