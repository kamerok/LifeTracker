package com.kamer.lifetracker.records

import androidx.lifecycle.ViewModel
import com.kamer.lifetracker.DataProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter


class RecordsViewModel : ViewModel() {

    fun getState(): Flow<ViewState> = flow {
        val data = DataProvider.getData()
        val dateToUiDate = data.drop(1).mapIndexed { index, list ->
            val columns = list.map { it.toString() }
            val date = LocalDate.parse(columns.first(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            val isFilled = (columns.filter { it != "" }.size - 1) == (data.first().size - 1)
            date to UiDay.RealDay(
                id = index.toString(),
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
        emit(ViewState(months))
    }

}
