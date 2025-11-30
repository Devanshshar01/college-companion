package com.example.collegecompanion.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.collegecompanion.data.Reminder
import com.example.collegecompanion.databinding.ItemReminderBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter for displaying reminders in RecyclerView.
 * Handles click events for completing and deleting reminders.
 */
class RemindersAdapter(
    private val onCompleteClick: (Reminder) -> Unit,
    private val onDeleteClick: (Reminder) -> Unit
) : RecyclerView.Adapter<RemindersAdapter.ViewHolder>() {

    private var reminders = listOf<Reminder>()
    private val dateFormat = SimpleDateFormat("MMM dd, hh:mm a", Locale.US)

    class ViewHolder(val binding: ItemReminderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReminderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = reminders[position]
        holder.binding.apply {
            reminderTitle.text = item.title
            reminderDescription.text = item.description.ifEmpty { "No description" }
            reminderTime.text = dateFormat.format(Date(item.dateTime))

            // Set priority indicator
            val priorityColor = when (item.priority) {
                com.example.collegecompanion.data.Priority.HIGH ->
                    android.graphics.Color.parseColor("#EF4444")

                com.example.collegecompanion.data.Priority.MEDIUM ->
                    android.graphics.Color.parseColor("#F59E0B")

                com.example.collegecompanion.data.Priority.LOW ->
                    android.graphics.Color.parseColor("#10B981")
            }
            priorityIndicator.setBackgroundColor(priorityColor)

            // Handle complete button click
            btnComplete.setOnClickListener {
                onCompleteClick(item)
            }

            // Handle delete button click
            btnDelete.setOnClickListener {
                onDeleteClick(item)
            }
        }
    }

    override fun getItemCount() = reminders.size

    /**
     * Update reminders with DiffUtil.
     */
    fun updateReminders(newReminders: List<Reminder>) {
        val diffCallback = ReminderDiffCallback(reminders, newReminders)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        reminders = newReminders
        diffResult.dispatchUpdatesTo(this)
    }

    /**
     * Submit list using DiffUtil (alternative method name).
     */
    fun submitList(newReminders: List<Reminder>) {
        updateReminders(newReminders)
    }

    /**
     * DiffUtil callback for efficient list updates.
     */
    private class ReminderDiffCallback(
        private val oldList: List<Reminder>,
        private val newList: List<Reminder>
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
