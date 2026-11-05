package com.truthlens.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

actual object TendrilsTts {
    private var tts: TextToSpeech? = null
    private var isReady = false
    private var currentlySpeaking = false

    fun init(context: Context) {
        if (tts != null) return
        try {
            tts = TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    isReady = true
                    tts?.language = Locale.ENGLISH
                    tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            currentlySpeaking = true
                        }

                        override fun onDone(utteranceId: String?) {
                            currentlySpeaking = false
                        }

                        @Deprecated("Deprecated in Java")
                        override fun onError(utteranceId: String?) {
                            currentlySpeaking = false
                        }
                    })
                }
            }
        } catch (e: Exception) {
            println("TendrilsTts init error: ${e.message}")
        }
    }

    actual fun speak(text: String, languageCode: String, pitch: Float, speed: Float) {
        if (!isReady || tts == null) return
        try {
            val locale = when (languageCode.lowercase()) {
                "sw", "kiswahili" -> Locale("sw")
                "lg", "luganda" -> Locale("lg")
                "fr", "french" -> Locale.FRENCH
                else -> Locale.ENGLISH
            }
            if (tts?.isLanguageAvailable(locale) ?: TextToSpeech.LANG_NOT_SUPPORTED >= TextToSpeech.LANG_AVAILABLE) {
                tts?.language = locale
            } else {
                tts?.language = Locale.ENGLISH
            }
            tts?.setPitch(pitch)
            tts?.setSpeechRate(speed)
            val utteranceId = "tendrils_${System.currentTimeMillis()}"
            // Strip markdown formatting before speaking
            val cleanText = text
                .replace(Regex("#+\\s*"), "")
                .replace(Regex("\\*\\*([^*]+)\\*\\*"), "$1")
                .replace(Regex("\\*([^*]+)\\*"), "$1")
                .replace(Regex("`[^`]+`"), "")
                .trim()

            tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
            currentlySpeaking = true
        } catch (e: Exception) {
            currentlySpeaking = false
        }
    }

    actual fun stop() {
        try {
            tts?.stop()
            currentlySpeaking = false
        } catch (_: Exception) {}
    }

    actual fun isSpeaking(): Boolean {
        return currentlySpeaking || (tts?.isSpeaking == true)
    }
}
