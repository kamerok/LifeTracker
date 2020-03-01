package com.kamer.lifetracker.calendar

import org.threeten.bp.LocalDate


data class UiMonth(
    val name: String,
    val days: List<UiDay>
)

sealed class UiDay {
    object DummyDay : UiDay()

    data class RealDay(
        val date: LocalDate,
        val isToday: Boolean,
        val isFilled: Boolean
    ) : UiDay()
}

