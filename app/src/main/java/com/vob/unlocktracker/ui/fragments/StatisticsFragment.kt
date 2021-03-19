package com.vob.unlocktracker.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.vob.unlocktracker.R
import com.vob.unlocktracker.databinding.FragmentStatisticsBinding
import com.vob.unlocktracker.ui.viewmodel.MainViewModel
import com.vob.unlocktracker.ui.viewmodel.StatisticsViewModel
import com.vob.unlocktracker.util.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlin.math.round

@AndroidEntryPoint
class StatisticsFragment : Fragment(R.layout.fragment_statistics) {

    private val viewModel: StatisticsViewModel by viewModels()
    private lateinit var binding: FragmentStatisticsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStatisticsBinding.bind(view)

        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        viewModel.totalTimeRun.observe(viewLifecycleOwner, Observer {
            it?.let {
                val totalTimeRun = TrackingUtility.getFormattedStopWatchTime(it)
                binding.tvTotalTime.text = totalTimeRun
            }
        })
        viewModel.totalDistance.observe(viewLifecycleOwner, Observer {
            it.let {
                val distanceInKm = it / 1000f
                val totalDistanceRounded = round(distanceInKm * 10f) / 10f
                val totalDistanceString = "${totalDistanceRounded}km"
                binding.tvTotalDistance.text = totalDistanceString
            }
        })
        viewModel.totalAvgSpeed.observe(viewLifecycleOwner, Observer {
            it?.let {
                val avgSpeed = round(it * 10f) / 10f
                val avgSpeedString = "$avgSpeed kmph"
                binding.tvAverageSpeed.text = avgSpeedString
            }
        })
        viewModel.totalCalBurned.observe(viewLifecycleOwner, Observer {
            it?.let {
                val totalCal = "${it}kcal"
                binding.tvTotalCalories.text = totalCal
            }
        })
    }

}