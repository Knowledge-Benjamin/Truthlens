package com.truthlens.voice

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import java.lang.ref.WeakReference

actual object TendrilsStt {
    private var activityRef: WeakReference<ComponentActivity>? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private var speechIntentLauncher: ActivityResultLauncher<Intent>? = null
    private var permissionLauncher: ActivityResultLauncher<String>? = null

    private var currentlyListening = false
    private var pendingLangCode = "en-US"
    private var activePartialCallback: ((String) -> Unit)? = null
    private var activeFinalCallback: ((String) -> Unit)? = null
    private var activeErrorCallback: ((String) -> Unit)? = null

    fun init(activity: ComponentActivity) {
        activityRef = WeakReference(activity)

        speechIntentLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            currentlyListening = false
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val transcript = matches?.firstOrNull()?.trim().orEmpty()
                if (transcript.isNotEmpty()) {
                    activeFinalCallback?.invoke(transcript)
                } else {
                    activeErrorCallback?.invoke("No speech detected")
                }
            } else {
                activeErrorCallback?.invoke("Voice input cancelled")
            }
        }

        permissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                startInternalListening()
            } else {
                // Fall back to system speech recognizer dialog if direct mic permission denied
                launchSystemRecognizerDialog()
            }
        }
    }

    actual fun startListening(
        languageCode: String,
        onPartialResult: (String) -> Unit,
        onFinalResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val activity = activityRef?.get()
        if (activity == null) {
            onError("Microphone unavailable")
            return
        }

        // Stop TTS if speaking so mic doesn't record the assistant's own voice
        TendrilsTts.stop()

        pendingLangCode = languageCode
        activePartialCallback = onPartialResult
        activeFinalCallback = onFinalResult
        activeErrorCallback = onError

        Handler(Looper.getMainLooper()).post {
            val hasPermission = ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                startInternalListening()
            } else {
                currentlyListening = true
                try {
                    permissionLauncher?.launch(Manifest.permission.RECORD_AUDIO)
                } catch (_: Throwable) {
                    launchSystemRecognizerDialog()
                }
            }
        }
    }

    private fun startInternalListening() {
        val activity = activityRef?.get() ?: return
        try {
            if (!SpeechRecognizer.isRecognitionAvailable(activity)) {
                launchSystemRecognizerDialog()
                return
            }

            speechRecognizer?.destroy()
            val recognizer = SpeechRecognizer.createSpeechRecognizer(activity)
            speechRecognizer = recognizer

            recognizer.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    currentlyListening = true
                }

                override fun onBeginningOfSpeech() {
                    currentlyListening = true
                }

                override fun onRmsChanged(rmsdB: Float) {}

                override fun onBufferReceived(buffer: ByteArray?) {}

                override fun onEndOfSpeech() {
                    currentlyListening = false
                }

                override fun onError(error: Int) {
                    currentlyListening = false
                    // If background service fails (e.g. ERROR_INSUFFICIENT_PERMISSIONS or ERROR_CLIENT),
                    // fall back seamlessly to the system recognizer dialog
                    if (error == SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ||
                        error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY ||
                        error == SpeechRecognizer.ERROR_CLIENT
                    ) {
                        launchSystemRecognizerDialog()
                    } else {
                        activeErrorCallback?.invoke("Listening ended")
                    }
                }

                override fun onResults(results: Bundle?) {
                    currentlyListening = false
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val text = matches?.firstOrNull()?.trim().orEmpty()
                    if (text.isNotEmpty()) {
                        activeFinalCallback?.invoke(text)
                    } else {
                        activeErrorCallback?.invoke("No speech heard")
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val partial = matches?.firstOrNull()?.trim().orEmpty()
                    if (partial.isNotEmpty()) {
                        activePartialCallback?.invoke(partial)
                    }
                }

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, pendingLangCode)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            }

            currentlyListening = true
            recognizer.startListening(intent)
        } catch (e: Throwable) {
            launchSystemRecognizerDialog()
        }
    }

    private fun launchSystemRecognizerDialog() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, pendingLangCode)
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to Tendrils...")
            }
            currentlyListening = true
            speechIntentLauncher?.launch(intent)
        } catch (e: Throwable) {
            currentlyListening = false
            activeErrorCallback?.invoke("Speech recognition not available on this device")
        }
    }

    actual fun stopListening() {
        Handler(Looper.getMainLooper()).post {
            try {
                speechRecognizer?.stopListening()
            } catch (_: Throwable) {}
            currentlyListening = false
        }
    }

    actual fun isListening(): Boolean = currentlyListening
}
