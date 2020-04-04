package com.kamer.lifetracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.kamer.lifetracker.spreadsheets.SpreadsheetsFragment


class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val activityResultDelegate = DataProvider.activityResultDelegate

    init {
        activityResultDelegate.registerActivity(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.fragmentFactory = object : FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String): Fragment =
                when (className) {
                    SpreadsheetsFragment::class.qualifiedName -> SpreadsheetsFragment(DataProvider.driveService)
                    else -> super.instantiate(classLoader, className)
                }
        }
    }

    override fun onStart() {
        super.onStart()
        val navController = findNavController(R.id.fragment_container)

        if (!navController.isGraphSet()) {
            val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
            val isLoggedAndSet = GoogleSignIn.getLastSignedInAccount(this) != null &&
                    DataProvider.prefs.sheetId != null
            val destination = if (isLoggedAndSet) R.id.home_fragment else R.id.login_fragment

            navGraph.startDestination = destination
            navController.graph = navGraph
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!activityResultDelegate.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun NavController.isGraphSet(): Boolean =
        try {
            graph; true
        } catch (e: IllegalStateException) {
            false
        }

}
