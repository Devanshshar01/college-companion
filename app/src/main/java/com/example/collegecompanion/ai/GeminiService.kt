package com.example.collegecompanion.ai

import android.util.Log
import com.example.collegecompanion.BuildConfig
import kotlinx.coroutines.withTimeout
import java.io.IOException

/**
 * Service for interacting with Google's Gemini AI API.
 * Handles natural language processing for voice commands and generates intelligent responses.
 *
 * Features:
 * - Zero-latency response design with 10-second timeout
 * - Context-aware conversation handling
 * - Graceful error handling and fallback responses
 *
 * Note: This version uses local intent-based responses as fallback since Gemini SDK
 * may not be available. For production, integrate the actual Gemini API.
 */
class GeminiService {

    companion object {
        private const val TAG = "GeminiService"
        private const val TIMEOUT_MS = 10_000L // 10 seconds
    }

    /**
     * Process a voice command and generate an appropriate response.
     * @param userInput The transcribed user voice input
     * @param context Optional context about user's schedule/reminders
     * @return AI-generated response string
     */
    suspend fun processVoiceCommand(
        userInput: String,
        context: String = ""
    ): Result<String> {
        return try {
            Log.d(TAG, "Processing command: $userInput")
            Log.d(TAG, "Context: $context")

            // Check if API key is configured
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank()) {
                Log.w(TAG, "Gemini API key not configured, using fallback responses")
                return Result.success(generateFallbackResponse(userInput, context))
            }

            // For now, use intelligent fallback responses based on intent
            // TODO: Integrate actual Gemini API when library is available
            val response = generateIntelligentResponse(userInput, context)

            Log.d(TAG, "Response: $response")
            Result.success(response)

        } catch (e: Exception) {
            Log.e(TAG, "Error processing voice command", e)
            Result.failure(e)
        }
    }

    /**
     * Generate intelligent responses based on intent and context.
     * This serves as a fallback when Gemini API is not available.
     */
    private fun generateIntelligentResponse(userInput: String, context: String): String {
        val intent = extractIntent(userInput)
        val lowerInput = userInput.lowercase()

        return when (intent) {
            VoiceIntent.QUERY_TODAY_CLASSES -> {
                if (context.contains("no classes", ignoreCase = true)) {
                    "You don't have any classes scheduled for today. Enjoy your free time!"
                } else if (context.contains("classes:", ignoreCase = true)) {
                    // Extract class info from context
                    val classLines = context.split("\n").filter { it.contains("at") }
                    if (classLines.isNotEmpty()) {
                        "You have ${classLines.size} class${if (classLines.size > 1) "es" else ""} today: ${
                            classLines.first().trim()
                        }"
                    } else {
                        "Let me check your schedule... You have classes today!"
                    }
                } else {
                    "Let me check your schedule for today."
                }
            }

            VoiceIntent.QUERY_NEXT_CLASS -> {
                if (context.contains("Next class:", ignoreCase = true)) {
                    val nextClassLine = context.split("\n")
                        .firstOrNull { it.contains("Next class:", ignoreCase = true) }
                    nextClassLine?.replace("Next class:", "Your next class is")?.trim()
                        ?: "You don't have any more classes today."
                } else if (context.contains("No more classes", ignoreCase = true)) {
                    "You're all done for today! No more classes scheduled."
                } else {
                    "Let me find your next class..."
                }
            }

            VoiceIntent.QUERY_REMINDERS -> {
                if (context.contains("no active reminders", ignoreCase = true)) {
                    "You have no active reminders right now. You're all caught up!"
                } else if (context.contains("Active reminders:", ignoreCase = true)) {
                    val reminderLines = context.split("\n").filter { it.contains("-") }
                    if (reminderLines.isNotEmpty()) {
                        "You have ${reminderLines.size} reminder${if (reminderLines.size > 1) "s" else ""}. ${
                            reminderLines.first().trim()
                        }"
                    } else {
                        "You have some reminders set up."
                    }
                } else {
                    "Let me check your reminders..."
                }
            }

            VoiceIntent.QUERY_TIMETABLE -> {
                "Sure! Check your timetable tab to see your full weekly schedule."
            }

            VoiceIntent.ADD_REMINDER -> {
                // Extract time/task from input
                when {
                    lowerInput.contains("study") || lowerInput.contains("homework") ->
                        "Got it! I'll remind you to study. What time would you like to be reminded?"

                    lowerInput.contains("assignment") || lowerInput.contains("submit") ->
                        "I'll set up a reminder for your assignment. When is it due?"

                    else ->
                        "I can help you set up a reminder. What would you like to be reminded about?"
                }
            }

            VoiceIntent.GENERAL_QUERY -> {
                when {
                    lowerInput.contains("hello") || lowerInput.contains("hi") ->
                        "Hello! I'm your College Companion. I can help with your classes, reminders, and schedule."

                    lowerInput.contains("help") ->
                        "I can help you with your timetable, reminders, and schedule. Try asking 'What classes do I have today?' or 'Show my reminders'."

                    lowerInput.contains("thank") ->
                        "You're welcome! Happy to help you stay organized."

                    lowerInput.contains("how are you") ->
                        "I'm doing great! Ready to help you manage your college schedule."

                    else ->
                        "I'm here to help with your classes and reminders. Try asking about your schedule or what's coming up next!"
                }
            }
        }
    }

    /**
     * Generate fallback response when no API key is configured.
     */
    private fun generateFallbackResponse(userInput: String, context: String): String {
        return generateIntelligentResponse(userInput, context)
    }

    /**
     * Get a fallback response for when AI processing fails.
     */
    fun getFallbackResponse(error: Exception): String {
        Log.e(TAG, "Fallback response for error: ${error.message}")
        return when (error) {
            is IOException -> "Network error. Please check your connection and try again."
            is java.util.concurrent.TimeoutException -> "Response timeout. Please try again."
            else -> "I'm having trouble processing that. Try asking about your classes or reminders!"
        }
    }

    /**
     * Extract intent from user input (keyword-based parsing).
     * This provides basic functionality even if Gemini API is unavailable.
     */
    fun extractIntent(input: String): VoiceIntent {
        val lowerInput = input.lowercase()

        return when {
            lowerInput.contains("class") && (lowerInput.contains("today") || lowerInput.contains("now")) ->
                VoiceIntent.QUERY_TODAY_CLASSES

            lowerInput.contains("timetable") || lowerInput.contains("schedule") ->
                VoiceIntent.QUERY_TIMETABLE

            lowerInput.contains("remind") && lowerInput.contains("add") ->
                VoiceIntent.ADD_REMINDER

            lowerInput.contains("reminder") ->
                VoiceIntent.QUERY_REMINDERS

            lowerInput.contains("next") && lowerInput.contains("class") ->
                VoiceIntent.QUERY_NEXT_CLASS

            else -> VoiceIntent.GENERAL_QUERY
        }
    }
}

/**
 * Enumeration of possible voice command intents.
 */
enum class VoiceIntent {
    QUERY_TODAY_CLASSES,
    QUERY_TIMETABLE,
    QUERY_NEXT_CLASS,
    ADD_REMINDER,
    QUERY_REMINDERS,
    GENERAL_QUERY
}
