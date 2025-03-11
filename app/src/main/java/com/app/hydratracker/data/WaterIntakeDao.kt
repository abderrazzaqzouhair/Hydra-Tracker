package com.app.hydratracker.data


import androidx.room.Update
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface WaterIntakeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterIntake(waterIntake: WaterIntake)

    @Update
    suspend fun updateWaterIntake(waterIntake: WaterIntake)

    @Query("DELETE FROM water_intake")
    suspend fun deleteAllTargets()

    @Delete
    suspend fun deleteWaterIntake(waterIntake: WaterIntake)

    @Query("SELECT * FROM water_intake WHERE date = :date")
    suspend fun getWaterIntakeByDate(date: String): List<WaterIntake>

    @Query("SELECT * FROM water_intake ORDER BY date ASC")
    suspend fun getAllWaterIntakes(): List<WaterIntake>
}
