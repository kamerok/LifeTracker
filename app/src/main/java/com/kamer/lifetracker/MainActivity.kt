package com.kamer.lifetracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!activityResultDelegate.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}
