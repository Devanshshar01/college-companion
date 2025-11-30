package com.example.collegecompanion.repository

import com.example.collegecompanion.data.Reminder
import com.example.collegecompanion.data.ReminderDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Repository for managing reminder data.
 * Provides a clean API for accessing and manipulating reminder information.
 *
 * Note: DAO methods are blocking, so we wrap them in withContext(Dispatchers.IO)
 * to ensure they don't block the main thread.
 */
class ReminderRepository(private val reminderDao: ReminderDao) {

    val activeReminders: Flow<List<Reminder>> = reminderDao.getActiveReminders()
    val allReminders: Flow<List<Reminder>> = reminderDao.getAllReminders()

    /**
     * Get reminders for today.
     * @return Flow of today's reminders
     */
    fun getTodayReminders(): Flow<List<Reminder>> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startOfDay = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val endOfDay = calendar.timeInMillis

        return reminderDao.getRemindersInRange(startOfDay, endOfDay)
    }

    suspend fun getReminderById(id: Long): Reminder? {
        return withContext(Dispatchers.IO) {
            reminderDao.getReminderById(id)
        }
    }

    suspend fun insertReminder(reminder: Reminder): Long {
        return withContext(Dispatchers.IO) {
            reminderDao.insertReminder(reminder)
        }
    }

    suspend fun updateReminder(reminder: Reminder) {
        withContext(Dispatchers.IO) {
            reminderDao.updateReminder(reminder)
        }
    }

    suspend fun deleteReminder(reminder: Reminder) {
        withContext(Dispatchers.IO) {
            reminderDao.deleteReminder(reminder)
        }
    }

    suspend fun markAsCompleted(id: Long) {
        withContext(Dispatchers.IO) {
            reminderDao.markAsCompleted(id)
        }
    }

    suspend fun deleteCompletedReminders() {
        withContext(Dispatchers.IO) {
            reminderDao.deleteCompletedReminders()
        }
    }
}
