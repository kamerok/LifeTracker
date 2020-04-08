package com.kamer.lifetracker

import android.content.Context
import android.content.Intent
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lifetracker.common.auth.AuthData
import lifetracker.common.auth.checkForRecover


class DriveService(
    private val context: Context,
    private val authData: AuthData,
    private val httpTransport: HttpTransport,
    private val jsonFactory: JsonFactory,
    private val recoverFromError: suspend (Intent) -> Unit
) {

    suspend fun getSpreadsheets(): List<Spreadsheet> = withContext(Dispatchers.IO) {
        val credential = GoogleAccountCredential.usingOAuth2(context, listOf(DriveScopes.DRIVE))
        credential.selectedAccount = authData.account

        val service = Drive.Builder(httpTransport, jsonFactory, credential)
            .setApplicationName(context.getString(R.string.app_name))
            .build()

        checkForRecover(recoverFromError) {
            service.files().list().setQ("mimeType='application/vnd.google-apps.spreadsheet'")
                .execute()
                .let { fileList -> fileList.files.map { Spreadsheet(it.id, it.name) } }
                .also { println(it) }
        }
    }

}
