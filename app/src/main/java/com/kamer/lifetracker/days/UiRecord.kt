package com.kamer.lifetracker.days

import org.threeten.bp.LocalDate


data class UiRecord(
    val id: String,
    val date: LocalDate,
    val state: String
)
