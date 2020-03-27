package com.kamer.lifetracker

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import com.kamer.lifetracker.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference


class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        DataProvider.activityRef = WeakReference(this)

        lifecycle.coroutineScope.launch {
            try {
                DataProvider.updateData()
            } catch (e: Exception) {
                Log.e("TAG", "omg: ", e)
            }
        }
    }
}
