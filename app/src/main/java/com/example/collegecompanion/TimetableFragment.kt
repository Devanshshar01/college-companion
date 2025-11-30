package com.example.collegecompanion

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.collegecompanion.adapters.TimetableAdapter
import com.example.collegecompanion.data.TimetableClass
import com.example.collegecompanion.databinding.DialogAddClassBinding
import com.example.collegecompanion.databinding.FragmentTimetableBinding
import com.example.collegecompanion.viewmodel.MainViewModel
import kotlinx.coroutines.launch

/**
 * Timetable Fragment - Displays weekly class schedule.
 * Shows all scheduled classes organized by day and time.
 * Allows users to add new classes via FAB.
 */
class TimetableFragment : Fragment() {

    private var _binding: FragmentTimetableBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: TimetableAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimetableBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeData()
        setupFab()
    }

    /**
     * Setup RecyclerView for timetable display.
     */
    private fun setupRecyclerView() {
        adapter = TimetableAdapter()
        binding.recyclerTimetable.layoutManager = LinearLayoutManager(context)
        binding.recyclerTimetable.adapter = adapter
    }

    /**
     * Observe ViewModel data and update UI.
     */
    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getTodayClasses().collect { classes ->
                if (classes.isEmpty()) {
                    binding.recyclerTimetable.visibility = View.GONE
                    binding.emptyStateText.visibility = View.VISIBLE
                } else {
                    binding.recyclerTimetable.visibility = View.VISIBLE
                    binding.emptyStateText.visibility = View.GONE
                    adapter.updateClasses(classes)
                }
            }
        }
    }

    /**
     * Setup FAB to add new class.
     */
    private fun setupFab() {
        binding.fabAddClass.setOnClickListener {
            showAddClassDialog()
        }
    }

    /**
     * Show dialog to add a new class.
     */
    private fun showAddClassDialog() {
        val dialogBinding = DialogAddClassBinding.inflate(layoutInflater)

        // Setup day spinner
        val days =
            arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, days)
        dialogBinding.spinnerDay.adapter = adapter

        AlertDialog.Builder(requireContext())
            .setTitle("Add New Class")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { _, _ ->
                val className = dialogBinding.inputClassName.text.toString()
                val instructor = dialogBinding.inputInstructor.text.toString()
                val room = dialogBinding.inputRoom.text.toString()
                val dayIndex = dialogBinding.spinnerDay.selectedItemPosition + 1 // 1-based
                val startTime = dialogBinding.inputStartTime.text.toString()
                val endTime = dialogBinding.inputEndTime.text.toString()
                val classType = dialogBinding.inputClassType.text.toString()

                if (className.isBlank() || startTime.isBlank() || endTime.isBlank()) {
                    Toast.makeText(context, "Please fill required fields", Toast.LENGTH_SHORT)
                        .show()
                    return@setPositiveButton
                }

                val newClass = TimetableClass(
                    className = className,
                    instructor = instructor,
                    roomNumber = room,
                    dayOfWeek = dayIndex,
                    startTime = startTime,
                    endTime = endTime,
                    classType = classType
                )

                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.addClass(newClass)
                    Toast.makeText(context, "Class added successfully!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
