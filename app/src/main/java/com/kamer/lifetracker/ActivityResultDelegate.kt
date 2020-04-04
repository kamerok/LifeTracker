package com.kamer.lifetracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred


class ActivityResultDelegate {

    private var activity: AppCompatActivity? = null
    private var currentCode: Int = 0
    private var resultByCode = mutableMapOf<Int, CompletableDeferred<ActivityResult?>>()

    fun registerActivity(activity: AppCompatActivity) {
        activity.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                @Suppress("NON_EXHAUSTIVE_WHEN")
                when (event) {
                    Lifecycle.Event.ON_CREATE -> this@ActivityResultDelegate.activity = activity
                    Lifecycle.Event.ON_DESTROY -> {
                        this@ActivityResultDelegate.activity = null
                        activity.lifecycle.removeObserver(this)
                    }
                }
            }
        })
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        return resultByCode[requestCode]?.let {
            it.complete(ActivityResult(resultCode, data))
            resultByCode.remove(requestCode)
            true
        } ?: false
    }

    fun launchIntentAsync(intent: Intent): Deferred<ActivityResult?> {
        activity?.run {
            val activityResult = CompletableDeferred<ActivityResult?>()
            if (intent.resolveActivity(packageManager) != null) {
                val resultCode = currentCode++
                resultByCode[resultCode] = activityResult
                startActivityForResult(intent, resultCode)
            } else {
                activityResult.complete(null)
            }
            return activityResult
        }
        return CompletableDeferred<ActivityResult?>().apply { complete(null) }
    }
}
