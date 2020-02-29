package com.kamer.lifetracker.properties

import androidx.lifecycle.ViewModel
import com.kamer.lifetracker.DataProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class PropertiesViewModel : ViewModel() {

    fun getState(): Flow<ViewState> = DataProvider.database.getProperties().map { list ->
        ViewState(list.map { UiProperty(it.id, it.name) })
    }

}
