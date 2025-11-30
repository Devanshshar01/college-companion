package com.example.collegecompanion

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.collegecompanion.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        // Toggles don't do anything yet, just UI
    }
}