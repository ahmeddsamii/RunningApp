package com.example.runningapp.repositories

import com.example.runningapp.db.RunDao
import com.example.runningapp.db.RunDto
import javax.inject.Inject

class RunRepository @Inject constructor(
    val runDao: RunDao
) {
    suspend fun addNewRun(runDto: RunDto) = runDao.addNewRun(runDto)

    suspend fun deleteRun(runDto: RunDto) = runDao.deleteRun(runDto)

    fun getAllRunsSortedByDate() = runDao.getAllRunsSortedByDate()
    fun getAllRunsSortedByTimeInMillis() = runDao.getAllRunsSortedByInMillis()
    fun getAllRunsSortedByAvgSpeed() = runDao.getAllRunsSortedByAvgSpeed()
    fun getAllRunsSortedByDistance() = runDao.getAllRunsSortedByDistance()
    fun getAllRunsSortedByCaloriesBurned() = runDao.getAllRunsSortedByCaloriesBurned()
    fun getTotalTimeInMillis() = runDao.getTotalTimeInMillis()
    fun getTotalCaloriesBurned() = runDao.getTotalCaloriesBurned()
    fun getTotalDistance() = runDao.getTotalDistance()
    fun getTotalAvgSpeed() = runDao.getTotalAvgSpeed()
}