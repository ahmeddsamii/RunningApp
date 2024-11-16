package com.example.runningapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RunDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNewRun(runDto: RunDto)

    @Delete
    suspend fun deleteRun(runDto: RunDto)

    @Query("SELECT * FROM running_table ORDER BY timeStamp DESC ")
    fun getAllRunsSortedByDate():LiveData<List<RunDto>>

    @Query("SELECT * FROM running_table ORDER BY timeInMillis DESC ")
    fun getAllRunsSortedByInMillis():LiveData<List<RunDto>>

    @Query("SELECT * FROM running_table ORDER BY avgSpeedInKMH DESC ")
    fun getAllRunsSortedByAvgSpeed():LiveData<List<RunDto>>

    @Query("SELECT * FROM running_table ORDER BY caloriesBurned DESC ")
    fun getAllRunsSortedByCaloriesBurned():LiveData<List<RunDto>>

    @Query("SELECT * FROM running_table ORDER BY distanceInMeters DESC ")
    fun getAllRunsSortedByDistance():LiveData<List<RunDto>>


    @Query("SELECT SUM(timeInMillis) FROM running_table")
    fun getTotalTimeInMillis():LiveData<Long>

    @Query("SELECT SUM(caloriesBurned) FROM running_table")
    fun getTotalCaloriesBurned():LiveData<Int>

    @Query("SELECT SUM(distanceInMeters) FROM running_table")
    fun getTotalDistance():LiveData<Int>

    @Query("SELECT AVG(avgSpeedInKMH) FROM running_table")
    fun getTotalAvgSpeed():LiveData<Float>

}