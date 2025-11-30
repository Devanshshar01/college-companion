package com.example.collegecompanion.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.collegecompanion.ai.GeminiService
import com.example.collegecompanion.ai.VoiceIntent
import com.example.collegecompanion.data.AppDatabase
import com.example.collegecompanion.data.Reminder
import com.example.collegecompanion.data.TimetableClass
import com.example.collegecompanion.repository.ReminderRepository
import com.example.collegecompanion.repository.TimetableRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Main ViewModel for handling voice interactions and data management.
 * Coordinates between voice engine, AI service, and data repositories.
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val timetableRepository = TimetableRepository(database.timetableDao())
    private val reminderRepository = ReminderRepository(database.reminderDao())
    private val geminiService = GeminiService()

    // LiveData for UI state
    private val _voiceResponse = MutableLiveData<String>()
    val voiceResponse: LiveData<String> = _voiceResponse

    private val _isProcessing = MutableLiveData<Boolean>()
    val isProcessing: LiveData<Boolean> = _isProcessing

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> = _successMessage

    // Cached data for quick access
    private var cachedTodayClasses: List<TimetableClass>? = null
    private var cachedActiveReminders: List<Reminder>? = null

    init {
        // Load data into cache on initialization
        loadCachedData()
    }

    /**
     * Load frequently accessed data into memory for faster responses.
     */
    private fun loadCachedData() {
        viewModelScope.launch {
            try {
                cachedTodayClasses = timetableRepository.getTodayClasses().first()
                cachedActiveReminders = reminderRepository.activeReminders.first()
            } catch (e: Exception) {
                // Silently handle, will retry on next access
            }
        }
    }

    /**
     * Process user voice command and generate response.
     * Main entry point for voice interaction pipeline.
     */
    fun processVoiceCommand(userInput: String) {
        viewModelScope.launch {
            try {
                _isProcessing.value = true
                _errorMessage.value = null

                // Extract intent for quick routing
                val intent = geminiService.extractIntent(userInput)

                // Build context for AI
                val context = buildContext(intent)

                // Get AI response
                val result = geminiService.processVoiceCommand(userInput, context)

                result.fold(
                    onSuccess = { response ->
                        _voiceResponse.value = response
                    },
                    onFailure = { error ->
                        val fallbackResponse = geminiService.getFallbackResponse(error as Exception)
                        _voiceResponse.value = fallbackResponse
                        _errorMessage.value = "AI processing failed: ${error.message}"
                    }
                )

            } catch (e: Exception) {
                _errorMessage.value = "Error processing command: ${e.message}"
                _voiceResponse.value = "I encountered an error. Please try again."
            } finally {
                _isProcessing.value = false
            }
        }
    }

    /**
     * Build context string based on intent for better AI responses.
     */
    private suspend fun buildContext(intent: VoiceIntent): String {
        return when (intent) {
            VoiceIntent.QUERY_TODAY_CLASSES -> {
                val classes = cachedTodayClasses ?: timetableRepository.getTodayClasses().first()
                if (classes.isEmpty()) {
                    "The user has no classes scheduled for today."
                } else {
                    val classInfo = classes.joinToString("\n") {
                        "${it.className} at ${it.startTime} in ${it.roomNumber}"
                    }
                    "Today's classes:\n$classInfo"
                }
            }

            VoiceIntent.QUERY_REMINDERS -> {
                val reminders = cachedActiveReminders ?: reminderRepository.activeReminders.first()
                if (reminders.isEmpty()) {
                    "The user has no active reminders."
                } else {
                    val reminderInfo = reminders.take(5).joinToString("\n") {
                        val dateStr =
                            SimpleDateFormat("MMM dd, hh:mm a", Locale.US).format(Date(it.dateTime))
                        "${it.title} - $dateStr"
                    }
                    "Active reminders:\n$reminderInfo"
                }
            }

            VoiceIntent.QUERY_NEXT_CLASS -> {
                val classes = cachedTodayClasses ?: timetableRepository.getTodayClasses().first()
                val nextClass = findNextClass(classes)
                if (nextClass != null) {
                    "Next class: ${nextClass.className} at ${nextClass.startTime} in ${nextClass.roomNumber}"
                } else {
                    "No more classes today."
                }
            }

            else -> ""
        }
    }

    /**
     * Find the next upcoming class based on current time.
     */
    private fun findNextClass(classes: List<TimetableClass>): TimetableClass? {
        val currentTime = SimpleDateFormat("HH:mm", Locale.US).format(Date())
        return classes.firstOrNull { it.startTime > currentTime }
    }

    /**
     * Get today's classes - exposed for UI.
     */
    fun getTodayClasses() = timetableRepository.getTodayClasses()

    /**
     * Get active reminders - exposed for UI.
     */
    fun getActiveReminders() = reminderRepository.activeReminders

    /**
     * Get all reminders (including completed) - exposed for UI.
     */
    val allReminders = reminderRepository.allReminders

    /**
     * Get all classes - exposed for UI.
     */
    val allClasses = timetableRepository.allClasses

    /**
     * Mark reminder as completed.
     */
    fun completeReminder(reminderId: Long) {
        viewModelScope.launch {
            try {
                reminderRepository.markAsCompleted(reminderId)
                _successMessage.value = "Reminder completed"
                loadCachedData() // Refresh cache
            } catch (e: Exception) {
                _errorMessage.value = "Failed to complete reminder: ${e.message}"
            }
        }
    }

    /**
     * Add a new reminder.
     */
    fun addReminder(reminder: Reminder) {
        viewModelScope.launch {
            try {
                reminderRepository.insertReminder(reminder)
                _successMessage.value = "Reminder added successfully"
                loadCachedData() // Refresh cache
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add reminder: ${e.message}"
            }
        }
    }

    /**
     * Delete a reminder.
     */
    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            try {
                reminderRepository.deleteReminder(reminder)
                _successMessage.value = "Reminder deleted"
                loadCachedData() // Refresh cache
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete reminder: ${e.message}"
            }
        }
    }

    /**
     * Add a new timetable class.
     */
    fun addClass(timetableClass: TimetableClass) {
        viewModelScope.launch {
            try {
                timetableRepository.insertClass(timetableClass)
                _successMessage.value = "Class added successfully"
                loadCachedData() // Refresh cache
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add class: ${e.message}"
            }
        }
    }

    /**
     * Delete a timetable class.
     */
    fun deleteClass(timetableClass: TimetableClass) {
        viewModelScope.launch {
            try {
                timetableRepository.deleteClass(timetableClass)
                _successMessage.value = "Class deleted"
                loadCachedData() // Refresh cache
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete class: ${e.message}"
            }
        }
    }

    /**
     * Clear error message after it's been shown.
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    /**
     * Clear success message after it's been shown.
     */
    fun clearSuccessMessage() {
        _successMessage.value = null
    }
}
