package com.app.hydratracker.ui.notification

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.hydratracker.HomeActivity
import com.app.hydratracker.R
import com.app.hydratracker.data.CustomDate
import com.app.hydratracker.data.Notifications
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale



class NotificationActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notification)
        val back = findViewById<ImageView>(R.id.back)
        val notificationDetails: ConstraintLayout = findViewById(R.id.not_details)
        val close:ImageView = findViewById(R.id.close)
        val ivIcon:ImageView = findViewById(R.id.ivIcon)
        val title: TextView = findViewById(R.id.tvTitle)
        val description: TextView = findViewById(R.id.tvDescription)
        close.setOnClickListener {
            notificationDetails.visibility = View.GONE
        }
        val notificationsList = listOf(
            Notifications(
                title = "Time to Hydrate!",
                image = R.drawable.baseline_water_drop_24,
                description = "It's been a while since your last drink. Stay hydrated!",
                date = CustomDate(2025, 3, 4).toDate(),
                readed = true
            ),
            Notifications(
                title = "Daily Goal Achieved!",
                image = R.drawable.baseline_today_24,
                description = "Congratulations! You've reached your daily hydration goal.",
                date = CustomDate(2025, 2, 2).toDate(),
                readed = true
            ),
            Notifications(
                title = "Hydration Tip",
                image = R.drawable.baseline_tips_and_updates_24,
                description = "Did you know? Drinking water before meals can help with digestion.",
                date = CustomDate(2025, 3, 4).toDate(),
                readed = false
            ),
            Notifications(
                title = "Halfway There!",
                image = R.drawable.baseline_add_road_24,
                description = "You're halfway to your daily hydration goal. Keep it up!",
                date = CustomDate(2025, 2, 14).toDate(),
                readed = false
            ),
            Notifications(
                title = "Low Hydration Alert",
                image = R.drawable.baseline_warning_amber_24,
                description = "Your water intake is low today. Don't forget to drink more!",
                date = CustomDate(2025, 2, 24).toDate(),
                readed = true
            ),
            Notifications(
                title = "New Streak Record!",
                image = R.drawable.baseline_local_fire_department_24,
                description = "You've set a new record of 7 days in a row. Amazing job!",
                date = CustomDate(2025, 2, 24).toDate(),
                readed = true
            ),
            Notifications(
                title = "Hydration Reminder",
                image = R.drawable.baseline_access_alarm_24,
                description = "Don't forget to log your water intake for the day.",
                date = CustomDate(2025, 2, 28).toDate(),
                readed = true
            ),
            Notifications(
                title = "Weekly Summary",
                image = R.drawable.baseline_check_24,
                description = "You drank an average of 2.5 liters per day this week. Great job!",
                date = CustomDate(2025, 3, 1).toDate(),
                readed = false
            ),
            Notifications(
                title = "Custom Goal Set",
                image = R.drawable.baseline_track_changes_24,
                description = "Your new daily hydration goal of 3 liters has been set.",
                date = CustomDate(2025, 2, 16).toDate(),
                readed = true
            ),
            Notifications(
                title = "Hydration Challenge",
                image = R.drawable.baseline_show_chart_24,
                description = "Join our 7-day hydration challenge and earn rewards!",
                date = CustomDate(2025, 1, 30).toDate(),
                readed = false
            )
        )
        val customAdapter = NotificationAdapter(notificationsList) { position ->
            notificationDetails.visibility = View.VISIBLE
            ivIcon.setImageResource(notificationsList[position].image)
            title.text = notificationsList[position].title
            description.text = notificationsList[position].description
        }

        val recyclerView: RecyclerView = findViewById(R.id.my_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = customAdapter


        back.setOnClickListener {
            Intent(this, HomeActivity::class.java).also {
                startActivity(it)
            }
        }

    }
}