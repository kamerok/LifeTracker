package com.kamer.lifetracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(SheetsScopes.SPREADSHEETS))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        signInView.setOnClickListener {
            startActivityForResult(googleSignInClient.signInIntent, SIGN_IN)
        }
        testView.setOnClickListener {
            val scopes = listOf(SheetsScopes.SPREADSHEETS)
            val credential = GoogleAccountCredential.usingOAuth2(this, scopes)
            credential.selectedAccount = GoogleSignIn.getLastSignedInAccount(this)!!.account

            val jsonFactory = JacksonFactory.getDefaultInstance()
            val httpTransport =  NetHttpTransport.Builder().build()
            val service = Sheets.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(getString(R.string.app_name))
                .build()

            GlobalScope.launch {
                val data = service.spreadsheets().values().get("1a9Phi9L0TzDrT1RwKcyaXiioW6ohsr4pCG1ezI7jZHo", "A1:Z").execute()
                println(data)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        updateUi()
    }

    private fun updateUi() {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        statusView.text = if (account != null) "signed" else "unsigned"
        signInView.isVisible = account == null
        testView.isVisible = account != null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SIGN_IN -> {
                // The Task returned from this call is always completed, no need to attach
                // a listener.
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
            }
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
        } catch (e: ApiException) { // The ApiException status code indicates the detailed failure reason.
// Please refer to the GoogleSignInStatusCodes class reference for more information.
        }
        updateUi()
    }

    companion object {
        private const val SIGN_IN = 10
    }
}
