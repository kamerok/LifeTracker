package com.kamer.lifetracker

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import java.lang.ref.WeakReference


object DataProvider {

    var activityRef: WeakReference<Activity>? = null

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

            val data = service.spreadsheets().values()
                .get("1a9Phi9L0TzDrT1RwKcyaXiioW6ohsr4pCG1ezI7jZHo", "A1:Z").execute()
            println(data)
            return data.getValues()
        } ?: emptyList()
}
