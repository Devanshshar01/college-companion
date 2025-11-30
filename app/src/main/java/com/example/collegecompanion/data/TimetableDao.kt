package com.example.collegecompanion.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for timetable operations.
 * Provides methods to query, insert, update, and delete class entries.
 *
 * Note: Using blocking operations for insert/update/delete to avoid kapt issues.
 * These should be called from coroutine contexts (withContext(Dispatchers.IO)).
 */
@Dao
interface TimetableDao {

    @Query("SELECT * FROM timetable_classes WHERE isActive = 1 ORDER BY dayOfWeek, startTime")
    fun getAllClasses(): Flow<List<TimetableClass>>

    @Query("SELECT * FROM timetable_classes WHERE dayOfWeek = :day AND isActive = 1 ORDER BY startTime")
    fun getClassesByDay(day: Int): Flow<List<TimetableClass>>

    @Query("SELECT * FROM timetable_classes WHERE id = :id")
    fun getClassById(id: Long): TimetableClass?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertClass(timetableClass: TimetableClass): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertClasses(classes: List<TimetableClass>)

    @Update
    fun updateClass(timetableClass: TimetableClass): Int

    @Delete
    fun deleteClass(timetableClass: TimetableClass): Int

    @Query("DELETE FROM timetable_classes")
    fun deleteAllClasses(): Int
}
