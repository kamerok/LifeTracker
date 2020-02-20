package com.kamer.lifetracker.record

import androidx.lifecycle.ViewModel
import com.kamer.lifetracker.DataProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class RecordViewModel(private val id: String) : ViewModel() {

    fun getState(): Flow<ViewState> = flow {
        val data = DataProvider.getCachedData()
        val names = data.first().drop(1)
        val row = data.drop(1)[id.toInt()].drop(1)
        val fields = names.mapIndexed { index, value ->
            val name = value.toString()
            RecordField(
                id = index.toString(),
                name = name,
                isPositive = when (row.getOrNull(index)){
                    "Y" -> true
                    "N" -> false
                    else -> null
                }
            )
        }
        emit(ViewState(fields))
    }

}
