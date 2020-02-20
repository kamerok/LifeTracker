package com.kamer.lifetracker.days

import androidx.lifecycle.ViewModel
import com.kamer.lifetracker.DataProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter


class RecordsViewModel : ViewModel() {

    fun getState(): Flow<ViewState> = flow {
        val data = withContext(Dispatchers.Default) { DataProvider.getData() }
        val parsed = data.drop(1).mapIndexed { index, list ->
            val columns = list.map { it.toString() }
            UiRecord(
                id = index.toString(),
                date = LocalDate.parse(columns.first(), DateTimeFormatter.ofPattern("DD/mm/yyyy")),
                state = "${columns.size - 1} of ${data.first().size - 1}"
            )
        }
        emit(ViewState(parsed))
    }

}
