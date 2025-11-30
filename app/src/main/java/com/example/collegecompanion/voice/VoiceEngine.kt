package com.example.collegecompanion.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.*

/**
 * Voice Engine for handling speech recognition and text-to-speech.
 * Implements zero-latency voice interface for seamless user interaction.
 *
 * Features:
 * - Real-time speech recognition with error handling
 * - Natural text-to-speech output
 * - Callback-based architecture for UI updates
 */
class VoiceEngine(
    private val context: Context,
    private val callback: VoiceCallback
) {

    companion object {
        private const val TAG = "VoiceEngine"
        private const val UTTERANCE_ID = "college_companion_tts"
    }

    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false

    interface VoiceCallback {
        fun onListeningStarted()
        fun onListeningEnded()
        fun onSpeechRecognized(text: String)
        fun onSpeechError(error: String)
        fun onSpeakingStarted()
        fun onSpeakingEnded()
    }

    init {
        initializeSpeechRecognizer()
        initializeTextToSpeech()
    }

    /**
     * Initialize speech recognizer for voice input.
     */
    private fun initializeSpeechRecognizer() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Log.e(TAG, "Speech recognition not available")
            callback.onSpeechError("Speech recognition not available on this device")
            return
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    Log.d(TAG, "Ready for speech")
                    callback.onListeningStarted()
                }

                override fun onBeginningOfSpeech() {
                    Log.d(TAG, "Speech started")
                }

                override fun onRmsChanged(rmsdB: Float) {
                    // Can be used for visualizing audio levels
                }

                override fun onBufferReceived(buffer: ByteArray?) {}

                override fun onEndOfSpeech() {
                    Log.d(TAG, "Speech ended")
                    callback.onListeningEnded()
                }

                override fun onError(error: Int) {
                    val errorMessage = getErrorMessage(error)
                    Log.e(TAG, "Speech recognition error: $errorMessage")
                    callback.onSpeechError(errorMessage)
                    callback.onListeningEnded()
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val recognizedText = matches[0]
                        Log.d(TAG, "Recognized: $recognizedText")
                        callback.onSpeechRecognized(recognizedText)
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    // Can be used for real-time transcription display
                }

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
    }

    /**
     * Initialize text-to-speech for voice output.
     */
    private fun initializeTextToSpeech() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.US
                isTtsReady = true
                Log.d(TAG, "TTS initialized successfully")

                // Set up utterance listener for callbacks
                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        callback.onSpeakingStarted()
                    }

                    override fun onDone(utteranceId: String?) {
                        callback.onSpeakingEnded()
                    }

                    override fun onError(utteranceId: String?) {
                        Log.e(TAG, "TTS error")
                        callback.onSpeakingEnded()
                    }
                })
            } else {
                Log.e(TAG, "TTS initialization failed")
                isTtsReady = false
            }
        }
    }

    /**
     * Start listening for voice input.
     */
    fun startListening() {
        if (speechRecognizer == null) {
            callback.onSpeechError("Speech recognizer not initialized")
            return
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        try {
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error starting speech recognition", e)
            callback.onSpeechError("Failed to start listening: ${e.message}")
        }
    }

    /**
     * Stop listening for voice input.
     */
    fun stopListening() {
        speechRecognizer?.stopListening()
        callback.onListeningEnded()
    }

    /**
     * Speak text using text-to-speech.
     * @param text The text to speak
     */
    fun speak(text: String) {
        if (!isTtsReady) {
            Log.e(TAG, "TTS not ready")
            callback.onSpeakingEnded()
            return
        }

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, UTTERANCE_ID)
        }

        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, params, UTTERANCE_ID)
    }

    /**
     * Stop current speech output.
     */
    fun stopSpeaking() {
        textToSpeech?.stop()
    }

    /**
     * Check if currently speaking.
     */
    fun isSpeaking(): Boolean {
        return textToSpeech?.isSpeaking ?: false
    }

    /**
     * Clean up resources.
     */
    fun cleanup() {
        speechRecognizer?.destroy()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        speechRecognizer = null
        textToSpeech = null
    }

    /**
     * Convert error code to user-friendly message.
     */
    private fun getErrorMessage(errorCode: Int): String {
        return when (errorCode) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No speech detected. Please try again."
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Speech recognizer is busy"
            SpeechRecognizer.ERROR_SERVER -> "Server error"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected"
            else -> "Speech recognition error"
        }
    }
}
