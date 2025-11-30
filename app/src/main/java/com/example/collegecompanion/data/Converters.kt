package com.example.collegecompanion.data

import androidx.room.TypeConverter

/**
 * Type converters for Room database.
 * Handles conversion of custom types (like enums) to database-compatible formats.
 */
class Converters {

    @TypeConverter
    fun fromPriority(priority: Priority): String {
        return priority.name
    }

    @TypeConverter
    fun toPriority(value: String): Priority {
        return try {
            Priority.valueOf(value)
        } catch (e: IllegalArgumentException) {
            Priority.MEDIUM
        }
    }
}
