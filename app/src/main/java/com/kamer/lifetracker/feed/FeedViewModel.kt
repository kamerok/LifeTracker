package com.kamer.lifetracker.feed

import androidx.lifecycle.ViewModel
import com.kamer.lifetracker.DataProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.threeten.bp.LocalDate


class FeedViewModel : ViewModel() {

    //TODO: skipped day
    fun getState(): Flow<ViewState> = DataProvider.database.getEntryStatus(LocalDate.now())
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
