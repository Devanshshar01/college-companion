package com.example.collegecompanion.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.collegecompanion.data.TimetableClass
import com.example.collegecompanion.databinding.ItemTimetableClassBinding

/**
 * Adapter for displaying timetable classes in RecyclerView.
 * Uses DiffUtil for efficient updates.
 */
class TimetableAdapter : RecyclerView.Adapter<TimetableAdapter.ViewHolder>() {

    private var classes = listOf<TimetableClass>()

    class ViewHolder(val binding: ItemTimetableClassBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTimetableClassBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = classes[position]
        holder.binding.apply {
            className.text = item.className
            classTime.text = "${item.startTime} - ${item.endTime}"
            classRoom.text = "Room ${item.roomNumber}"
            classType.text = item.classType
        }
    }

    override fun getItemCount() = classes.size

    /**
     * Update classes with DiffUtil.
     */
    fun updateClasses(newClasses: List<TimetableClass>) {
        val diffCallback = TimetableDiffCallback(classes, newClasses)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        classes = newClasses
        diffResult.dispatchUpdatesTo(this)
    }

    /**
     * DiffUtil callback for efficient list updates.
     */
    private class TimetableDiffCallback(
        private val oldList: List<TimetableClass>,
        private val newList: List<TimetableClass>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
