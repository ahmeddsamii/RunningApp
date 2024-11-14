package com.example.runningapp.services

import android.content.Intent
import androidx.lifecycle.LifecycleService
import com.example.runningapp.other.Constants.ACTION_PAUSE_SERVICE
import com.example.runningapp.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runningapp.other.Constants.ACTION_STOP_SERVICE
import timber.log.Timber

class TrackingServices: LifecycleService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(intent.action){
                ACTION_START_OR_RESUME_SERVICE ->{
                    Timber.d("started or resume service")
                }
                ACTION_PAUSE_SERVICE ->{
                    Timber.d("paused service")
                }
                ACTION_STOP_SERVICE ->{
                    Timber.d("stopped service")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}