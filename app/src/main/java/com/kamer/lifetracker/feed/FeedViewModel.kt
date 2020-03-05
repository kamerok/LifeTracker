package com.kamer.lifetracker.feed

import androidx.lifecycle.ViewModel
import com.kamer.lifetracker.DataProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime


class FeedViewModel : ViewModel() {

    //TODO: skipped day
    fun getState(): Flow<ViewState> {
        val date = if (LocalTime.now().isBefore(LocalTime.of(6, 0))) {
            LocalDate.now().minusDays(1)
        } else {
            LocalDate.now()
        }
        return DataProvider.database.getEntryStatus(date)
            .map { (entry, total) ->
                val today = if (entry.count == total) {
                    TodayProgress.Done
                } else {
                    TodayProgress.Progress(
                        entry.date,
                        total.toInt(),
                        entry.count.toInt()
                    )
                }
                ViewState(listOf(today))
            }
    }

}
