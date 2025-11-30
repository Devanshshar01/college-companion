package com.example.collegecompanion.util

import com.example.collegecompanion.data.Priority
import com.example.collegecompanion.data.Reminder
import com.example.collegecompanion.data.TimetableClass
import java.util.*

/**
 * Utility class for generating sample data for testing and demonstration.
 * This helps populate the app with realistic data for the demo.
 */
object SampleDataGenerator {

    /**
     * Generate sample timetable classes for the week.
     */
    fun generateSampleClasses(): List<TimetableClass> {
        return listOf(
            // Monday
            TimetableClass(
                className = "Data Structures and Algorithms",
                instructor = "Dr. Smith",
                roomNumber = "CS-302",
                dayOfWeek = 1, // Monday
                startTime = "09:00",
                endTime = "10:30",
                classType = "Lecture",
                color = "#2563EB"
            ),
            TimetableClass(
                className = "Database Management Systems",
                instructor = "Prof. Johnson",
                roomNumber = "CS-401",
                dayOfWeek = 1,
                startTime = "11:00",
                endTime = "12:30",
                classType = "Lecture",
                color = "#10B981"
            ),
            TimetableClass(
                className = "DSA Lab",
                instructor = "Dr. Smith",
                roomNumber = "Lab-4",
                dayOfWeek = 1,
                startTime = "14:00",
                endTime = "16:00",
                classType = "Lab",
                color = "#F59E0B"
            ),

            // Wednesday
            TimetableClass(
                className = "Operating Systems",
                instructor = "Dr. Williams",
                roomNumber = "CS-305",
                dayOfWeek = 3, // Wednesday
                startTime = "09:00",
                endTime = "10:30",
                classType = "Lecture",
                color = "#8B5CF6"
            ),
            TimetableClass(
                className = "Computer Networks",
                instructor = "Prof. Brown",
                roomNumber = "CS-403",
                dayOfWeek = 3,
                startTime = "11:00",
                endTime = "12:30",
                classType = "Lecture",
                color = "#EC4899"
            ),

            // Friday
            TimetableClass(
                className = "Software Engineering",
                instructor = "Dr. Davis",
                roomNumber = "CS-304",
                dayOfWeek = 5, // Friday
                startTime = "10:00",
                endTime = "11:30",
                classType = "Lecture",
                color = "#06B6D4"
            ),
            TimetableClass(
                className = "Machine Learning",
                instructor = "Prof. Wilson",
                roomNumber = "CS-501",
                dayOfWeek = 5,
                startTime = "14:00",
                endTime = "15:30",
                classType = "Lecture",
                color = "#EF4444"
            )
        )
    }

    /**
     * Generate sample reminders for testing.
     */
    fun generateSampleReminders(): List<Reminder> {
        val calendar = Calendar.getInstance()

        return listOf(
            // Today reminders
            Reminder(
                title = "Study DSA Chapter 5",
                description = "Binary Trees and BST",
                dateTime = calendar.apply {
                    set(Calendar.HOUR_OF_DAY, 20)
                    set(Calendar.MINUTE, 0)
                }.timeInMillis,
                priority = Priority.HIGH
            ),
            Reminder(
                title = "Submit DBMS Assignment",
                description = "SQL queries assignment due",
                dateTime = calendar.apply {
                    add(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                }.timeInMillis,
                priority = Priority.HIGH
            ),
            Reminder(
                title = "OS Lab Report",
                description = "Process scheduling report",
                dateTime = calendar.apply {
                    add(Calendar.DAY_OF_MONTH, 2)
                    set(Calendar.HOUR_OF_DAY, 17)
                    set(Calendar.MINUTE, 0)
                }.timeInMillis,
                priority = Priority.MEDIUM
            ),
            Reminder(
                title = "Group Meeting",
                description = "SE project discussion",
                dateTime = calendar.apply {
                    add(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 16)
                    set(Calendar.MINUTE, 0)
                }.timeInMillis,
                priority = Priority.LOW
            )
        )
    }
}
