package com.kamer.lifetracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.kamer.lifetracker.spreadsheets.SpreadsheetsFragment
import java.lang.ref.WeakReference


class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.fragmentFactory = object : FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String): Fragment =
                when (className) {
                    SpreadsheetsFragment::class.qualifiedName -> SpreadsheetsFragment(DataProvider.driveService)
                    else -> super.instantiate(classLoader, className)
                }
        }

        DataProvider.activityRef = WeakReference(this)
    }
}
