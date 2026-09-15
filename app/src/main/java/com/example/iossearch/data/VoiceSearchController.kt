package com.example.iossearch.data

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class VoiceState { IDLE, LISTENING, PROCESSING, ERROR }

/**
 * Wraps Android's on-device SpeechRecognizer to power a mic-button dictation
 * flow analogous to iOS Search's voice dictation button. This uses Android's
 * own speech APIs, not Apple's — Siri/SFSpeechRecognizer are not available
 * outside iOS, so this is a native-equivalent implementation.
 */
class VoiceSearchController(private val context: Context) {

    var state by mutableStateOf(VoiceState.IDLE)
        private set

    var partialText by mutableStateOf("")
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private var recognizer: SpeechRecognizer? = null

    fun isAvailable(): Boolean = SpeechRecognizer.isRecognitionAvailable(context)

    fun start(onFinalResult: (String) -> Unit) {
        if (!isAvailable()) {
            state = VoiceState.ERROR
            errorMessage = "Speech recognition isn't available on this device."
            return
        }

        stop()
        errorMessage = null
        partialText = ""

        recognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    state = VoiceState.LISTENING
                }

                override fun onBeginningOfSpeech() {
                    state = VoiceState.LISTENING
                }

                override fun onRmsChanged(rmsdB: Float) { /* could drive a waveform animation */ }

                override fun onBufferReceived(buffer: ByteArray?) {}

                override fun onEndOfSpeech() {
                    state = VoiceState.PROCESSING
                }

                override fun onError(error: Int) {
                    state = VoiceState.ERROR
                    errorMessage = when (error) {
                        SpeechRecognizer.ERROR_NO_MATCH -> "Didn't catch that. Try again."
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected."
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission is required."
                        else -> "Voice search failed. Try again."
                    }
                }

                override fun onResults(results: Bundle?) {
                    val text = results
                        ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.firstOrNull()
                        .orEmpty()
                    state = VoiceState.IDLE
                    if (text.isNotBlank()) onFinalResult(text)
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    partialText = partialResults
                        ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.firstOrNull()
                        .orEmpty()
                }

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }
            startListening(intent)
        }
    }

    fun stop() {
        recognizer?.destroy()
        recognizer = null
        if (state == VoiceState.LISTENING || state == VoiceState.PROCESSING) {
            state = VoiceState.IDLE
        }
    }

    fun dismissError() {
        errorMessage = null
        state = VoiceState.IDLE
    }
}
