package com.app.hydratracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_target")
data class DailyTarget(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val targetAmount: Int,
    val time: String,
    val isdrinked: Boolean,
)

