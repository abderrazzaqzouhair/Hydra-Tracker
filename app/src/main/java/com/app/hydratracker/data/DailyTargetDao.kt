package com.app.hydratracker.data

import androidx.room.*
@Dao
interface DailyTargetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTarget(target: DailyTarget)

    @Query("SELECT * FROM daily_target Order By time Asc")
    suspend fun getAllTargets(): List<DailyTarget>

    @Query("DELETE FROM daily_target")
    suspend fun deleteAllTargets()

    @Query("UPDATE daily_target SET isDrinked = :isDrinked WHERE time = :time")
    suspend fun updateIsDrinked(time: String, isDrinked: Boolean)
}

