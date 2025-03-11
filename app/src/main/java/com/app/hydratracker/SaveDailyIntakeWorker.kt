package com.app.hydratracker

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import androidx.work.*
import com.app.hydratracker.data.AppDatabase
import com.app.hydratracker.data.WaterIntake
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SaveDailyIntakeWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val sharedPreferences = applicationContext.getSharedPreferences("WaterIntakePrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        GlobalScope.launch(Dispatchers.IO) {
            var todayAmount = 0
            val waterIntakeDao = AppDatabase.getDatabase(applicationContext).waterIntakeDao()
            val todayDrink = AppDatabase.getDatabase(applicationContext).dailyTargetDao()
            val list = todayDrink.getAllTargets()
            list.forEach { target ->
                if (target.isdrinked) {
                    todayAmount += target.targetAmount
                }
            }
            val waterIntake = WaterIntake(date = getPreviousDate(), amount = todayAmount, target = sharedPreferences.getInt("theTarget", 1000))
            waterIntakeDao.insertWaterIntake(waterIntake)
            todayDrink.deleteAllTargets()
            editor.putInt("theTarget", 0)
            editor.apply()
            Log.d("this", "this is the time : " + waterIntakeDao.getAllWaterIntakes().toString())
        }
        return Result.success()
    }

    fun getPreviousDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        return dateFormat.format(calendar.time)
    }
}

