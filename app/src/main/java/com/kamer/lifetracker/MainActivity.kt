package com.kamer.lifetracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.navigation.NavController
import androidx.navigation.findNavController
import lifetracker.feature.login.LoginFragment
import lifetracker.feature.spreadsheets.SpreadsheetsFragment


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
                    SpreadsheetsFragment::class.qualifiedName -> SpreadsheetsFragment(
                        getString(R.string.app_name),
                        DataProvider.authData,
                        DataProvider.httpTransport,
                        DataProvider.jsonFactory,
                        { DataProvider.activityResultDelegate.launchIntentAsync(it).await() }
                    ) {
                        DataProvider.authData.sheetId = it
                        findNavController(R.id.fragment_container)
                            .navigate(R.id.action_spreadsheets_fragment_to_home_fragment)
                    }
                    LoginFragment::class.qualifiedName -> LoginFragment {
                        findNavController(R.id.fragment_container)
                            .navigate(R.id.action_login_fragment_to_spreadsheets_fragment)
                    }
                    else -> super.instantiate(classLoader, className)
                }
        }
    }

    override fun onStart() {
        super.onStart()
        val navController = findNavController(R.id.fragment_container)

        if (!navController.isGraphSet()) {
            val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
            val authData = DataProvider.authData
            val isLoggedAndSet = authData.account != null && authData.sheetId != null
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
