package com.example.grammarous.utils

import android.app.AlertDialog
import android.app.Service
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.annotation.RequiresApi
import kotlin.system.exitProcess

class TimeTracker : Service() {
    private lateinit var handler:Handler
    private lateinit var looper: Looper
    private  var elapsedTime:Int = 0
    private val SCREEN_TIME_LIMIT  = 290840985
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            looper = Looper.getMainLooper()
            handler = Handler(looper)
            startTime()
        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun startTime() {
        handler.postDelayed({
                            elapsedTime+=1000
            if(elapsedTime>=SCREEN_TIME_LIMIT){
                showDialog()
            }else{
                startTime()
            }
        },null, 1000)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun showDialog() {
        val intent = Intent("com.example.grammarous.SHOW_SCREEN_TIME_ALERT")
        sendBroadcast(intent)
    }


}