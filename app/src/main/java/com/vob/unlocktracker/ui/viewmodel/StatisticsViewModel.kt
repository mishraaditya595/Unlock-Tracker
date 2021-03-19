package com.vob.unlocktracker.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.vob.unlocktracker.repository.MainRepo
import javax.inject.Inject

class StatisticsViewModel @ViewModelInject constructor(
        val mainRepo: MainRepo
): ViewModel() {

        val totalTimeRun = mainRepo.getTotalTimeInMillis()
        val totalDistance = mainRepo.getTotalDistance()
        val totalCalBurned = mainRepo.getTotalCaloriesBurned()
        val totalAvgSpeed = mainRepo.getTotalAvgSpeed()

        val runsSortedByDate = mainRepo.getAllRunsSortedByDate()
}