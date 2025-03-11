package com.app.hydratracker

import android.app.Application
import android.util.Log
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        scheduleBackupWork()
    }
    fun scheduleBackupWork() {
        val now = Calendar.getInstance()
        val midnight = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        if (now.after(midnight)) {
            midnight.add(Calendar.DAY_OF_YEAR, 1)
        }
        val initialDelay = midnight.timeInMillis - now.timeInMillis
        val backupRequest = OneTimeWorkRequestBuilder<SaveDailyIntakeWorker>()
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(applicationContext).enqueue(backupRequest)
    }

}
