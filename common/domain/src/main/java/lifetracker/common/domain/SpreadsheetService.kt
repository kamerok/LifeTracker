package lifetracker.common.domain

import android.content.Context
import android.content.Intent
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.ValueRange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lifetracker.common.auth.AuthData
import lifetracker.common.auth.checkForRecover


class SpreadsheetService(
    private val context: Context,
    private val appName: String,
    private val authData: AuthData,
    private val httpTransport: HttpTransport,
    private val jsonFactory: JsonFactory,
    private val recoverFromError: suspend (Intent) -> Unit
) {

    private val scopes = listOf(SheetsScopes.SPREADSHEETS)

    suspend fun getData(): List<List<Any>> = withContext(Dispatchers.IO) {
        val credential = GoogleAccountCredential.usingOAuth2(context, scopes)
        credential.selectedAccount = authData.account

        val service = Sheets.Builder(httpTransport, jsonFactory, credential)
            .setApplicationName(appName)
            .build()

        val data = checkForRecover(recoverFromError) {
            service.spreadsheets().values().get(authData.sheetId, "A1:Z").execute()
        }
        println(data)
        data.getValues()
    }

    suspend fun setCell(row: Long, column: Long, value: Any) = withContext(Dispatchers.IO) {
        val credential = GoogleAccountCredential.usingOAuth2(context, scopes)
        credential.selectedAccount = authData.account

        val service = Sheets.Builder(httpTransport, jsonFactory, credential)
            .setApplicationName(appName)
            .build()

        val range = ('A'.toInt() + column).toChar().toString() + row
        val valueRange = ValueRange().setValues(listOf(listOf(value)))
        println("$range $value")
        checkForRecover(recoverFromError) {
            service.spreadsheets().values().update(authData.sheetId, range, valueRange)
                .apply { valueInputOption = "RAW" }.execute()
        }
        Unit
    }

}
