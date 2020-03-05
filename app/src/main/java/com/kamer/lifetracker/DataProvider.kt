package com.kamer.lifetracker

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.ValueRange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lifetracker.database.Data
import lifetracker.database.Entry
import lifetracker.database.EntryProperty
import lifetracker.database.Property
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.lang.ref.WeakReference
import java.util.*


object DataProvider {

    var activityRef: WeakReference<Activity>? = null

    val database by lazy { Data(activityRef!!.get()!!) }

    private val SHEET_ID = "1a9Phi9L0TzDrT1RwKcyaXiioW6ohsr4pCG1ezI7jZHo"

    suspend fun updateData() = withContext(Dispatchers.Default) {
        activityRef?.get()?.let { activity ->
            val scopes = listOf(SheetsScopes.SPREADSHEETS)
            val credential = GoogleAccountCredential.usingOAuth2(activity, scopes)
            credential.selectedAccount = GoogleSignIn.getLastSignedInAccount(activity)!!.account

            val jsonFactory = JacksonFactory.getDefaultInstance()
            val httpTransport = NetHttpTransport.Builder().build()
            val service = Sheets.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(activity.getString(R.string.app_name))
                .build()

            val data = service.spreadsheets().values().get(SHEET_ID, "A1:Z").execute()
            println(data)
            val values = data.getValues()

            val properties = values.first().drop(1).mapIndexed { index, value ->
                Property.Impl(
                    id = UUID.randomUUID().toString(),
                    name = value.toString(),
                    position = index.toLong()
                )
            }
            val entries = values.drop(1)
                .map {
                    LocalDate.parse(
                        it.first().toString(),
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    )
                }
                .mapIndexed { index, entryDate ->
                    Entry.Impl(
                        id = UUID.randomUUID().toString(),
                        date = entryDate,
                        position = index.toLong()
                    )
                }
            val entryProperties = values.drop(1).withIndex().flatMap { row ->
                val entryValues = row.value.drop(1)
                entryValues.mapIndexed { index, value ->
                    EntryProperty.Impl(
                        entryId = entries[row.index].id,
                        propertyId = properties[index].id,
                        value = when (value) {
                            "Y" -> true
                            "N" -> false
                            else -> null
                        }
                    )
                }
            }
            database.setData(
                properties,
                entries,
                entryProperties
            )
        }
    }

    suspend fun updateData(entryId: String, propertyId: String, value: Boolean?) =
        withContext(Dispatchers.Default) {
            activityRef?.get()?.let { activity ->
                val scopes = listOf(SheetsScopes.SPREADSHEETS)
                val credential = GoogleAccountCredential.usingOAuth2(activity, scopes)
                credential.selectedAccount = GoogleSignIn.getLastSignedInAccount(activity)!!.account

                val jsonFactory = JacksonFactory.getDefaultInstance()
                val httpTransport = NetHttpTransport.Builder().build()
                val service = Sheets.Builder(httpTransport, jsonFactory, credential)
                    .setApplicationName(activity.getString(R.string.app_name))
                    .build()

                val rowNumber = database.getEntry(entryId).position + 1 /*first row*/ + 1 /*indexes start from zero but table not*/
                val columnNumber = database.getProperty(propertyId).position + 1
                val range = ('A'.toInt() + columnNumber).toChar().toString() + rowNumber
                val valueRange =
                    ValueRange().setValues(
                        listOf(
                            listOf(
                                (value?.let { if (it) "Y" else "N" })
                                    ?: ""
                            )
                        )
                    )
                println("$range $value")
                service.spreadsheets().values().update(SHEET_ID, range, valueRange)
                    .apply { valueInputOption = "RAW" }.execute()
            }
        }
}
