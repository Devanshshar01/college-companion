package com.example.collegecompanion

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.collegecompanion.adapters.RemindersAdapter
import com.example.collegecompanion.data.Priority
import com.example.collegecompanion.data.Reminder
import com.example.collegecompanion.viewmodel.MainViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment for managing reminders.
 * Displays all reminders and allows users to add, edit, or delete them.
 */
class RemindersFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateText: TextView
    private lateinit var fabAddReminder: FloatingActionButton
    private lateinit var adapter: RemindersAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reminders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        recyclerView = view.findViewById(R.id.recycler_reminders)
        emptyStateText = view.findViewById(R.id.empty_state_text)
        fabAddReminder = view.findViewById(R.id.fab_add_reminder)

        // Setup RecyclerView
        setupRecyclerView()

        // Observe reminders
        observeReminders()

        // Setup FAB click listener
        fabAddReminder.setOnClickListener {
            showAddReminderDialog()
        }
    }

    private fun setupRecyclerView() {
        adapter = RemindersAdapter(
            onCompleteClick = { reminder ->
                completeReminder(reminder)
            },
            onDeleteClick = { reminder ->
                deleteReminder(reminder)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun observeReminders() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allReminders.collect { reminders ->
                if (reminders.isEmpty()) {
                    emptyStateText.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    emptyStateText.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    adapter.submitList(reminders)
                }
            }
        }
    }

    private fun showAddReminderDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_reminder, null)

        // Get views
        val titleInput = dialogView.findViewById<EditText>(R.id.input_reminder_title)
        val descriptionInput = dialogView.findViewById<EditText>(R.id.input_reminder_description)
        val btnPickDate = dialogView.findViewById<android.widget.Button>(R.id.btn_pick_date)
        val btnPickTime = dialogView.findViewById<android.widget.Button>(R.id.btn_pick_time)
        val radioGroup = dialogView.findViewById<android.widget.RadioGroup>(R.id.radio_priority)

        // Variables to store selected date and time
        var selectedDate = ""
        var selectedTime = ""

        // Setup date picker
        btnPickDate.setOnClickListener {
            showDatePicker { date ->
                selectedDate = date
                btnPickDate.text = date
            }
        }

        // Setup time picker
        btnPickTime.setOnClickListener {
            showTimePicker { time ->
                selectedTime = time
                btnPickTime.text = time
            }
        }

        // Build dialog
        AlertDialog.Builder(requireContext())
            .setTitle("Add Reminder")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val title = titleInput.text.toString()
                val description = descriptionInput.text.toString()

                // Get priority from radio group
                val priorityStr = when (radioGroup.checkedRadioButtonId) {
                    R.id.radio_low -> "Low"
                    R.id.radio_high -> "High"
                    else -> "Medium"
                }

                if (title.isNotBlank() && selectedDate.isNotBlank() && selectedTime.isNotBlank()) {
                    addReminder(title, description, selectedDate, selectedTime, priorityStr)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please fill all required fields",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = android.app.DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                onDateSelected(dateFormat.format(selectedCalendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showTimePicker(onTimeSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val timePickerDialog = android.app.TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedCalendar.set(Calendar.MINUTE, minute)
                onTimeSelected(timeFormat.format(selectedCalendar.time))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // 24-hour format
        )
        timePickerDialog.show()
    }

    private fun addReminder(
        title: String,
        description: String,
        dateStr: String,
        timeStr: String,
        priorityStr: String
    ) {
        try {
            // Parse date and time
            val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val dateTime = dateTimeFormat.parse("$dateStr $timeStr")

            if (dateTime != null) {
                val priority = when (priorityStr) {
                    "Low" -> Priority.LOW
                    "Medium" -> Priority.MEDIUM
                    "High" -> Priority.HIGH
                    else -> Priority.MEDIUM
                }

                val reminder = Reminder(
                    title = title,
                    description = description,
                    dateTime = dateTime.time, // Convert Date to Long (milliseconds)
                    priority = priority,
                    isCompleted = false
                )

                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.addReminder(reminder)
                    Toast.makeText(
                        requireContext(),
                        "Reminder added successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Error adding reminder: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun completeReminder(reminder: Reminder) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.completeReminder(reminder.id)
            Toast.makeText(
                requireContext(),
                "Reminder completed",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun deleteReminder(reminder: Reminder) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Reminder")
            .setMessage("Are you sure you want to delete this reminder?")
            .setPositiveButton("Delete") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.deleteReminder(reminder)
                    Toast.makeText(
                        requireContext(),
                        "Reminder deleted",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
