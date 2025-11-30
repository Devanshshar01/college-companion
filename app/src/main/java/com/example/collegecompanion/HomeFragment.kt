package com.example.collegecompanion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.collegecompanion.databinding.FragmentHomeBinding
import com.example.collegecompanion.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Home Fragment - Displays today's summary and recent activity.
 * Shows quick overview of classes and reminders for the day.
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: RecentActivityAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeData()
    }

    /**
     * Setup RecyclerView for recent activity display.
     */
    private fun setupRecyclerView() {
        adapter = RecentActivityAdapter(emptyList())
        binding.recyclerRecent.layoutManager = LinearLayoutManager(context)
        binding.recyclerRecent.adapter = adapter
    }

    /**
     * Observe ViewModel data and update UI.
     */
    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            // Observe today's classes
            viewModel.getTodayClasses().collect { classes ->
                val classCount = classes.size

                // Observe today's reminders
                viewModel.getActiveReminders().collect { reminders ->
                    val reminderCount = reminders.filter { reminder ->
                        isToday(reminder.dateTime)
                    }.size

                    // Update summary text
                    binding.summaryText.text = getString(
                        R.string.summary_text,
                        classCount,
                        reminderCount
                    )

                    // Build activity items from classes and reminders
                    val activityItems = buildActivityItems(classes, reminders)
                    updateRecyclerView(activityItems)
                }
            }
        }
    }

    /**
     * Build activity items from classes and reminders.
     */
    private fun buildActivityItems(
        classes: List<com.example.collegecompanion.data.TimetableClass>,
        reminders: List<com.example.collegecompanion.data.Reminder>
    ): List<ActivityItem> {
        val items = mutableListOf<ActivityItem>()

        // Add today's classes
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
        classes.forEach { timetableClass ->
            items.add(
                ActivityItem(
                    title = timetableClass.className,
                    subtitle = "${timetableClass.classType} • Room ${timetableClass.roomNumber}",
                    time = timetableClass.startTime,
                    icon = "class"
                )
            )
        }

        // Add today's reminders
        reminders.filter { isToday(it.dateTime) }.forEach { reminder ->
            items.add(
                ActivityItem(
                    title = reminder.title,
                    subtitle = if (reminder.description.isNotEmpty()) reminder.description else "Reminder",
                    time = timeFormat.format(Date(reminder.dateTime)),
                    icon = "reminder"
                )
            )
        }

        // Sort by time
        items.sortBy { it.time }

        return items
    }

    /**
     * Update RecyclerView with new data.
     */
    private fun updateRecyclerView(items: List<ActivityItem>) {
        if (items.isEmpty()) {
            binding.recyclerRecent.visibility = View.GONE
            binding.emptyStateText.visibility = View.VISIBLE
            binding.emptyStateText.text = getString(R.string.no_activity)
        } else {
            binding.recyclerRecent.visibility = View.VISIBLE
            binding.emptyStateText.visibility = View.GONE
            adapter.updateItems(items)
        }
    }

    /**
     * Check if timestamp is today.
     */
    private fun isToday(timestamp: Long): Boolean {
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_YEAR)
        val todayYear = calendar.get(Calendar.YEAR)

        calendar.timeInMillis = timestamp
        val itemDay = calendar.get(Calendar.DAY_OF_YEAR)
        val itemYear = calendar.get(Calendar.YEAR)

        return today == itemDay && todayYear == itemYear
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

/**
 * Data class for activity items displayed in RecyclerView.
 */
data class ActivityItem(
    val title: String,
    val subtitle: String,
    val time: String,
    val icon: String = "class"
)
