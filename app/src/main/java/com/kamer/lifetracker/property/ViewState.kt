package com.kamer.lifetracker.property

import org.threeten.bp.LocalDate


data class ViewState(
    val filledDates: Map<LocalDate, Boolean>
)
