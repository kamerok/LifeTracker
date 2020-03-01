package com.kamer.lifetracker.property

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class PropertyViewModel(private val id: String) : ViewModel() {

    fun getState(): Flow<ViewState> = flow {
        emit(ViewState(emptyMap()))
    }

}
