package lifetracker.feature.feed

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import lifetracker.common.database.Data
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime


class FeedViewModel(private val data: Data) : ViewModel() {

    fun getState(): Flow<ViewState> {
        val currentEntryDate = if (LocalTime.now().isBefore(LocalTime.of(6, 0))) {
            LocalDate.now().minusDays(1)
        } else {
            LocalDate.now()
        }
        val previousProgress = data.getEntryStatus(currentEntryDate.minusDays(1))
            .map { (entry, total) ->
                if (entry.count < total) {
                    SkippedDay(entry.date)
                } else {
                    null
                }
            }
        val currentProgress = data.getEntryStatus(currentEntryDate)
            .map { (entry, total) ->
                if (entry.count == total) {
                    TodayProgress.Done(entry.date)
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
