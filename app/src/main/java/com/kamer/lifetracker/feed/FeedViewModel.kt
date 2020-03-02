package com.kamer.lifetracker.feed

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.threeten.bp.LocalDate


class FeedViewModel : ViewModel() {

    fun getState(): Flow<ViewState> = flowOf(
        ViewState(
            listOf(
                SkippedDay(LocalDate.now()),
                TodayProgress.Progress(LocalDate.now(), 10, 4),
                TodayProgress.Done
            )
        )
    )

}
