package com.example.runningapp.ui.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runningapp.db.RunDto
import com.example.runningapp.enums.SortType
import com.example.runningapp.repositories.RunRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val runRepository: RunRepository
):ViewModel() {

    private val runsSortedByDate = runRepository.getAllRunsSortedByDate()
    private val runsSortedByAvgSpeed = runRepository.getAllRunsSortedByAvgSpeed()
    private val runsSortedByDistance = runRepository.getAllRunsSortedByDistance()
    private val runsSortedByCaloriesBurned = runRepository.getAllRunsSortedByCaloriesBurned()
    private val runsSortedByTimeInMillis = runRepository.getAllRunsSortedByTimeInMillis()



    val runs = MediatorLiveData<List<RunDto>>()
    var sortType = SortType.DATE


    init {
        runs.addSource(runsSortedByDate){
            sortRun(sortType)
        }
        runs.addSource(runsSortedByDistance){
            sortRun(sortType)
        }
        runs.addSource(runsSortedByAvgSpeed){
            sortRun(sortType)
        }
        runs.addSource(runsSortedByTimeInMillis){
            sortRun(sortType)
        }
        runs.addSource(runsSortedByCaloriesBurned){
            sortRun(sortType)
        }
    }


     fun sortRun(sortType: SortType) =
        when(sortType){
            SortType.DATE -> runsSortedByDate.value?.let {runs.value = it }
            SortType.DISTANCE -> runsSortedByDistance.value?.let {runs.value = it }
            SortType.AVG_SPEED -> runsSortedByAvgSpeed.value?.let {runs.value = it }
            SortType.RUNNING_TIME -> runsSortedByTimeInMillis.value?.let {runs.value = it }
            SortType.CALORIES_BURNED -> runsSortedByCaloriesBurned.value?.let {runs.value = it }
        }


    fun insertNewRun(runDto: RunDto){
        viewModelScope.launch {
            runRepository.addNewRun(runDto)
        }
    }
}