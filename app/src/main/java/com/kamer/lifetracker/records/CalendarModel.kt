package com.kamer.lifetracker.records


data class UiMonth(
    val name: String,
    val days: List<UiDay>
)

sealed class UiDay {
    object DummyDay : UiDay()

    data class RealDay(
        val id: String,
        val text: String,
        val isToday: Boolean,
        val isFilled: Boolean
    ) : UiDay()
}

