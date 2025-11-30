package com.example.collegecompanion

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.collegecompanion.databinding.ActivityHelpBinding

/**
 * Help Activity - Displays voice command guide and usage tips.
 * Helps users understand how to effectively use voice commands.
 */
class HelpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHelpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
    }

    /**
     * Setup toolbar with back navigation.
     */
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}
