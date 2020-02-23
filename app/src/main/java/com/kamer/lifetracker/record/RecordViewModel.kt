package com.kamer.lifetracker.record

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kamer.lifetracker.DataProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch


class RecordViewModel(private val id: String) : ViewModel() {

    private val channel: ConflatedBroadcastChannel<ViewState> = ConflatedBroadcastChannel()

    init {
        viewModelScope.launch {
            val data = DataProvider.getCachedData()
            val names = data.first().drop(1)
            val row = data.drop(1)[id.toInt()].drop(1)
            val fields = names
                .mapIndexed { index, value ->
                    val name = value.toString()
                    RecordField(
                        id = index.toString(),
                        name = name,
                        isPositive = when (row.getOrNull(index)) {
                            "Y" -> true
                            "N" -> false
                            else -> null
                        }
                    )
                }
                .sortedBy { it.isPositive != null }
            channel.send(ViewState(fields))
        }
    }

    fun getState(): Flow<ViewState> = channel.asFlow()

    fun onStateClick(id: String, isPositive: Boolean) {
        val currentState = channel.value
        channel.offer(
            ViewState(
                currentState.fields.map { recordField ->
                    if (recordField.id == id) {
                        val newValue = when {
                            recordField.isPositive == true && isPositive -> null
                            recordField.isPositive == false && !isPositive -> null
                            else -> isPositive
                        }
                        GlobalScope.launch {
                            try {
                                DataProvider.updateData(this@RecordViewModel.id, id, newValue)
                            } catch (e: Exception) {
                                Log.e("TAG", "onStateClick: ", e)
                            }
                        }
                        recordField.copy(isPositive = newValue)
                    } else {
                        recordField
                    }
                }.sortedBy { it.isPositive != null }
            )
        )
    }

}
