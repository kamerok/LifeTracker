package com.kamer.lifetracker.record

import android.util.Log
import androidx.lifecycle.ViewModel
import com.kamer.lifetracker.DataProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class RecordViewModel(private val id: String) : ViewModel() {

    fun getState(): Flow<ViewState> =
        DataProvider.database.getEntryProperties(id)
            .map { data ->
                val fields = data.map { value ->
                    RecordField(
                        id = value.id ?: "",
                        name = value.name ?: "",
                        isPositive = value.value
                    )
                }.sortedBy { it.isPositive != null }
                ViewState(fields)
            }

    fun onStateClick(id: String, isPositive: Boolean) {
        GlobalScope.launch {
            try {
                val entryProperty =
                    DataProvider.database.getEntryProperty(this@RecordViewModel.id, id)
                val newValue = when {
                    entryProperty.value == true && isPositive -> null
                    entryProperty.value == false && !isPositive -> null
                    else -> isPositive
                }
                DataProvider.updateData(this@RecordViewModel.id, id, newValue)
                DataProvider.database.updateEntryPropertyValue(this@RecordViewModel.id, id, newValue)
            } catch (e: Exception) {
                Log.e("TAG", "onStateClick: ", e)
            }
        }
    }

}
