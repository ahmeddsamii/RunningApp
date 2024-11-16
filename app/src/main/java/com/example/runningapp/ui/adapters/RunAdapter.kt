package com.example.runningapp.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.runningapp.databinding.ItemRunBinding
import com.example.runningapp.db.RunDto
import com.example.runningapp.diffutils.RunDiffUtil
import com.example.runningapp.other.TrackingUtility
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RunAdapter:ListAdapter<RunDto, RunAdapter.RunViewHolder>(RunDiffUtil()) {
    private lateinit var binding: ItemRunBinding

    class RunViewHolder(val binding: ItemRunBinding): ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemRunBinding.inflate(inflater,parent, false)
        return RunViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val currentRunItem = getItem(position)
        holder.itemView.apply {
            Glide.with(context).load(currentRunItem.img).into(binding.ivRunImage)
            val calender = Calendar.getInstance().apply {
                timeInMillis = currentRunItem.timeStamp
            }
            val dateFormat = SimpleDateFormat("dd.MM.yy",Locale.getDefault())
            binding.tvDate.text = dateFormat.format(calender.time)
            binding.tvAvgSpeed.text = "${currentRunItem.avgSpeedInKMH}km/h"
            binding.tvTime.text = TrackingUtility.getFormattedStopWatchTime(currentRunItem.timeInMillis)
            binding.tvDistance.text = "${currentRunItem.distanceInMeters/1000}km"
            binding.tvCalories.text = "${currentRunItem.caloriesBurned}kcal"


        }
    }
}