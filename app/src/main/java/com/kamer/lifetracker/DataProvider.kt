package com.kamer.lifetracker

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.squareup.sqldelight.android.AndroidSqliteDriver
import lifetracker.common.auth.AuthData
import lifetracker.common.database.Data
import lifetracker.common.database.Database
import lifetracker.common.database.DatabaseFactory
import lifetracker.common.domain.SpreadsheetService
import lifetracker.common.domain.Synchronizer
import lifetracker.common.domain.UpdateDataUseCase
import lifetracker.library.activityresult.ActivityResultDelegate
import java.lang.ref.WeakReference


object DataProvider {

    var context: WeakReference<Context>? = null

    val database by lazy {
        Data(
            DatabaseFactory.buildDatabase(
                AndroidSqliteDriver(
                    schema = Database.Schema,
                    context = context!!.get()!!,
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
    val authData by lazy {
        AuthData(
            context!!.get()!!,
            context!!.get()!!.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        )
    }

    val jsonFactory: JsonFactory = JacksonFactory.getDefaultInstance()
    val httpTransport: HttpTransport = NetHttpTransport.Builder().build()
    val activityResultDelegate =
        ActivityResultDelegate()
    val spreadSheetService by lazy {
        SpreadsheetService(
            context!!.get()!!.applicationContext,
            context!!.get()!!.getString(R.string.app_name),
            authData,
            httpTransport,
            jsonFactory
        ) {
            activityResultDelegate.launchIntentAsync(it).await()
        }
    }

    val synchronizer by lazy {
        Synchronizer(UpdateDataUseCase(database), spreadSheetService)
    }

}
