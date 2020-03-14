package com.kamer.lifetracker

import android.app.Activity
import lifetracker.database.Data
import java.lang.ref.WeakReference


object DataProvider {

    var activityRef: WeakReference<Activity>? = null

    val database by lazy { Data(activityRef!!.get()!!) }

    private val service by lazy { Service(activityRef!!.get()!!.applicationContext) }
    private val updateDataUseCase by lazy { UpdateDataUseCase(database) }

    suspend fun updateData() = updateDataUseCase.saveData(service.getData())

    suspend fun updateData(entryId: String, propertyId: String, value: Boolean?) {
        val rowNumber =
            database.getEntry(entryId).position + 1 /*first row*/ + 1 /*indexes start from zero but table not*/
        val columnNumber = database.getProperty(propertyId).position + 1
        val cellValue = (value?.let { if (it) "Y" else "N" }) ?: ""
        service.setCell(rowNumber, columnNumber, cellValue)
    }
}
