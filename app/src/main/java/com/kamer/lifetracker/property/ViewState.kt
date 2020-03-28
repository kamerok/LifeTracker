package com.kamer.lifetracker.property

import org.threeten.bp.LocalDate


data class ViewState(
    val name: String,
    val filledDates: Map<LocalDate, Boolean>
)
