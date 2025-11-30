package com.example.collegecompanion.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data model representing a reminder/task.
 * Supports both time-based and date-based reminders with priority levels.
 */
@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val dateTime: Long, // Timestamp in milliseconds
    val priority: Priority = Priority.MEDIUM,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class Priority {
    LOW, MEDIUM, HIGH
}
