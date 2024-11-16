package com.example.runningapp.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.runningapp.R
import com.example.runningapp.databinding.FragmentStatisticsBinding
import com.example.runningapp.other.TrackingUtility
import com.example.runningapp.ui.viewmodels.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.round

@AndroidEntryPoint
class StatisticsFragment : Fragment() {
    private lateinit var binding:FragmentStatisticsBinding
    private val viewModel: StatisticsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
    }

    @SuppressLint("SetTextI18n")
    private fun subscribeToObservers(){
        viewModel.totalTimeRun.observe(viewLifecycleOwner){
            it?.let {
                val totalTimeRun = TrackingUtility.getFormattedStopWatchTime(it)
                binding.tvTotalTime.text = totalTimeRun
            }
        }

        viewModel.totalDistance.observe(viewLifecycleOwner){
            it?.let {
                val distanceInKm =  it / 1000f
                binding.tvTotalDistance.text = "${(round(distanceInKm * 10f ) / 10f)}km"
            }
        }

        viewModel.totalSpeed.observe(viewLifecycleOwner){
            it?.let {
                val avgSpeedInKm = round(it * 10f) / 10f
                binding.tvAverageSpeed.text = "${avgSpeedInKm}km/h"
            }
        }

        viewModel.totalCaloriesBurned.observe(viewLifecycleOwner){
            it?.let {
                binding.tvTotalCalories.text = "${it}kcal"
            }
        }
    }
}