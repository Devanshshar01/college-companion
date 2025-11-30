package com.example.collegecompanion.repository

import com.example.collegecompanion.data.TimetableClass
import com.example.collegecompanion.data.TimetableDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Repository for managing timetable data.
 * Provides a clean API for accessing and manipulating class schedule information.
 *
 * Note: DAO methods are blocking, so we wrap them in withContext(Dispatchers.IO)
 * to ensure they don't block the main thread.
 */
class TimetableRepository(private val timetableDao: TimetableDao) {

    val allClasses: Flow<List<TimetableClass>> = timetableDao.getAllClasses()

    fun getClassesByDay(day: Int): Flow<List<TimetableClass>> {
        return timetableDao.getClassesByDay(day)
    }

    /**
     * Get classes for today based on current day of week.
     * @return Flow of today's classes
     */
    fun getTodayClasses(): Flow<List<TimetableClass>> {
        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val dayOfWeek = convertCalendarDayToDbDay(today)
        return timetableDao.getClassesByDay(dayOfWeek)
    }

    suspend fun getClassById(id: Long): TimetableClass? {
        return withContext(Dispatchers.IO) {
            timetableDao.getClassById(id)
        }
    }

    suspend fun insertClass(timetableClass: TimetableClass): Long {
        return withContext(Dispatchers.IO) {
            timetableDao.insertClass(timetableClass)
        }
    }

    suspend fun updateClass(timetableClass: TimetableClass) {
        withContext(Dispatchers.IO) {
            timetableDao.updateClass(timetableClass)
        }
    }

    suspend fun deleteClass(timetableClass: TimetableClass) {
        withContext(Dispatchers.IO) {
            timetableDao.deleteClass(timetableClass)
        }
    }

    /**
     * Convert Calendar day (Sunday=1) to our DB format (Monday=1).
     */
    private fun convertCalendarDayToDbDay(calendarDay: Int): Int {
        return when (calendarDay) {
            Calendar.SUNDAY -> 7
            else -> calendarDay - 1
        }
    }
}
