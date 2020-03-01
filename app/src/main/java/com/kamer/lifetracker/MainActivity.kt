package com.kamer.lifetracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.lifecycle.coroutineScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.api.services.sheets.v4.SheetsScopes
import com.kamer.lifetracker.databinding.ActivityMainBinding
import com.kamer.lifetracker.properties.PropertiesFragment
import com.kamer.lifetracker.records.RecordsFragment
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference


class MainActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        DataProvider.activityRef = WeakReference(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(SheetsScopes.SPREADSHEETS))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.signInView.setOnClickListener {
            startActivityForResult(googleSignInClient.signInIntent, SIGN_IN)
        }

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.fragment_container, RecordsFragment::class.java, null, "history")
            }
        }

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_history -> {
                    supportFragmentManager.commit {
                        supportFragmentManager.findFragmentByTag("props")?.run { hide(this) }
                        show(supportFragmentManager.findFragmentByTag("history")!!)
                    }
                    true
                }
                R.id.action_properties -> {
                    supportFragmentManager.commit {
                        supportFragmentManager.findFragmentByTag("history")?.run { hide(this) }
                        val fragment = supportFragmentManager.findFragmentByTag("props")
                        if (fragment != null) {
                            show(fragment)
                        } else {
                            add(
                                R.id.fragment_container,
                                PropertiesFragment::class.java,
                                null,
                                "props"
                            )
                        }
                    }
                    true
                }
                else -> false
            }
        }
    }

    override fun onStart() {
        super.onStart()
        updateUi()
    }

    private fun updateUi() {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        binding.signInView.isVisible = account == null
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
        lifecycle.coroutineScope.launch {
            try {
                DataProvider.updateData()
                // Signed in successfully, show authenticated UI.
            } catch (e: Exception) { // The ApiException status code indicates the detailed failure reason.
// Please refer to the GoogleSignInStatusCodes class reference for more information.
            }
            updateUi()
        }
    }

    companion object {
        private const val SIGN_IN = 10
    }
}
