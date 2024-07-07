package com.example.grammarous.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import android.os.SystemClock


class ApplicationTimeSpentTracker : ActivityLifecycleCallbacks {
    private var startTime: Long = 0
    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {
        startTime = SystemClock.elapsedRealtime() // Record the start time when the app is resumed
    }

    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}

    // Check the time spent when the app goes into the background
    override fun onActivityPaused(activity: Activity) {
        val elapsedTime = SystemClock.elapsedRealtime() - startTime
        if (elapsedTime >= TIME_LIMIT_MS) {
            // If the time limit is exceeded, show an AlertDialog
            showTimeLimitReachedDialog(activity)
        }
    }

    // Method to show AlertDialog when time limit is reached
    private fun showTimeLimitReachedDialog(activity: Activity) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Time Limit Reached")
        builder.setMessage("You have reached the time limit for using this app.")
        builder.setPositiveButton(
            "OK"
        ) { dialogInterface, i -> // Close the app when the user clicks OK
            activity.finish()
        }
        val dialog = builder.create()
        dialog.show()
    }

    companion object {
        private const val TIME_LIMIT_MS = ( 60 * 1000 // 20 minutes in milliseconds
                ).toLong()
    }
}

