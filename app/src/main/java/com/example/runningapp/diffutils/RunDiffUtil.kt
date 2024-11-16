package com.example.runningapp.diffutils

import androidx.recyclerview.widget.DiffUtil
import com.example.runningapp.db.RunDto

class RunDiffUtil:DiffUtil.ItemCallback<RunDto>() {
    override fun areItemsTheSame(oldItem: RunDto, newItem: RunDto): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RunDto, newItem: RunDto): Boolean {
        return oldItem == newItem
    }
}