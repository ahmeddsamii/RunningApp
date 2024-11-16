package com.example.runningapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runningapp.db.RunDto
import com.example.runningapp.repositories.RunRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val runRepository: RunRepository
):ViewModel() {


    fun getAllRunSortedByDate() = runRepository.getAllRunsSortedByDate()

    fun insertNewRun(runDto: RunDto){
        viewModelScope.launch {
            runRepository.addNewRun(runDto)
        }
    }
}