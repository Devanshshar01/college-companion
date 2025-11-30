package com.example.collegecompanion.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for reminder operations.
 * Provides methods to query, insert, update, and delete reminder entries.
 *
 * Note: Using blocking operations for insert/update/delete to avoid kapt issues.
 * These should be called from coroutine contexts (withContext(Dispatchers.IO)).
 */
@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminders WHERE isCompleted = 0 ORDER BY dateTime ASC")
    fun getActiveReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders ORDER BY dateTime DESC")
    fun getAllReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE id = :id")
    fun getReminderById(id: Long): Reminder?

    @Query("SELECT * FROM reminders WHERE dateTime >= :startTime AND dateTime < :endTime AND isCompleted = 0")
    fun getRemindersInRange(startTime: Long, endTime: Long): Flow<List<Reminder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReminder(reminder: Reminder): Long

    @Update
    fun updateReminder(reminder: Reminder): Int

    @Delete
    fun deleteReminder(reminder: Reminder): Int

    @Query("UPDATE reminders SET isCompleted = 1 WHERE id = :id")
    fun markAsCompleted(id: Long): Int

    @Query("DELETE FROM reminders WHERE isCompleted = 1")
    fun deleteCompletedReminders(): Int
}
