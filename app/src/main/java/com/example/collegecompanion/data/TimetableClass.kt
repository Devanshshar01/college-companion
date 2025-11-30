package com.example.collegecompanion.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data model representing a class/lecture in the timetable.
 * Stores information about scheduled classes including time, location, and details.
 */
@Entity(tableName = "timetable_classes")
data class TimetableClass(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val className: String,
    val instructor: String = "",
    val roomNumber: String = "",
    val dayOfWeek: Int, // 1 = Monday, 7 = Sunday
    val startTime: String, // Format: "HH:mm"
    val endTime: String, // Format: "HH:mm"
    val classType: String = "Lecture", // Lecture, Lab, Tutorial, etc.
    val color: String = "#2563EB", // Hex color for UI display
    val isActive: Boolean = true
)
