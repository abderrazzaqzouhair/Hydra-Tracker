package com.app.hydratracker

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.hydratracker.data.AppDatabase
import com.app.hydratracker.data.DailyTarget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val time = intent?.getStringExtra("consumption")
        val database = AppDatabase.getDatabase(context)

        val currentDay = database.dailyTargetDao()


        val notificationId = intent?.getIntExtra("notificationId", 0) ?: 0
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        when (intent?.action) {
            "I_DRANK_ACTION" -> {
                notificationManager.cancel(notificationId)
                CoroutineScope(Dispatchers.IO).launch {
                    currentDay.updateIsDrinked(time?: "", true)
                }


            }
            "NOT_DRANK_ACTION" -> {
                notificationManager.cancel(notificationId)
                Toast.makeText(context, "You didn't drink water. Stay hydrated!", Toast.LENGTH_SHORT).show()
                Log.d("this", "not drink")
            }
        }
    }
}
