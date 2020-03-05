package com.kamer.lifetracker.feed

import androidx.lifecycle.ViewModel
import com.kamer.lifetracker.DataProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime


class FeedViewModel : ViewModel() {

    //TODO: skipped day
    fun getState(): Flow<ViewState> {
        val currentEntryDate = if (LocalTime.now().isBefore(LocalTime.of(6, 0))) {
            LocalDate.now().minusDays(1)
        } else {
            LocalDate.now()
        }
        val previousProgress = DataProvider.database.getEntryStatus(currentEntryDate.minusDays(1))
            .map { (entry, total) ->
                if (entry.count < total) {
                    SkippedDay(entry.date)
                } else {
                    null
                }
            }
        val currentProgress = DataProvider.database.getEntryStatus(currentEntryDate)
            .map { (entry, total) ->
                if (entry.count == total) {
                    TodayProgress.Done
                } else {
                    TodayProgress.Progress(
                        entry.date,
                        total.toInt(),
                        entry.count.toInt()
                    )
                }
            }
        return previousProgress.combine(currentProgress) { previous, current ->
            ViewState(listOfNotNull(previous, current))
        }
    }

}
