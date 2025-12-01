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
            // Class name
            className.text = item.className

            // Time range with icon
            classTime.text = "${item.startTime} - ${item.endTime}"

            // Room number
            classRoom.text = item.roomNumber

            // Class type chip
            classTypeChip.text = item.classType

            // Day abbreviation from day number
            dayShort.text = getDayAbbreviation(item.dayOfWeek)

            // Calculate and display duration
            val durationMinutes = calculateDuration(item.startTime, item.endTime)
            duration.text = "$durationMinutes min"
        }
    }

    /**
     * Get day abbreviation from day number (1-7).
     */
    private fun getDayAbbreviation(dayNumber: Int): String {
        return when (dayNumber) {
            1 -> "MON"
            2 -> "TUE"
            3 -> "WED"
            4 -> "THU"
            5 -> "FRI"
            6 -> "SAT"
            7 -> "SUN"
            else -> "MON"
        }
    }

    /**
     * Calculate duration between start and end times.
     */
    private fun calculateDuration(startTime: String, endTime: String): Int {
        return try {
            val format = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.US)
            val start = format.parse(startTime)
            val end = format.parse(endTime)
            if (start != null && end != null) {
                val diffInMillis = end.time - start.time
                (diffInMillis / (1000 * 60)).toInt() // Convert to minutes
            } else {
                90 // Default duration
            }
        } catch (e: Exception) {
            90 // Default duration on error
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
