package com.kamer.lifetracker.property

import androidx.lifecycle.ViewModel
import com.kamer.lifetracker.records.UiDay
import com.kamer.lifetracker.records.UiMonth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.TemporalAdjusters.firstDayOfYear
import org.threeten.bp.temporal.TemporalAdjusters.lastDayOfYear


class PropertyViewModel(private val id: String) : ViewModel() {

    fun getState(): Flow<ViewState> = flow {
        var start = LocalDate.now().with(firstDayOfYear())
        val end = LocalDate.now().with(lastDayOfYear())
        val totalDates: MutableList<LocalDate> = mutableListOf()
        while (!start.isAfter(end)) {
            totalDates.add(start)
            start = start.plusDays(1)
        }
        val months = totalDates.groupBy { it.month }
            .mapValues {
                val datesToDays = it.value
                val startDate = datesToDays.first()
                (1 until startDate.dayOfWeek.value).map { UiDay.DummyDay }
                    .plus(datesToDays.map {
                        UiDay.RealDay(
                            it.toString(),
                            it.dayOfMonth.toString(),
                            it == LocalDate.now(),
                            false
                        )
                    })
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
