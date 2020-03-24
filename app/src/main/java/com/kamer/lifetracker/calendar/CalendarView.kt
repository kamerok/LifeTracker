package com.kamer.lifetracker.calendar

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.TemporalAdjusters
import org.threeten.bp.temporal.TemporalAdjusters.firstDayOfYear


class CalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private var listener: ((LocalDate) -> Unit)? = null

    private val adapter = MonthsAdapter { listener?.invoke(it) }

    init {
        setAdapter(adapter)
        layoutManager = LinearLayoutManager(context)
    }

    fun onDateClickListener(listener: (LocalDate) -> Unit) {
        this.listener = listener
    }

    fun setData(data: Map<LocalDate, Boolean>) {
        var start = LocalDate.now().with(firstDayOfYear())
        val end = LocalDate.now().with(TemporalAdjusters.lastDayOfYear())
        val totalDates: MutableList<LocalDate> = mutableListOf()
        while (!start.isAfter(end)) {
            totalDates.add(start)
            start = start.plusDays(1)
        }
        val months = totalDates.groupBy { it.month }
            .mapValues { (month, days) ->
                val startDate = days.first()
                (1 until startDate.dayOfWeek.value).map { UiDay.DummyDay }
                    .plus(days.map {
                        UiDay.RealDay(
                            it,
                            it == LocalDate.now(),
                            data[it] == true
                        )
                    })
            }
            .map { (month, days) ->
                UiMonth(
                    name = month.name,
                    days = days
                )
            }
        adapter.setData(months)
    }

}
