package com.example.collegecompanion

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.collegecompanion.databinding.ActivitySettingsBinding

/**
 * Settings Activity - Manages app preferences and configuration.
 * Provides toggles for voice features, dark mode, and access to help documentation.
 */
class SettingsActivity : AppCompatActivity() {

    companion object {
        private const val PREFS_NAME = "CollegeCompanionPrefs"
        private const val KEY_DARK_MODE = "dark_mode_enabled"
        private const val KEY_TTS_ENABLED = "tts_enabled"
        private const val KEY_VOICE_FEEDBACK = "voice_feedback"
        private const val KEY_NOTIFICATIONS = "notifications_enabled"

        /**
         * Check if dark mode is enabled.
         */
        fun isDarkModeEnabled(context: Context): Boolean {
            val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            return prefs.getBoolean(KEY_DARK_MODE, false)
        }

        /**
         * Apply dark mode setting on app startup.
         */
        fun applyDarkModeOnStartup(context: Context) {
            val isDarkMode = isDarkModeEnabled(context)
            val mode = if (isDarkMode) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }

    private lateinit var binding: ActivitySettingsBinding
    private val prefs by lazy {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadPreferences()
        setupListeners()
    }

    /**
     * Setup toolbar with back navigation.
     */
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    /**
     * Load saved preferences and update UI.
     */
    private fun loadPreferences() {
        // Load dark mode preference
        val isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false)
        binding.switchDarkMode.isChecked = isDarkMode

        // Load other preferences
        binding.switchTts.isChecked = prefs.getBoolean(KEY_TTS_ENABLED, true)
        binding.switchVoiceFeedback.isChecked = prefs.getBoolean(KEY_VOICE_FEEDBACK, true)
        binding.switchNotifications.isChecked = prefs.getBoolean(KEY_NOTIFICATIONS, true)
    }

    /**
     * Setup listeners for switches and buttons.
     */
    private fun setupListeners() {
        // Dark mode toggle
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(KEY_DARK_MODE, isChecked).apply()
            applyDarkMode(isChecked)
        }

        // TTS toggle
        binding.switchTts.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(KEY_TTS_ENABLED, isChecked).apply()
        }

        // Voice feedback toggle
        binding.switchVoiceFeedback.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(KEY_VOICE_FEEDBACK, isChecked).apply()
        }

        // Notifications toggle
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(KEY_NOTIFICATIONS, isChecked).apply()
        }

        // Help guide button
        binding.btnHelpGuide.setOnClickListener {
            startActivity(Intent(this, HelpActivity::class.java))
        }
    }

    /**
     * Apply dark mode setting immediately.
     */
    private fun applyDarkMode(enabled: Boolean) {
        val mode = if (enabled) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    /**
     * Check if TTS is enabled.
     */
    fun isTtsEnabled(): Boolean {
        return prefs.getBoolean(KEY_TTS_ENABLED, true)
    }

    /**
     * Check if voice feedback is enabled.
     */
    fun isVoiceFeedbackEnabled(): Boolean {
        return prefs.getBoolean(KEY_VOICE_FEEDBACK, true)
    }
}
