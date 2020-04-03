package com.kamer.lifetracker

import android.app.Activity
import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.squareup.sqldelight.android.AndroidSqliteDriver
import lifetracker.database.Data
import lifetracker.database.Database
import lifetracker.database.DatabaseFactory
import java.lang.ref.WeakReference


object DataProvider {

    var activityRef: WeakReference<Activity>? = null

    val database by lazy {
        Data(
            DatabaseFactory.buildDatabase(
                AndroidSqliteDriver(
                    schema = Database.Schema,
                    context = activityRef!!.get()!!,
                    name = "database.db",
                    callback = object : AndroidSqliteDriver.Callback(Database.Schema) {
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            db.execSQL("PRAGMA foreign_keys=ON;")
                        }
                    }
                )
            )
        )
    }
    val prefs by lazy {
        Prefs(
            activityRef!!.get()!!.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        )
    }

    private val jsonFactory = JacksonFactory.getDefaultInstance()
    private val httpTransport = NetHttpTransport.Builder().build()
    private val driveService by lazy { DriveService(activityRef!!.get()!!.applicationContext, httpTransport, jsonFactory) }
    private val spreadSheetService by lazy { SpreadsheetService(activityRef!!.get()!!.applicationContext, prefs, httpTransport, jsonFactory) }
    private val synchronizer by lazy { Synchronizer(UpdateDataUseCase(database), spreadSheetService) }

    suspend fun updateData() = synchronizer.sync()

    suspend fun updateData(entryId: String, propertyId: String, value: Boolean?) {
        val rowNumber =
            database.getEntry(entryId).position + 1 /*first row*/ + 1 /*indexes start from zero but table not*/
        val columnNumber = database.getProperty(propertyId).position + 1
        val cellValue = (value?.let { if (it) "Y" else "N" }) ?: ""
        spreadSheetService.setCell(rowNumber, columnNumber, cellValue)
    }

    suspend fun getSheets(): List<Spreadsheet> = driveService.getSpreadsheets()
}
