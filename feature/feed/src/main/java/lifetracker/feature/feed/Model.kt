package lifetracker.feature.feed

import org.threeten.bp.LocalDate


sealed class FeedItem

sealed class TodayProgress : FeedItem() {
    data class Done(
        val date: LocalDate
    ) : TodayProgress()

    data class Progress(
        val date: LocalDate,
        val total: Int,
        val progress: Int
    ) : TodayProgress()
}

data class SkippedDay(
    val date: LocalDate
) : FeedItem()
