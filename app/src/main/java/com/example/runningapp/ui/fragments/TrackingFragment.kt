package com.example.runningapp.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.runningapp.R
import com.example.runningapp.databinding.FragmentTrackingBinding
import com.example.runningapp.db.RunDto
import com.example.runningapp.other.Constants.ACTION_PAUSE_SERVICE
import com.example.runningapp.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runningapp.other.Constants.ACTION_STOP_SERVICE
import com.example.runningapp.other.Constants.MAP_ZOOM
import com.example.runningapp.other.Constants.NOTIFICATION_PERMISSION_REQUEST_CODE
import com.example.runningapp.other.Constants.POLYLINE_COLOR
import com.example.runningapp.other.Constants.POLYLINE_WIDTH
import com.example.runningapp.other.TrackingUtility
import com.example.runningapp.services.TrackingServices
import com.example.runningapp.services.polyLine
import com.example.runningapp.ui.viewmodels.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import kotlin.math.round

@AndroidEntryPoint
class TrackingFragment : Fragment(), EasyPermissions.PermissionCallbacks {
    private lateinit var binding:FragmentTrackingBinding
    private var map:GoogleMap? = null

    private var isTracking = false
    private var pathPoint = mutableListOf<polyLine>()
    private var currentTimeInMillis = 0L
    private var menu:Menu? = null
    private var weight = 80f

    private val viewModel: MainViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        binding = FragmentTrackingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapView.onCreate(savedInstanceState)

        binding.btnToggleRun.setOnClickListener{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestNotificationPermission()
            }else{
                toggleRun()
            }
        }

        binding.btnFinishRun.setOnClickListener{
            zoomToSeeTheWholeTracking()
            finishRunAndSaveToDatabase()
        }

        binding.mapView.getMapAsync {
            map = it
            addAllPolyLinesInCaseOfConfigurationChange()
        }

        subscribeToObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.tool_bar_tracking_menu, menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (currentTimeInMillis>0){
            this.menu?.getItem(0)?.isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.close_tracking ->{
                showConfirmationForCancelRun()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    @SuppressLint("SuspiciousIndentation")
    private fun showConfirmationForCancelRun(){
        val alert = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Cancel the Run?")
            .setMessage("Are you sure you want to cancel this run and delete all its data?")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("yes"){_, _->
                stopRun()
            }
            .setNegativeButton("No"){dialogInterface,_ ->
                dialogInterface.cancel()
            }
            .create()

            alert.show()
    }


    private fun stopRun(){
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }



    private fun subscribeToObservers(){
        TrackingServices.isTracking.observe(viewLifecycleOwner){
            updateTracking(it)
        }

        TrackingServices.pathPoints.observe(viewLifecycleOwner){
            pathPoint = it
            addLatestPolyLine()
            moveCameraToUser()
        }

        TrackingServices.timeRunInMillis.observe(viewLifecycleOwner){
            currentTimeInMillis = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(currentTimeInMillis,true)
            binding.tvTimer.text = formattedTime
        }
    }

    private fun toggleRun(){
        if (isTracking){
            menu?.getItem(0)?.isVisible = true
            sendCommandToService(ACTION_PAUSE_SERVICE)
        }else{
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateTracking(isTracking:Boolean){
        this.isTracking = isTracking
        if (!isTracking){
            binding.btnToggleRun.text = "Start"
            binding.btnFinishRun.visibility = View.VISIBLE
        }else{
            binding.btnToggleRun.text = "Stop"
            menu?.getItem(0)?.isVisible = true
            binding.btnFinishRun.visibility = View.GONE
        }
    }

    private fun moveCameraToUser(){
        if (pathPoint.isNotEmpty() && pathPoint.last().isNotEmpty()){
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoint.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }


    private fun zoomToSeeTheWholeTracking(){
        val bounds = LatLngBounds.Builder()
        for (polyline in pathPoint){
            for (position in polyline){
                bounds.include(position)
            }
        }

        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                binding.mapView.width,
                binding.mapView.height,
                (binding.mapView.height*0.05f).toInt()
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun finishRunAndSaveToDatabase(){
        map?.snapshot { bitmap ->
            var distanceInMeters = 0
            for(polyline in pathPoint){
                distanceInMeters += TrackingUtility.calculateTotalDistance(polyline).toInt()
            }
            var avgSpeed = round((distanceInMeters / 1000L) / (currentTimeInMillis / 1000f / 60 / 60) * 10) /10f
            val timeStamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMeters / 1000f) * weight).toInt()
            val run = RunDto(bitmap, timeStamp,avgSpeed,distanceInMeters,currentTimeInMillis, caloriesBurned)
            viewModel.insertNewRun(run)
            Snackbar.make(requireView(), "Run Saved Successfully", Snackbar.LENGTH_LONG).show()
            stopRun()
        }
    }

    private fun addAllPolyLinesInCaseOfConfigurationChange(){
        for (polyLine in pathPoint){
            val polyLineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyLine)
            map?.addPolyline(polyLineOptions)
        }
    }

    private fun addLatestPolyLine(){
        if (pathPoint.isNotEmpty() && pathPoint.last().size > 1 ){
            val preLastLatLng = pathPoint.last()[pathPoint.last().size - 2]
            val lastLatLng = pathPoint.last().last()
            val polyLineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)

            map?.addPolyline(polyLineOptions)
        }
    }

    private fun sendCommandToService (action:String) =
        Intent(requireContext(), TrackingServices::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!EasyPermissions.hasPermissions(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                )) {
                EasyPermissions.requestPermissions(
                    this,
                    "Notification permission is required for this feature",
                    NOTIFICATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }else{
                toggleRun()
            }
        }
    }

    // Add permission callback
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            AppSettingsDialog.Builder(this).build().show()
        }else{
            requestNotificationPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }
}