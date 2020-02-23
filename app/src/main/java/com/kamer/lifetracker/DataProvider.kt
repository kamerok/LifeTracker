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
import java.lang.ref.WeakReference


object DataProvider {

    var activityRef: WeakReference<Activity>? = null

    private val cachedData = mutableListOf<List<Any>>()

    private val SHEET_ID = "1a9Phi9L0TzDrT1RwKcyaXiioW6ohsr4pCG1ezI7jZHo"

    fun getData(): List<List<Any>> =
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
            cachedData.clear()
            cachedData.addAll(data.getValues())
            return data.getValues()
        } ?: emptyList()

    fun getCachedData(): List<List<Any>> = cachedData

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

                val rowNumber = entryId.toInt() + 1 /*first row*/ + 1 /*indexes start from 0 but table not*/
                val columnNumber = propertyId.toInt() + 1
                val range = ('A'.toInt() + columnNumber).toChar().toString() + rowNumber
                val value = ValueRange().setValues(listOf(listOf((value?.let { if (it) "Y" else "N" }) ?: "")))
                println("$range $value")
                service.spreadsheets().values().update(SHEET_ID, range, value).apply { valueInputOption = "RAW"}.execute()
            }
        }
}
