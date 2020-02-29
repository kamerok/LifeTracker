package com.kamer.lifetracker.properties

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


class PropertiesViewModel : ViewModel() {

    fun getState(): Flow<ViewState> = flowOf(ViewState(listOf(
        UiProperty("1", "name1"),
        UiProperty("2", "name2"),
        UiProperty("3", "name3"),
        UiProperty("4", "name4")
    )))

}
