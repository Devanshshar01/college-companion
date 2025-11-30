package com.example.collegecompanion

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.collegecompanion.databinding.ActivityMainBinding
import com.example.collegecompanion.viewmodel.MainViewModel
import com.example.collegecompanion.voice.VoiceEngine

/**
 * Main Activity - Entry point of College Companion app.
 * Handles voice interactions, navigation, and coordinates between UI and business logic.
 *
 * Key Features:
 * - Zero-latency voice interface with real-time feedback
 * - Seamless navigation between fragments
 * - Permission handling for microphone access
 * - Haptic feedback for enhanced UX
 */
class MainActivity : AppCompatActivity(), VoiceEngine.VoiceCallback {

    companion object {
        private const val TAG = "MainActivity"
        private const val VIBRATION_DURATION_MS = 50L
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var voiceEngine: VoiceEngine
    private val viewModel: MainViewModel by viewModels()

    private var isListening = false
    private var isProcessing = false
    private var pulseAnimator: ObjectAnimator? = null

    private val micPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startVoiceInteraction()
        } else {
            showPermissionDeniedDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupVoiceEngine()
        setupNavigation()
        setupToolbar()
        setupVoiceInteraction()
        observeViewModel()
    }

    /**
     * Initialize voice engine for speech recognition and TTS.
     */
    private fun setupVoiceEngine() {
        voiceEngine = VoiceEngine(this, this)
    }

    /**
     * Setup navigation controller and bottom navigation.
     */
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)

        // Add smooth slide animation for navigation transitions
        binding.bottomNav.setOnItemSelectedListener { item ->
            val currentDestination = navController.currentDestination?.id
            if (currentDestination != item.itemId) {
                navController.navigate(item.itemId)
                true
            } else {
                false
            }
        }
    }

    /**
     * Setup toolbar with menu items.
     */
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

    /**
     * Setup voice interaction button and feedback.
     */
    private fun setupVoiceInteraction() {
        binding.voiceFab.setOnClickListener {
            handleVoiceButtonClick()
        }

        // Ensure mic button is accessible
        binding.voiceFab.contentDescription = getString(R.string.voice_button_description)
    }

    /**
     * Observe ViewModel LiveData for UI updates.
     */
    private fun observeViewModel() {
        // Observe voice response
        viewModel.voiceResponse.observe(this) { response ->
            if (response.isNotEmpty()) {
                displayResponse(response)
                speakResponse(response)
            }
        }

        // Observe processing state
        viewModel.isProcessing.observe(this) { processing ->
            isProcessing = processing
            updateProcessingState(processing)
        }

        // Observe error messages
        viewModel.errorMessage.observe(this) { error ->
            error?.let {
                Log.e(TAG, "Error: $it")
                Toast.makeText(this, "Error: $it", Toast.LENGTH_SHORT).show()
                viewModel.clearErrorMessage()
            }
        }

        // Observe success messages
        viewModel.successMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                performHapticFeedback()
                viewModel.clearSuccessMessage()
            }
        }
    }

    /**
     * Handle voice button click - check permission and start listening.
     */
    private fun handleVoiceButtonClick() {
        // Prevent multiple simultaneous operations
        if (isListening || isProcessing) {
            stopVoiceInteraction()
            return
        }

        // Check microphone permission
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                startVoiceInteraction()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) -> {
                showPermissionRationaleDialog()
            }

            else -> {
                micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    /**
     * Start voice interaction session.
     */
    private fun startVoiceInteraction() {
        isListening = true
        performHapticFeedback()
        voiceEngine.startListening()
    }

    /**
     * Stop current voice interaction.
     */
    private fun stopVoiceInteraction() {
        if (isListening) {
            voiceEngine.stopListening()
        }
        if (voiceEngine.isSpeaking()) {
            voiceEngine.stopSpeaking()
        }
        isListening = false
        stopPulseAnimation()
        binding.voiceFeedbackText.text = getString(R.string.voice_prompt)
    }

    /**
     * Display AI response in UI with fade-in animation.
     */
    private fun displayResponse(response: String) {
        binding.voiceFeedbackText.apply {
            alpha = 0f
            text = response
            animate()
                .alpha(1f)
                .setDuration(300)
                .start()
        }
    }

    /**
     * Speak response using TTS.
     */
    private fun speakResponse(response: String) {
        voiceEngine.speak(response)
    }

    /**
     * Update UI based on processing state.
     */
    private fun updateProcessingState(processing: Boolean) {
        binding.voiceFab.isEnabled = !processing
        if (processing) {
            binding.voiceFeedbackText.text = getString(R.string.processing)
        }
    }

    /**
     * Show dialog explaining why microphone permission is needed.
     */
    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Microphone Permission Required")
            .setMessage("College Companion needs microphone access to understand your voice commands and help you manage your schedule hands-free.")
            .setPositiveButton("Grant Permission") { _, _ ->
                micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Show dialog when permission is permanently denied.
     */
    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("Voice commands won't work without microphone permission. You can enable it in app settings.")
            .setPositiveButton("OK", null)
            .show()
    }

    /**
     * Perform haptic feedback for better UX.
     */
    private fun performHapticFeedback() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    VIBRATION_DURATION_MS,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(VIBRATION_DURATION_MS)
        }
    }

    /**
     * Start pulse animation on listening indicator.
     */
    private fun startPulseAnimation() {
        binding.pulseView.visibility = View.VISIBLE
        pulseAnimator = ObjectAnimator.ofPropertyValuesHolder(
            binding.pulseView,
            PropertyValuesHolder.ofFloat("scaleX", 1f, 1.3f),
            PropertyValuesHolder.ofFloat("scaleY", 1f, 1.3f),
            PropertyValuesHolder.ofFloat("alpha", 0.7f, 0.3f)
        ).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            start()
        }
    }

    /**
     * Stop pulse animation.
     */
    private fun stopPulseAnimation() {
        pulseAnimator?.cancel()
        binding.pulseView.visibility = View.INVISIBLE
        binding.pulseView.scaleX = 1f
        binding.pulseView.scaleY = 1f
        binding.pulseView.alpha = 1f
    }

    // VoiceEngine.VoiceCallback implementations

    override fun onListeningStarted() {
        runOnUiThread {
            binding.voiceFeedbackText.text = getString(R.string.listening)
            startPulseAnimation()
            binding.voiceFab.isEnabled = true // Allow stopping
        }
    }

    override fun onListeningEnded() {
        runOnUiThread {
            isListening = false
            stopPulseAnimation()
            binding.voiceFab.isEnabled = !isProcessing
        }
    }

    override fun onSpeechRecognized(text: String) {
        runOnUiThread {
            Log.d(TAG, "Speech recognized: $text")
            binding.voiceFeedbackText.text = "\"$text\""

            // Process the command through ViewModel
            viewModel.processVoiceCommand(text)
        }
    }

    override fun onSpeechError(error: String) {
        runOnUiThread {
            Log.e(TAG, "Speech error: $error")
            binding.voiceFeedbackText.text = error
            stopPulseAnimation()
            isListening = false
            binding.voiceFab.isEnabled = true

            // Show error with retry option
            if (error.contains("permission", ignoreCase = true)) {
                showPermissionRationaleDialog()
            }
        }
    }

    override fun onSpeakingStarted() {
        runOnUiThread {
            Log.d(TAG, "Started speaking")
        }
    }

    override fun onSpeakingEnded() {
        runOnUiThread {
            Log.d(TAG, "Finished speaking")
            performHapticFeedback()

            // Reset to default prompt after speaking
            binding.voiceFeedbackText.postDelayed({
                if (!isListening && !isProcessing) {
                    binding.voiceFeedbackText.apply {
                        animate()
                            .alpha(0f)
                            .setDuration(300)
                            .withEndAction {
                                text = getString(R.string.voice_prompt)
                                animate().alpha(1f).setDuration(300).start()
                            }
                            .start()
                    }
                }
            }, 2000)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        voiceEngine.cleanup()
        pulseAnimator?.cancel()
    }

    override fun onPause() {
        super.onPause()
        // Stop voice interaction when app goes to background
        stopVoiceInteraction()
    }
}
