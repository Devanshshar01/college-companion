package com.example.collegecompanion

import android.app.Application
import android.content.Context
import android.util.Log
import com.example.collegecompanion.data.AppDatabase
import com.example.collegecompanion.util.SampleDataGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Application class for College Companion.
 * Handles app-wide initialization including database setup with sample data.
 */
class CollegeCompanionApp : Application() {

    companion object {
        private const val TAG = "CollegeCompanionApp"
        private const val PREFS_NAME = "CollegeCompanionPrefs"
        private const val KEY_FIRST_LAUNCH = "is_first_launch"
    }

    override fun onCreate() {
        super.onCreate()

        // Apply dark mode setting on startup
        SettingsActivity.applyDarkModeOnStartup(this)

        // Initialize sample data on first launch
        initializeSampleDataIfNeeded()
    }

    /**
     * Initialize database with sample data on first app launch.
     * DISABLED - Users will add their own data.
     */
    private fun initializeSampleDataIfNeeded() {
        // Sample data initialization disabled
        // Users will add their own classes and reminders
    }
}
