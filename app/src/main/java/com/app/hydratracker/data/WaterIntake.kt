package com.app.hydratracker.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "water_intake",
    indices = [Index(value = ["date"], unique = true)]
)
data class WaterIntake(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val amount: Int,
    val target:Int
)
