package com.example.collegecompanion

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.collegecompanion.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isListening = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupToolbar()
        setupVoiceInteraction()
    }

    private fun setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)

        // Optional: Sync toolbar title with destination
        // NavigationUI.setupWithNavController(binding.toolbar, navController)
    }

    private fun setupVoiceInteraction() {
        binding.voiceFab.setOnClickListener {
            toggleListeningState()
        }
    }

    private fun toggleListeningState() {
        isListening = !isListening

        if (isListening) {
            binding.voiceFeedbackText.text = getString(R.string.listening)
            binding.pulseView.visibility = View.VISIBLE
            startPulseAnimation(binding.pulseView)
        } else {
            binding.voiceFeedbackText.text = getString(R.string.voice_prompt)
            binding.pulseView.visibility = View.INVISIBLE
            binding.pulseView.animate().cancel()
        }
    }

    private fun startPulseAnimation(view: View) {
        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat("scaleX", 1.2f),
            PropertyValuesHolder.ofFloat("scaleY", 1.2f)
        )
        scaleDown.duration = 500
        scaleDown.repeatCount = ObjectAnimator.INFINITE
        scaleDown.repeatMode = ObjectAnimator.REVERSE
        scaleDown.start()
    }
}