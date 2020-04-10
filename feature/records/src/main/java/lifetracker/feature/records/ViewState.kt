package lifetracker.feature.records

import org.threeten.bp.LocalDate


data class ViewState(
    val filledDates: Map<LocalDate, Boolean>
)
