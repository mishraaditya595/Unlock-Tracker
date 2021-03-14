package com.vob.unlocktracker.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.vob.unlocktracker.repository.MainRepo
import javax.inject.Inject

class MainViewModel @ViewModelInject constructor(
        val mainRepo: MainRepo
): ViewModel() {

}