package com.kamer.lifetracker

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.ValueRange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class SpreadsheetService(
    private val context: Context,
    private val prefs: Prefs,
    private val httpTransport: HttpTransport,
    private val jsonFactory: JsonFactory
) {

    private val scopes = listOf(SheetsScopes.SPREADSHEETS)

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

}
