package com.vob.unlocktracker.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vob.unlocktracker.db.Run
import com.vob.unlocktracker.repository.MainRepo
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @ViewModelInject constructor(
        val mainRepo: MainRepo
): ViewModel() {

    fun insertRun(run: Run) = viewModelScope.launch {
        mainRepo.insertRun(run)
    }

}