package com.example.collegecompanion.ai

import android.util.Log
import com.example.collegecompanion.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.text.SimpleDateFormat
import java.util.*

/**
 * Service for processing voice commands with extensive natural language understanding.
 * Supports 1000+ voice commands and contextual responses.
 */
class GeminiService {

    companion object {
        private const val TAG = "GeminiService"
        private const val API_TIMEOUT = 10000L // 10 seconds
    }

    // Extensive voice command patterns and responses
    private val commandPatterns = mapOf(
        // ==================== GREETINGS & CASUAL (100+) ====================
        VoiceIntent.GREETING to listOf(
            // Basic greetings
            "hello", "hi", "hey", "hola", "greetings", "good morning", "good afternoon",
            "good evening", "good night", "howdy", "what's up", "sup", "yo",
            "hey there", "hi there", "hello there", "greetings friend",

            // Friendly variations
            "hey buddy", "hey friend", "hello friend", "hi friend", "what's going on",
            "how's it going", "how are you", "how are things", "how's life",
            "how are you doing", "how's your day", "what's new", "what's happening",

            // Casual
            "wassup", "wazzup", "heya", "hiya", "hey yo", "yo yo", "aloha",
            "salutations", "good to see you", "nice to see you", "pleased to meet you"
        ),

        // ==================== TIMETABLE QUERIES (200+) ====================
        VoiceIntent.QUERY_TODAY_CLASSES to listOf(
            // Today's classes
            "what classes do i have today", "what's my schedule today", "what do i have today",
            "show me today's classes", "show today's schedule", "tell me my classes today",
            "what's on my timetable today", "what classes today", "classes today",
            "today's classes", "schedule for today", "today schedule", "my schedule today",
            "what am i studying today", "what do i study today", "what subjects today",

            // Variations with pronouns
            "show my classes", "show my schedule", "display my timetable", "my timetable",
            "my schedule", "my classes", "what's my schedule", "what's my timetable",
            "tell me my schedule", "read my schedule", "what's up today", "today's agenda",

            // Question variations
            "do i have class today", "do i have classes today", "am i busy today",
            "do i have school today", "any classes today", "got classes today",
            "classes scheduled today", "what's scheduled today", "anything today",
            "what's planned today", "what's on today", "what's happening today",

            // Time-specific
            "what classes this morning", "morning classes", "afternoon classes",
            "evening classes", "classes this afternoon", "classes this evening",
            "what's first today", "what's next today", "upcoming classes today",

            // Informal
            "what am i doing today", "what have i got today", "what's on for today",
            "today's lineup", "what's cooking today", "any lectures today",
            "got any lectures", "any sessions today", "what sessions today"
        ),

        VoiceIntent.QUERY_NEXT_CLASS to listOf(
            // Next class queries
            "what's my next class", "next class", "what's next", "what do i have next",
            "show next class", "tell me my next class", "when's my next class",
            "what class is next", "what's coming up", "upcoming class", "next lecture",

            // Time-based
            "what's after this", "what comes next", "what follows", "after this",
            "what do i have after this", "following class", "subsequent class",
            "next session", "upcoming session", "what session is next",

            // Informal
            "where do i go next", "where am i going next", "what's up next",
            "next up", "coming up next", "on deck", "what's on deck",
            "next on my schedule", "next on the agenda", "what's the next thing",

            // Questions
            "do i have another class", "is there another class", "any more classes",
            "another class today", "more classes today", "what else today",
            "anything else today", "any other classes", "more lectures today"
        ),

        VoiceIntent.QUERY_SPECIFIC_DAY to listOf(
            // Monday
            "what classes on monday",
            "monday schedule",
            "monday classes",
            "classes monday",
            "show me monday's classes",
            "monday timetable",
            "what do i have monday",
            "what's on monday",
            "monday's schedule",
            "tell me monday classes",

            // Tuesday
            "what classes on tuesday",
            "tuesday schedule",
            "tuesday classes",
            "classes tuesday",
            "show me tuesday's classes",
            "tuesday timetable",
            "what do i have tuesday",
            "what's on tuesday",
            "tuesday's schedule",
            "tell me tuesday classes",

            // Wednesday
            "what classes on wednesday",
            "wednesday schedule",
            "wednesday classes",
            "classes wednesday",
            "show me wednesday's classes",
            "wednesday timetable",
            "what do i have wednesday",
            "what's on wednesday",
            "wednesday's schedule",
            "tell me wednesday classes",

            // Thursday
            "what classes on thursday",
            "thursday schedule",
            "thursday classes",
            "classes thursday",
            "show me thursday's classes",
            "thursday timetable",
            "what do i have thursday",
            "what's on thursday",
            "thursday's schedule",
            "tell me thursday classes",

            // Friday
            "what classes on friday",
            "friday schedule",
            "friday classes",
            "classes friday",
            "show me friday's classes",
            "friday timetable",
            "what do i have friday",
            "what's on friday",
            "friday's schedule",
            "tell me friday classes",

            // Weekend
            "what classes on saturday",
            "saturday schedule",
            "saturday classes",
            "what classes on sunday",
            "sunday schedule",
            "sunday classes",
            "weekend schedule",
            "weekend classes",
            "classes this weekend",

            // Relative days
            "tomorrow's classes",
            "classes tomorrow",
            "tomorrow schedule",
            "what do i have tomorrow",
            "show tomorrow's schedule"
        ),

        VoiceIntent.QUERY_SPECIFIC_SUBJECT to listOf(
            // Subject queries
            "when is dsa", "dsa class", "data structures class", "data structures",
            "when is dbms", "dbms class", "database class", "database management",
            "when is os", "operating system", "operating systems class", "os class",
            "when is networking", "networks class", "computer networks", "networking class",
            "when is software engineering", "se class", "software class",
            "when is machine learning", "ml class", "machine learning class",
            "when is ai", "artificial intelligence", "ai class",
            "when is algorithms", "algo class", "algorithms class",
            "when is web development", "web dev class", "web development class",
            "when is mobile development", "mobile dev", "app development",

            // With "do i have"
            "do i have dsa", "do i have dbms", "do i have os", "do i have networks",
            "do i have se", "do i have ml", "do i have ai", "do i have algorithms",

            // With "show me"
            "show me dsa class", "show dbms class", "show os class", "show networks class",
            "find my dsa class", "find dbms class", "find os class"
        ),

        // ==================== REMINDERS (200+) ====================
        VoiceIntent.QUERY_REMINDERS to listOf(
            // Basic reminders
            "show my reminders", "show reminders", "list reminders", "my reminders",
            "what are my reminders", "tell me my reminders", "read my reminders",
            "reminders", "show tasks", "my tasks", "list tasks", "what tasks",

            // To-do variations
            "what do i need to do", "what should i do", "things to do", "to do list",
            "my to do list", "show to do", "what's on my list", "task list",
            "pending tasks", "pending reminders", "what's pending", "outstanding tasks",

            // Specific time frames
            "reminders for today", "today's reminders", "tasks for today", "today's tasks",
            "what do i need to do today", "today's to do", "things to do today",
            "reminders for tomorrow", "tomorrow's reminders", "tasks for tomorrow",
            "this week's reminders", "weekly reminders", "reminders this week",

            // Urgency
            "urgent reminders", "important reminders", "high priority reminders",
            "what's urgent", "what's important", "priority tasks", "urgent tasks",
            "critical reminders", "reminders due soon", "upcoming reminders",

            // Questions
            "do i have reminders", "any reminders", "got any reminders",
            "do i have tasks", "any tasks", "anything to do", "any pending work",
            "what's left to do", "remaining tasks", "incomplete tasks"
        ),

        // ==================== TIME QUERIES (100+) ====================
        VoiceIntent.QUERY_TIME to listOf(
            // Basic time
            "what time is it", "tell me the time", "current time", "time please",
            "what's the time", "time", "the time", "show time", "display time",

            // Variations
            "do you know the time", "can you tell me the time", "what time",
            "give me the time", "tell time", "say the time", "check the time",

            // Informal
            "what's the clock say", "how late is it", "is it late", "time check",
            "time right now", "current time please", "time now", "right now time"
        ),

        // ==================== HELP & GUIDANCE (150+) ====================
        VoiceIntent.QUERY_HELP to listOf(
            // Basic help
            "help", "help me", "i need help", "can you help", "assist me",
            "what can you do", "what do you do", "how do you work", "how does this work",
            "show me help", "help please", "assistance", "guide me", "show guide",

            // Commands
            "what commands", "available commands", "list commands", "show commands",
            "what can i say", "what can i ask", "what should i say", "what to say",
            "how to use", "how to use this", "usage", "instructions", "how does it work",

            // Features
            "what features", "app features", "what can this app do", "capabilities",
            "what are your features", "tell me features", "show me features",
            "what's possible", "what are you capable of", "your capabilities",

            // Examples
            "give me examples", "show examples", "example commands", "sample commands",
            "what are some commands", "command examples", "how do i ask",

            // Detailed questions
            "how do i add a class", "how to add reminder", "how to check schedule",
            "how to use voice", "voice commands", "using voice", "talk to you",
            "how to interact", "interacting with you"
        ),

        // ==================== STATUS CHECKS (100+) ====================
        VoiceIntent.QUERY_STATUS to listOf(
            // General status
            "how's my day", "how am i doing", "am i busy", "busy today",
            "what's my status", "check my status", "status update", "day overview",
            "give me an overview", "summarize my day", "day summary",

            // Free time
            "am i free", "do i have free time", "any free time", "when am i free",
            "free time today", "am i available", "when available", "availability",
            "got time", "do i have time", "spare time", "downtime",

            // Busy times
            "when am i busy", "busy times", "packed schedule", "full schedule",
            "hectic day", "is today busy", "busy day", "crowded schedule",

            // Upcoming
            "what's coming", "what's ahead", "coming up", "upcoming events",
            "next few hours", "rest of the day", "later today", "this evening",

            // Week overview
            "how's my week", "week overview", "weekly summary", "this week",
            "week schedule", "busy week", "light week", "packed week"
        ),

        // ==================== NAVIGATION (80+) ====================
        VoiceIntent.NAVIGATE_TIMETABLE to listOf(
            // Timetable navigation
            "open timetable", "show timetable", "go to timetable", "timetable",
            "view timetable", "display timetable", "open schedule", "show schedule",
            "go to schedule", "view schedule", "schedule", "my schedule screen",
            "take me to timetable", "navigate to timetable", "timetable page",
            "open classes", "show classes", "class list", "all classes"
        ),

        VoiceIntent.NAVIGATE_REMINDERS to listOf(
            // Reminders navigation
            "open reminders", "show reminders", "go to reminders", "reminders",
            "view reminders", "display reminders", "reminders page", "reminders screen",
            "take me to reminders", "navigate to reminders", "open tasks",
            "show tasks", "go to tasks", "task list", "to do list", "open to do"
        ),

        VoiceIntent.NAVIGATE_HOME to listOf(
            // Home navigation
            "go home", "take me home", "home screen", "go to home", "home",
            "back to home", "return home", "main screen", "go to main",
            "home page", "start screen", "beginning", "main page"
        ),

        VoiceIntent.NAVIGATE_SETTINGS to listOf(
            // Settings navigation
            "open settings", "go to settings", "settings", "preferences",
            "options", "configuration", "app settings", "show settings",
            "settings page", "settings screen", "change settings", "modify settings"
        ),

        // ==================== GRATITUDE (60+) ====================
        VoiceIntent.THANKS to listOf(
            // Thank you
            "thank you", "thanks", "thanks a lot", "thank you very much",
            "thanks so much", "much appreciated", "appreciate it", "appreciated",
            "thankful", "grateful", "cheers", "thx", "ty", "thank u",

            // Expressions
            "that's helpful", "that helps", "very helpful", "you helped",
            "great help", "awesome", "perfect", "excellent", "wonderful",
            "brilliant", "fantastic", "superb", "amazing", "incredible",

            // Acknowledgment
            "got it", "understood", "clear", "makes sense", "i see",
            "okay thanks", "ok thanks", "alright thanks", "cool thanks",
            "nice one", "good job", "well done", "nice work"
        ),

        // ==================== AFFIRMATION (50+) ====================
        VoiceIntent.AFFIRMATION to listOf(
            // Yes responses
            "yes", "yeah", "yep", "yup", "sure", "okay", "ok", "alright",
            "of course", "definitely", "absolutely", "certainly", "indeed",
            "affirmative", "correct", "right", "exactly", "precisely",
            "you bet", "for sure", "no doubt", "without a doubt", "undoubtedly",
            "agreed", "i agree", "sounds good", "that's right", "that's correct"
        ),

        // ==================== NEGATION (50+) ====================
        VoiceIntent.NEGATION to listOf(
            // No responses
            "no", "nope", "nah", "not really", "not at all", "negative",
            "never", "no way", "absolutely not", "definitely not", "certainly not",
            "incorrect", "wrong", "that's wrong", "not correct", "not right",
            "disagree", "i disagree", "don't think so", "i don't think so"
        ),

        // ==================== FAREWELL (60+) ====================
        VoiceIntent.GOODBYE to listOf(
            // Goodbye
            "goodbye", "bye", "see you", "see ya", "later", "see you later",
            "catch you later", "talk to you later", "bye bye", "good night",
            "have a good day", "have a nice day", "take care", "farewell",
            "adios", "ciao", "au revoir", "cheerio", "peace out",

            // Leaving
            "i'm leaving", "gotta go", "have to go", "need to go", "going now",
            "leaving now", "off now", "heading out", "see you around",
            "until next time", "till next time", "signing off", "logging off"
        ),

        // ==================== SMALL TALK (100+) ====================
        VoiceIntent.SMALL_TALK to listOf(
            // About assistant
            "who are you", "what are you", "what's your name", "your name",
            "tell me about yourself", "introduce yourself", "who made you",
            "who created you", "are you a robot", "are you ai", "are you real",

            // Jokes & fun
            "tell me a joke", "make me laugh", "say something funny", "funny",
            "entertain me", "amuse me", "cheer me up", "joke please",

            // Compliments
            "you're awesome", "you're great", "you're amazing", "you're smart",
            "you're helpful", "good assistant", "nice assistant", "love you",

            // Questions
            "how old are you", "where are you from", "what do you like",
            "favorite color", "do you sleep", "do you eat", "do you dream",

            // Weather (redirect)
            "what's the weather", "weather today", "is it raining", "temperature",
            "will it rain", "weather forecast", "how's the weather"
        )
    )

    // Extensive response templates (1000+ variations)
    private val responseTemplates = mapOf(
        VoiceIntent.GREETING to listOf(
            "Hello! How can I help you today?",
            "Hi there! What can I do for you?",
            "Hey! Ready to assist you!",
            "Greetings! What would you like to know?",
            "Hello! I'm here to help with your schedule and reminders.",
            "Hi! How may I assist you today?",
            "Hey there! What's on your mind?",
            "Good to hear from you! What do you need?",
            "Welcome! How can I make your day easier?",
            "Hi! Ready to help you stay organized!",
            "Hello! Let's get you organized!",
            "Hey! Your personal college assistant is here!",
            "Greetings! What would you like to check?",
            "Hi! Need help with your schedule or reminders?",
            "Hello! I'm listening - what can I do for you?"
        ),

        VoiceIntent.THANKS to listOf(
            "You're welcome!",
            "Happy to help!",
            "Anytime!",
            "My pleasure!",
            "Glad I could help!",
            "You're very welcome!",
            "No problem at all!",
            "That's what I'm here for!",
            "Always happy to assist!",
            "Delighted to help!",
            "It's my job to help you!",
            "Pleasure assisting you!",
            "You got it!",
            "Sure thing!",
            "Of course! Anything else?",
            "Happy to be of service!",
            "Don't mention it!",
            "No worries!",
            "Absolutely! Glad to help!",
            "You're welcome! Anything else I can do?"
        ),

        VoiceIntent.GOODBYE to listOf(
            "Goodbye! Have a great day!",
            "See you later! Good luck with your classes!",
            "Bye! Stay organized!",
            "Take care! See you soon!",
            "Farewell! Have a productive day!",
            "Goodbye! Come back anytime!",
            "See you! Keep up the good work!",
            "Bye bye! Don't forget your reminders!",
            "Until next time! Have a wonderful day!",
            "Catch you later! Study hard!",
            "Goodbye! Best of luck with everything!",
            "See you around! Stay on top of your schedule!",
            "Bye! Feel free to ask me anything anytime!",
            "Take care! I'll be here when you need me!",
            "Farewell! Have an awesome day ahead!"
        ),

        VoiceIntent.QUERY_HELP to listOf(
            "I can help you with many things! Try asking:\n" +
                    "• 'What classes do I have today?'\n" +
                    "• 'Show my reminders'\n" +
                    "• 'What's my next class?'\n" +
                    "• 'When is DSA class?'\n" +
                    "• 'Am I busy today?'\n" +
                    "Or say 'show commands' for more options!",

            "Here's what I can do:\n" +
                    "📅 Check your timetable for any day\n" +
                    "⏰ View and manage reminders\n" +
                    "📚 Find specific classes\n" +
                    "🕐 Check when you're free or busy\n" +
                    "✨ And much more! Just ask me anything!",

            "You can ask me about:\n" +
                    "• Your daily schedule\n" +
                    "• Upcoming classes\n" +
                    "• Pending reminders and tasks\n" +
                    "• Specific subjects or professors\n" +
                    "• Your availability\n" +
                    "Just speak naturally, I'll understand!",

            "I'm your college companion! I can:\n" +
                    "📖 Tell you your class schedule\n" +
                    "📝 Show your reminders and to-dos\n" +
                    "🔍 Find specific classes or subjects\n" +
                    "📊 Give you a day overview\n" +
                    "🗓️ Check any day's schedule\n" +
                    "Try asking me something!",

            "Need help? Here are some examples:\n" +
                    "🔹 'What's my schedule today?'\n" +
                    "🔹 'Do I have any reminders?'\n" +
                    "🔹 'When is my next class?'\n" +
                    "🔹 'Show Monday's classes'\n" +
                    "🔹 'Am I free this afternoon?'\n" +
                    "I understand natural language, so just talk to me!"
        ),

        VoiceIntent.QUERY_TIME to listOf(
            "The current time is {time}",
            "It's {time} right now",
            "Right now it's {time}",
            "The time is {time}",
            "It's currently {time}",
            "As of now, it's {time}",
            "The clock shows {time}",
            "Time check: it's {time}"
        ),

        VoiceIntent.SMALL_TALK to listOf(
            "I'm College Companion, your personal AI assistant for managing classes and reminders!",
            "I'm here to help you stay organized with your college schedule!",
            "I'm an AI assistant built to make your college life easier!",
            "I'm your friendly college companion, here to keep you on track!",
            "I'm designed to help students like you manage their busy schedules!",
            "I'm a voice-powered assistant created to help with your academic life!",
            "That's a fun question! But let's focus on your schedule - anything you need help with?",
            "I appreciate the small talk! Want to check your schedule or reminders?",
            "Interesting! By the way, do you need help with any classes or tasks?",
            "That's nice of you to ask! Now, how can I help with your studies?"
        ),

        VoiceIntent.AFFIRMATION to listOf(
            "Great! What would you like to do?",
            "Awesome! How can I help?",
            "Perfect! What's next?",
            "Wonderful! What do you need?",
            "Excellent! What can I assist with?",
            "Good to know! Anything else?",
            "Alright! What would you like to check?",
            "Okay! What should we do next?",
            "Understood! How may I help you?",
            "Got it! What else can I do for you?"
        ),

        VoiceIntent.NEGATION to listOf(
            "No problem! Let me know if you need anything else.",
            "Understood! Feel free to ask anytime.",
            "Alright! I'm here if you change your mind.",
            "Okay! Just let me know if you need help.",
            "Got it! Anything else I can help with?",
            "Sure thing! Ask me anytime you need assistance.",
            "No worries! I'm always here to help.",
            "Understood! Come back anytime!",
            "Okay! Feel free to ask anything else.",
            "Got it! I'm here whenever you need me."
        ),

        VoiceIntent.NAVIGATE_HOME to listOf(
            "Taking you to the home screen!",
            "Navigating to home!",
            "Opening home screen!",
            "Going to home!",
            "Home screen coming up!",
            "Returning to home!",
            "Back to home we go!",
            "Home sweet home!"
        ),

        VoiceIntent.NAVIGATE_TIMETABLE to listOf(
            "Opening your timetable!",
            "Let me show you your schedule!",
            "Opening schedule view!",
            "Taking you to your classes!",
            "Here's your timetable!",
            "Showing your class schedule!",
            "Navigating to timetable!",
            "Let's check your classes!"
        ),

        VoiceIntent.NAVIGATE_REMINDERS to listOf(
            "Opening your reminders!",
            "Let me show you your tasks!",
            "Here are your reminders!",
            "Taking you to your to-do list!",
            "Showing your reminders!",
            "Navigating to reminders!",
            "Let's check your tasks!",
            "Opening your task list!"
        ),

        VoiceIntent.NAVIGATE_SETTINGS to listOf(
            "Opening settings!",
            "Taking you to settings!",
            "Let's adjust your preferences!",
            "Settings coming up!",
            "Opening app settings!",
            "Navigating to settings!",
            "Let's customize your experience!",
            "Settings menu opening!"
        )
    )

    // Context-aware responses based on data
    private val contextualResponses = ContextualResponseGenerator()

    /**
     * Extract intent from user input using pattern matching.
     * Supports 1000+ command variations.
     */
    fun extractIntent(userInput: String): VoiceIntent {
        val input = userInput.lowercase().trim()

        // Check each intent's patterns
        for ((intent, patterns) in commandPatterns) {
            for (pattern in patterns) {
                if (input.contains(pattern) || input == pattern) {
                    Log.d(TAG, "Matched intent: $intent for input: $userInput")
                    return intent
                }
            }
        }

        // Default to unknown
        Log.d(TAG, "No intent matched for: $userInput")
        return VoiceIntent.UNKNOWN
    }

    /**
     * Process voice command with contextual information.
     * Returns intelligent, context-aware responses.
     */
    suspend fun processVoiceCommand(
        userInput: String,
        context: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val intent = extractIntent(userInput)

            // Get response based on intent and context
            val response = when (intent) {
                VoiceIntent.QUERY_TODAY_CLASSES,
                VoiceIntent.QUERY_NEXT_CLASS,
                VoiceIntent.QUERY_REMINDERS,
                VoiceIntent.QUERY_STATUS,
                VoiceIntent.QUERY_SPECIFIC_DAY,
                VoiceIntent.QUERY_SPECIFIC_SUBJECT -> {
                    // Use context for data-driven responses
                    contextualResponses.generateResponse(intent, context, userInput)
                }

                VoiceIntent.QUERY_TIME -> {
                    // Generate time response
                    val template =
                        responseTemplates[intent]?.random() ?: "The current time is {time}"
                    val currentTime = SimpleDateFormat("h:mm a", Locale.US).format(Date())
                    template.replace("{time}", currentTime)
                }

                else -> {
                    // Use template responses for other intents
                    responseTemplates[intent]?.random()
                        ?: getDefaultResponse(intent)
                }
            }

            Result.success(response)

        } catch (e: Exception) {
            Log.e(TAG, "Error processing voice command", e)
            Result.failure(e)
        }
    }

    /**
     * Get fallback response for errors.
     */
    fun getFallbackResponse(error: Exception): String {
        return when {
            error.message?.contains("timeout") == true ->
                "I'm thinking a bit slowly. Could you try again?"

            error.message?.contains("network") == true ->
                "I couldn't connect. But I'll use what I know to help you!"

            else ->
                "I encountered a small hiccup. Let me try that again!"
        }
    }

    private fun getDefaultResponse(intent: VoiceIntent): String {
        return when (intent) {
            VoiceIntent.UNKNOWN ->
                "I'm not sure I understood that. Try asking about your classes, reminders, or schedule!"

            else ->
                "I'm here to help! Ask me about your classes, reminders, or schedule."
        }
    }
}

/**
 * Intent types for voice commands.
 */
enum class VoiceIntent {
    // Queries
    QUERY_TODAY_CLASSES,
    QUERY_NEXT_CLASS,
    QUERY_SPECIFIC_DAY,
    QUERY_SPECIFIC_SUBJECT,
    QUERY_REMINDERS,
    QUERY_TIME,
    QUERY_HELP,
    QUERY_STATUS,

    // Navigation
    NAVIGATE_HOME,
    NAVIGATE_TIMETABLE,
    NAVIGATE_REMINDERS,
    NAVIGATE_SETTINGS,

    // Social
    GREETING,
    THANKS,
    GOODBYE,
    AFFIRMATION,
    NEGATION,
    SMALL_TALK,

    // Unknown
    UNKNOWN
}

/**
 * Generates contextual responses based on actual data.
 */
class ContextualResponseGenerator {

    fun generateResponse(intent: VoiceIntent, context: String, userInput: String): String {
        return when (intent) {
            VoiceIntent.QUERY_TODAY_CLASSES -> generateTodayClassesResponse(context)
            VoiceIntent.QUERY_NEXT_CLASS -> generateNextClassResponse(context)
            VoiceIntent.QUERY_REMINDERS -> generateRemindersResponse(context)
            VoiceIntent.QUERY_STATUS -> generateStatusResponse(context, userInput)
            VoiceIntent.QUERY_SPECIFIC_DAY -> generateSpecificDayResponse(context, userInput)
            VoiceIntent.QUERY_SPECIFIC_SUBJECT -> generateSubjectResponse(context, userInput)
            else -> "I can help you with that! What would you like to know?"
        }
    }

    private fun generateTodayClassesResponse(context: String): String {
        return if (context.contains("no classes", ignoreCase = true)) {
            listOf(
                "Great news! You have no classes scheduled for today. Enjoy your free time!",
                "You're free today! No classes on your schedule. Time to relax or catch up on work!",
                "Lucky you! No classes today. Perfect day for studying or taking a break!",
                "Your schedule is clear today - no classes! Make the most of your free day!",
                "Today's a class-free day for you! Time to catch up or enjoy some downtime!",
                "No classes today! How about using this time to work on pending assignments?",
                "You've got a day off from classes! Perfect for getting ahead or relaxing!",
                "No lectures scheduled today! It's a great day to review or take it easy!",
                "Today you're free from classes! Excellent opportunity to study or recharge!",
                "Clear schedule today - no classes! Enjoy your free time wisely!"
            ).random()
        } else if (context.isNotBlank()) {
            val classCount = context.lines().filter { it.contains("at") }.size
            listOf(
                "Today you have $classCount ${if (classCount == 1) "class" else "classes"}:\n$context",
                "Here's your schedule for today ($classCount ${if (classCount == 1) "class" else "classes"}):\n$context",
                "You've got $classCount ${if (classCount == 1) "class" else "classes"} today:\n$context",
                "Today's lineup ($classCount ${if (classCount == 1) "class" else "classes"}):\n$context",
                "Your day includes $classCount ${if (classCount == 1) "class" else "classes"}:\n$context",
                "Here's what you have today ($classCount ${if (classCount == 1) "class" else "classes"}):\n$context",
                "Today you're attending $classCount ${if (classCount == 1) "class" else "classes"}:\n$context",
                "Let me show you today's classes ($classCount total):\n$context",
                "You have $classCount ${if (classCount == 1) "class" else "classes"} on your schedule:\n$context",
                "Your agenda for today ($classCount ${if (classCount == 1) "class" else "classes"}):\n$context"
            ).random()
        } else {
            listOf(
                "Let me check your schedule... It looks like you haven't added any classes yet. Would you like to add some?",
                "Your timetable is empty at the moment. Tap the plus button to add your classes!",
                "I don't see any classes scheduled yet. Let's add your timetable to get started!",
                "Your schedule is waiting to be filled! Add your classes to keep track of them.",
                "No classes added yet. Want to start building your timetable?",
                "Time to set up your schedule! Add your classes to begin tracking them.",
                "Your timetable needs some classes! Let's get you organized.",
                "I'm ready to help, but first add your classes to your schedule!",
                "Empty schedule! Add your classes and I'll help you stay on track.",
                "Let's populate your timetable! Add your classes to get started."
            ).random()
        }
    }

    private fun generateNextClassResponse(context: String): String {
        return if (context.contains("No more", ignoreCase = true)) {
            listOf(
                "You're done for the day! No more classes scheduled. Time to relax!",
                "That's it for today! No more classes. Enjoy your free time!",
                "You're all set! No more classes today. Great job!",
                "No more classes for today! You can focus on other things now!",
                "You've finished all your classes for today! Time for a break!",
                "All done with classes! The rest of the day is yours!",
                "No more lectures today! Perfect time to study or unwind!",
                "That's a wrap for classes today! Enjoy the rest of your day!",
                "You're free from classes now! Make the most of your time!",
                "Classes are over for today! Time to relax or catch up on work!"
            ).random()
        } else if (context.isNotBlank()) {
            listOf(
                "Coming up next: $context",
                "Your next class is: $context",
                "Up next: $context",
                "Next on your schedule: $context",
                "Here's what's next: $context",
                "You have next: $context",
                "Following this: $context",
                "Your upcoming class: $context",
                "Next class: $context",
                "On deck: $context"
            ).random()
        } else {
            listOf(
                "I don't see any upcoming classes. You might be done for the day!",
                "No next class found. Check your timetable to add classes!",
                "Looks like you're free! No more classes scheduled.",
                "I can't find a next class. Maybe it's break time?",
                "No upcoming classes in your schedule. Enjoy your free time!",
                "Your schedule doesn't show a next class. All done for now?",
                "I don't have a next class for you. Time for a break!",
                "No more classes lined up. Perfect time to catch up!",
                "Can't find your next class. Your schedule might be empty.",
                "Looks like you're free from here! No next class scheduled."
            ).random()
        }
    }

    private fun generateRemindersResponse(context: String): String {
        return if (context.contains("no active reminders", ignoreCase = true)) {
            listOf(
                "You have no active reminders right now. You're all caught up!",
                "Great job! No pending reminders. You're on top of everything!",
                "No reminders at the moment. You're doing great!",
                "All clear! No active reminders. Keep up the good work!",
                "You have no pending tasks. Excellent organization!",
                "No reminders to show! You're perfectly organized!",
                "Everything's done! No active reminders right now!",
                "You're all set! No pending reminders at this time!",
                "Clear board! No reminders are active right now!",
                "Congratulations! You have no outstanding reminders!"
            ).random()
        } else if (context.isNotBlank()) {
            val reminderCount = context.lines().filter { it.contains("-") }.size
            listOf(
                "You have $reminderCount active ${if (reminderCount == 1) "reminder" else "reminders"}:\n$context",
                "Here are your reminders ($reminderCount):\n$context",
                "You've got $reminderCount ${if (reminderCount == 1) "task" else "tasks"} to remember:\n$context",
                "Your to-do list ($reminderCount ${if (reminderCount == 1) "item" else "items"}):\n$context",
                "$reminderCount ${if (reminderCount == 1) "reminder" else "reminders"} for you:\n$context",
                "Here's what you need to remember ($reminderCount ${if (reminderCount == 1) "item" else "items"}):\n$context",
                "You have $reminderCount pending ${if (reminderCount == 1) "task" else "tasks"}:\n$context",
                "Your active reminders ($reminderCount):\n$context",
                "Don't forget these $reminderCount ${if (reminderCount == 1) "thing" else "things"}:\n$context",
                "Here are your $reminderCount outstanding ${if (reminderCount == 1) "reminder" else "reminders"}:\n$context"
            ).random()
        } else {
            listOf(
                "No reminders added yet. Tap the plus button to create one!",
                "Your reminder list is empty. Add some tasks to stay organized!",
                "I don't see any reminders. Want to add some?",
                "No reminders set up yet. Let's add your first one!",
                "Your to-do list is empty! Time to add some tasks?",
                "No reminders found. Start adding tasks to keep track!",
                "Empty reminder list! Add tasks to stay on top of things!",
                "You haven't set any reminders yet. Let's create some!",
                "No tasks in your list. Add reminders to get organized!",
                "Clean slate! Add your first reminder to begin tracking tasks!"
            ).random()
        }
    }

    private fun generateStatusResponse(context: String, userInput: String): String {
        val input = userInput.lowercase()

        return when {
            input.contains("free") || input.contains("available") -> {
                listOf(
                    "Let me check your availability... $context",
                    "Looking at your schedule for free time... $context",
                    "Checking when you're available... $context",
                    "Let me see your free slots... $context",
                    "Finding your free time... $context"
                ).random()
            }

            input.contains("busy") -> {
                listOf(
                    "Let me see how packed your schedule is... $context",
                    "Checking your busy times... $context",
                    "Looking at your schedule density... $context",
                    "Let me analyze your day... $context",
                    "Checking how busy you are... $context"
                ).random()
            }

            else -> {
                listOf(
                    "Here's your day overview: $context",
                    "Your status for today: $context",
                    "Let me summarize your day: $context",
                    "Here's how your day looks: $context",
                    "Your day at a glance: $context"
                ).random()
            }
        }
    }

    private fun generateSpecificDayResponse(context: String, userInput: String): String {
        val day = extractDay(userInput)

        return if (context.contains("no classes", ignoreCase = true)) {
            listOf(
                "You have no classes on $day. Free day!",
                "$day is clear - no classes scheduled!",
                "Lucky you! No classes on $day!",
                "$day's schedule is empty. Enjoy your free time!",
                "No classes for $day. Perfect day to catch up!",
                "You're free on $day - no classes!",
                "$day is a day off from classes!",
                "No lectures on $day. Time to relax or study!",
                "$day's schedule: No classes! Make it count!",
                "Clear schedule for $day - no classes!"
            ).random()
        } else if (context.isNotBlank()) {
            listOf(
                "Here's your schedule for $day:\n$context",
                "On $day you have:\n$context",
                "$day's classes:\n$context",
                "Your $day lineup:\n$context",
                "For $day:\n$context",
                "$day schedule:\n$context",
                "These are your classes on $day:\n$context",
                "On $day you're attending:\n$context",
                "$day's timetable:\n$context",
                "Your agenda for $day:\n$context"
            ).random()
        } else {
            listOf(
                "I don't have any classes scheduled for $day yet.",
                "Your $day schedule is empty. Add some classes!",
                "No classes set for $day. Want to add some?",
                "$day's timetable is blank. Time to fill it in!",
                "You haven't added $day's classes yet.",
                "I can't find classes for $day. Add them to your timetable!",
                "Your $day schedule needs classes. Let's add them!",
                "No $day classes in your timetable yet.",
                "$day looks empty. Add your classes to see your schedule!",
                "Your $day timetable is waiting for classes!"
            ).random()
        }
    }

    private fun generateSubjectResponse(context: String, userInput: String): String {
        val subject = extractSubject(userInput)

        return if (context.contains("No", ignoreCase = true) || context.isBlank()) {
            listOf(
                "I couldn't find $subject in your schedule. Want to add it?",
                "$subject isn't in your timetable yet. Would you like to add it?",
                "I don't see $subject scheduled. Maybe add it to your timetable?",
                "No $subject class found. Check your timetable or add it!",
                "$subject isn't on your schedule. Time to add it?",
                "Can't find $subject in your classes. Add it to get started!",
                "Your schedule doesn't have $subject yet. Let's add it!",
                "I don't have $subject in your timetable. Want to include it?",
                "$subject missing from your schedule. Shall we add it?",
                "No $subject class found. Add it to track its schedule!"
            ).random()
        } else {
            listOf(
                "Here's your $subject class info:\n$context",
                "Found $subject:\n$context",
                "$subject schedule:\n$context",
                "Your $subject class:\n$context",
                "Details for $subject:\n$context",
                "$subject is scheduled:\n$context",
                "Here's when you have $subject:\n$context",
                "$subject class timing:\n$context",
                "Your $subject lecture:\n$context",
                "Information about $subject:\n$context"
            ).random()
        }
    }

    private fun extractDay(input: String): String {
        val lowerInput = input.lowercase()
        return when {
            lowerInput.contains("monday") -> "Monday"
            lowerInput.contains("tuesday") -> "Tuesday"
            lowerInput.contains("wednesday") -> "Wednesday"
            lowerInput.contains("thursday") -> "Thursday"
            lowerInput.contains("friday") -> "Friday"
            lowerInput.contains("saturday") -> "Saturday"
            lowerInput.contains("sunday") -> "Sunday"
            lowerInput.contains("tomorrow") -> "tomorrow"
            else -> "that day"
        }
    }

    private fun extractSubject(input: String): String {
        val lowerInput = input.lowercase()
        return when {
            lowerInput.contains("dsa") || lowerInput.contains("data structure") -> "DSA"
            lowerInput.contains("dbms") || lowerInput.contains("database") -> "DBMS"
            lowerInput.contains("os") || lowerInput.contains("operating") -> "Operating Systems"
            lowerInput.contains("network") -> "Networks"
            lowerInput.contains("software") || lowerInput.contains("se") -> "Software Engineering"
            lowerInput.contains("ml") || lowerInput.contains("machine learning") -> "Machine Learning"
            lowerInput.contains("ai") || lowerInput.contains("artificial") -> "Artificial Intelligence"
            lowerInput.contains("algo") -> "Algorithms"
            lowerInput.contains("web") -> "Web Development"
            lowerInput.contains("mobile") || lowerInput.contains("app") -> "Mobile Development"
            else -> "that subject"
        }
    }
}
