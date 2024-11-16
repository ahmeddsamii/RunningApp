package com.example.runningapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.runningapp.repositories.RunRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    runRepository: RunRepository
):ViewModel() {

    val totalTimeRun = runRepository.getTotalTimeInMillis()
    val totalDistance = runRepository.getTotalDistance()
    val totalCaloriesBurned = runRepository.getTotalCaloriesBurned()
    val totalSpeed = runRepository.getTotalAvgSpeed()


    val runsSortedByDate = runRepository.getAllRunsSortedByDate()
}