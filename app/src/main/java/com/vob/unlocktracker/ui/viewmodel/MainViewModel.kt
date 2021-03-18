package com.vob.unlocktracker.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vob.unlocktracker.db.Run
import com.vob.unlocktracker.repository.MainRepo
import com.vob.unlocktracker.util.SortType
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @ViewModelInject constructor(
        val mainRepo: MainRepo
): ViewModel() {

    private val runsSortedByDate = mainRepo.getAllRunsSortedByDate()
    private val runsSortedByDistance = mainRepo.getAllRunsSortedByDistance()
    private val runsSortedByCaloriesBurned = mainRepo.getAllRunsSortedByCaloriesBurned()
    private val runsSortedByTimeInMillis = mainRepo.getAllRunsSortedByTimeInMillis()
    private val runsSortedByAvgSpeed = mainRepo.getAllRunsSortedByAvgSpeed()

    val runs = MediatorLiveData<List<Run>>()

    var sortType = SortType.DATE //default sort type

    init {
        runs.addSource(runsSortedByDate) {
            if (sortType == SortType.DATE)
                it?.let { runs.value = it }
        }

        runs.addSource(runsSortedByAvgSpeed) {
            if (sortType == SortType.AVG_SPEED)
                it?.let { runs.value = it }
        }

        runs.addSource(runsSortedByCaloriesBurned) {
            if (sortType == SortType.CALORIES_BURNED)
                it?.let { runs.value = it }
        }

        runs.addSource(runsSortedByDistance) {
            if (sortType == SortType.DISTANCE)
                it?.let { runs.value = it }
        }

        runs.addSource(runsSortedByTimeInMillis) {
            if (sortType == SortType.RUNNING_TIME)
                it?.let { runs.value = it }
        }
    }

    fun sortRuns(sortType: SortType) =
            when(sortType)
            {
                SortType.DATE -> runsSortedByDate.value?.let { runs.value = it }
                SortType.RUNNING_TIME -> runsSortedByTimeInMillis.value?.let { runs.value = it }
                SortType.DISTANCE -> runsSortedByDistance.value?.let { runs.value = it }
                SortType.CALORIES_BURNED -> runsSortedByCaloriesBurned.value?.let { runs.value = it }
                SortType.AVG_SPEED -> runsSortedByAvgSpeed.value?.let { runs.value = it }
            } .also {
                this.sortType = sortType
            }

    fun insertRun(run: Run) = viewModelScope.launch {
        mainRepo.insertRun(run)
    }

}