package com.kamer.lifetracker

import android.app.Activity
import android.content.Context
import lifetracker.database.Data
import lifetracker.database.DatabaseFactory
import java.lang.ref.WeakReference


object DataProvider {

    var activityRef: WeakReference<Activity>? = null

    val database by lazy { Data(DatabaseFactory.buildDatabase(activityRef!!.get()!!)) }
    val prefs by lazy {
        Prefs(
            activityRef!!.get()!!.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        )
    }

    private val service by lazy { Service(activityRef!!.get()!!.applicationContext, prefs) }
    private val synchronizer by lazy { Synchronizer(UpdateDataUseCase(database), service) }

    suspend fun updateData() = synchronizer.sync()

    suspend fun updateData(entryId: String, propertyId: String, value: Boolean?) {
        val rowNumber =
            database.getEntry(entryId).position + 1 /*first row*/ + 1 /*indexes start from zero but table not*/
        val columnNumber = database.getProperty(propertyId).position + 1
        val cellValue = (value?.let { if (it) "Y" else "N" }) ?: ""
        service.setCell(rowNumber, columnNumber, cellValue)
    }

    suspend fun getSheets(): List<Spreadsheet> = listOf(
        Spreadsheet("1a9Phi9L0TzDrT1RwKcyaXiioW6ohsr4pCG1ezI7jZHo", "Test"),
        Spreadsheet("1zaVn2FPlWGwfPg3Fn7MqctkUoH3QAEg212GUcdGoELE", "Real")
    )
}
