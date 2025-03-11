package com.app.hydratracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val consumption = inputData.getString("time")
        sendNotifications(consumption)
        return Result.success()
    }
    fun sendNotifications(consumption:String?) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "default_channel"
            val channelName = "Default Notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = 1

        val drankIntent = Intent(applicationContext, NotificationActionReceiver::class.java).apply {
            action = "I_DRANK_ACTION"
            putExtra("notificationId", notificationId)
            putExtra("consumption", consumption)
        }
        val drankPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            0,
            drankIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notDrankIntent = Intent(applicationContext, NotificationActionReceiver::class.java).apply {
            action = "NOT_DRANK_ACTION"
            putExtra("notificationId", notificationId)
        }
        val notDrankPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            1,
            notDrankIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(applicationContext, "default_channel")
            .setSmallIcon(R.drawable.bottle_of_water_rafiki_1)
            .setContentTitle("Water Reminder")
            .setContentText("It's time to drink water!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(R.drawable.baseline_check_24, "I Drank", drankPendingIntent)
            .addAction(R.drawable.baseline_close_24, "Not Drank", notDrankPendingIntent)

        NotificationManagerCompat.from(applicationContext).notify(notificationId, builder.build())
    }
}