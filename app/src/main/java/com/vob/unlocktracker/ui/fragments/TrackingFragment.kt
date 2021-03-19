package com.vob.unlocktracker.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.vob.unlocktracker.R
import com.vob.unlocktracker.databinding.FragmentTrackingBinding
import com.vob.unlocktracker.db.Run
import com.vob.unlocktracker.service.Polyline
import com.vob.unlocktracker.service.TrackingService
import com.vob.unlocktracker.ui.viewmodel.MainViewModel
import com.vob.unlocktracker.util.Constants.ACTION_PAUSE_SERVICE
import com.vob.unlocktracker.util.Constants.ACTION_START_OR_RESUME_SERVICE
import com.vob.unlocktracker.util.Constants.ACTION_STOP_SERVICE
import com.vob.unlocktracker.util.Constants.MAP_ZOOM
import com.vob.unlocktracker.util.Constants.POLYLINE_COLOR
import com.vob.unlocktracker.util.Constants.POLYLINE_WIDTH
import com.vob.unlocktracker.util.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.math.round

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {

    lateinit var binding: FragmentTrackingBinding
    private val viewModel: MainViewModel by viewModels()
    private var map: GoogleMap? = null
    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()
    private var currentTimeMillis = 0L
    private var menu: Menu? = null

    @set:Inject
    var weight = 80f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTrackingBinding.bind(view)
        binding.mapView.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        binding.btnFinishRun.visibility = View.GONE
        binding.btnToggleRun.setOnClickListener {
            toggleRun()
        }

        binding.btnFinishRun.setOnClickListener {
            zoomToSeeWholeTrack()
            endRunAndSaveToDB()
        }

        binding.mapView.getMapAsync{
            map = it
            addAllPolyLines()
        }

        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })

        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints = it
            addLatestPolyline()
            moveCameraAsPerUserPosition()
        })

        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, Observer {
            currentTimeMillis = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(currentTimeMillis, true)
            binding.tvTimer.text = formattedTime
        })
    }

    private fun toggleRun() {
        if (isTracking)
        {
            menu?.getItem(0)?.isVisible = true
            if (menu == null)
                Timber.d("No options menu")
            sendCommandToService(ACTION_PAUSE_SERVICE)
        }
        else
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_tracking_menu, menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (currentTimeMillis > 0L)
            this.menu?.getItem(0)?.isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId)
        {
            R.id.cancelTracking -> showCancelTrackingDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showCancelTrackingDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                .setTitle("Cancel Run")
                .setMessage("Are you sure that you want to cancel the run?")
                .setIcon(R.drawable.ic_baseline_delete_24)
                .setPositiveButton("Yes") { _, _ ->
                    stopRun()
                }
                .setNegativeButton("No") { dialogInterface, _ ->
                    dialogInterface.cancel()
                }
                .create()
        dialog.show()
    }

    private fun stopRun() {
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if (!isTracking)
        {
            binding.btnToggleRun.text = "Start"
            binding.btnFinishRun.visibility = View.VISIBLE
        }
        else
        {
            binding.btnToggleRun.text = "Stop"
            menu?.getItem(0)?.isVisible = true
            binding.btnFinishRun.visibility = View.GONE
        }
    }

    private fun moveCameraAsPerUserPosition() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty())
        {
            map?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                            pathPoints.last().last(),
                            MAP_ZOOM
                    )
            )
        }
    }

    private fun zoomToSeeWholeTrack() {
        val bounds = LatLngBounds.builder()
        for (polyline in pathPoints)
        {
            for (pos in polyline)
            {
                bounds.include(pos)
            }
        }

        map?.moveCamera(
                CameraUpdateFactory.newLatLngBounds(
                        bounds.build(),
                        binding.mapView.width,
                        binding.mapView.height,150)
//                        (binding.mapView.height + 0.05f).toInt()

        )
    }

    private fun endRunAndSaveToDB() {
        map?.snapshot {
            var distanceInMeters = 0
            for (polyline in pathPoints)
            {
                distanceInMeters += TrackingUtility.calculatePolylineLength(polyline).toInt()
            }
            val avgSpeed = round((distanceInMeters / 1000f) / (currentTimeMillis / 1000f / 60 / 60) * 10) / 10f
            val dateTimeStamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMeters / 1000f) * weight).toInt()
            val run = Run(
                    it,
                    dateTimeStamp,
                    avgSpeed,
                    distanceInMeters,
                    currentTimeMillis,
                    caloriesBurned
            )
            viewModel.insertRun(run)
            Snackbar.make(
                    requireActivity().findViewById(R.id.rootView),
                    "Run saved successfully",
                    Snackbar.LENGTH_LONG
            ).show()
            stopRun()
        }
    }

    private fun addAllPolyLines() {
        for (polyline in pathPoints)
        {
            val polylineOptions = PolylineOptions()
                    .color(POLYLINE_COLOR)
                    .width(POLYLINE_WIDTH)
                    .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyline() {
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1)
        {
            val preLastLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()
            val polylineOptions = PolylineOptions()
                    .color(POLYLINE_COLOR)
                    .width(POLYLINE_WIDTH)
                    .add(preLastLng)
                    .add(lastLatLng)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun sendCommandToService(action: String) =
            Intent(requireContext(), TrackingService::class.java).also {
                it.action = action
                requireContext().startService(it)
            }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }
}