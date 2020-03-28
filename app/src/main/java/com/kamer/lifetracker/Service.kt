package com.kamer.lifetracker

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.ValueRange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class Service(
    private val context: Context,
    private val prefs: Prefs
) {

    private val scopes = listOf(SheetsScopes.SPREADSHEETS, DriveScopes.DRIVE)
    private val jsonFactory = JacksonFactory.getDefaultInstance()
    private val httpTransport = NetHttpTransport.Builder().build()

    suspend fun getData(): List<List<Any>> = withContext(Dispatchers.IO) {
        val credential = GoogleAccountCredential.usingOAuth2(context, scopes)
        credential.selectedAccount = GoogleSignIn.getLastSignedInAccount(context)!!.account

        val service = Sheets.Builder(httpTransport, jsonFactory, credential)
            .setApplicationName(context.getString(R.string.app_name))
            .build()

        val data = service.spreadsheets().values().get(prefs.sheetId, "A1:Z").execute()
        println(data)
        data.getValues()
    }

    suspend fun setCell(row: Long, column: Long, value: Any) = withContext(Dispatchers.IO) {
        val credential = GoogleAccountCredential.usingOAuth2(context, scopes)
        credential.selectedAccount = GoogleSignIn.getLastSignedInAccount(context)!!.account

        val service = Sheets.Builder(httpTransport, jsonFactory, credential)
            .setApplicationName(context.getString(R.string.app_name))
            .build()

        val range = ('A'.toInt() + column).toChar().toString() + row
        val valueRange = ValueRange().setValues(listOf(listOf(value)))
        println("$range $value")
        service.spreadsheets().values().update(prefs.sheetId, range, valueRange)
            .apply { valueInputOption = "RAW" }.execute()
    }

    suspend fun getSpreadsheets(): List<Spreadsheet> = withContext(Dispatchers.IO) {
        val credential = GoogleAccountCredential.usingOAuth2(context, scopes)
        credential.selectedAccount = GoogleSignIn.getLastSignedInAccount(context)!!.account

        val service = Drive.Builder(httpTransport, jsonFactory, credential)
            .setApplicationName(context.getString(R.string.app_name))
            .build()

        service.files().list().setQ("mimeType='application/vnd.google-apps.spreadsheet'")
            .execute()
            .let { fileList ->
                fileList.files.map {
                    Spreadsheet(
                        it.id,
                        it.name
                    )
                }
            }
            .also { println(it) }
    }

}
