package com.kamer.lifetracker.records

import org.threeten.bp.LocalDate


data class UiRecord(
    val id: String,
    val date: LocalDate,
    val state: String
)
