package com.vob.unlocktracker.service

import android.content.Intent
import androidx.lifecycle.LifecycleService
import com.vob.unlocktracker.util.Constants.ACTION_PAUSE_SERVICE
import com.vob.unlocktracker.util.Constants.ACTION_START_OR_RESUME_SERVICE
import com.vob.unlocktracker.util.Constants.ACTION_STOP_SERVICE
import timber.log.Timber

class TrackingService: LifecycleService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action)
            {
                ACTION_START_OR_RESUME_SERVICE ->
                {
                    Timber.d("Service started or resumed")
                }
                ACTION_PAUSE_SERVICE ->
                {
                    Timber.d("Service paused")
                }
                ACTION_STOP_SERVICE ->
                {
                    Timber.d("Service stopped")
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }
}