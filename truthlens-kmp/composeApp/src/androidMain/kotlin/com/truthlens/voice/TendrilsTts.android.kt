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
                    try {
                        tts?.language = Locale.ENGLISH
                        // Select the friendliest, clearest on-device voice available
                        val voices = tts?.voices
                        if (!voices.isNullOrEmpty()) {
                            val englishVoices = voices.filter {
                                (it.locale.language == "en" || it.locale == Locale.US || it.locale == Locale.UK) &&
                                !it.isNetworkConnectionRequired
                            }
                            // Prefer female/warm natural voice names if present
                            val warmVoice = englishVoices.firstOrNull { voice ->
                                val name = voice.name.lowercase()
                                name.contains("female") || name.contains("sfg") || name.contains("soft") || name.contains("natural")
                            } ?: englishVoices.firstOrNull()

                            if (warmVoice != null) {
                                tts?.voice = warmVoice
                            }
                        }
                    } catch (_: Throwable) {}

                    // Set default pitch and speed for friendly, enthusiastic sprout companion
                    tts?.setPitch(1.20f)
                    tts?.setSpeechRate(1.04f)

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
        speakChunk(text = text, flush = true, languageCode = languageCode, pitch = pitch, speed = speed)
    }

    actual fun speakChunk(text: String, flush: Boolean, languageCode: String, pitch: Float, speed: Float) {
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

            // Friendly companion pitch: 1.20f baseline for youthful, bright sprout persona
            val effectivePitch = if (pitch == 1.0f) 1.20f else pitch
            val effectiveSpeed = if (speed == 1.0f) 1.04f else speed
            tts?.setPitch(effectivePitch)
            tts?.setSpeechRate(effectiveSpeed)

            val utteranceId = "tendrils_${System.nanoTime()}"
            val cleanText = sanitizeForSpeech(text)

            if (cleanText.isNotBlank()) {
                val queueMode = if (flush) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
                tts?.speak(cleanText, queueMode, null, utteranceId)
                currentlySpeaking = true
            }
        } catch (e: Exception) {
            currentlySpeaking = false
        }
    }

    private fun sanitizeForSpeech(raw: String): String {
        var s = raw
        // Remove code blocks
        s = s.replace(Regex("```[\\s\\S]*?```"), "Code sample omitted for reading.")
        // Remove inline code
        s = s.replace(Regex("`([^`]+)`"), "$1")
        // Remove markdown images and links
        s = s.replace(Regex("!\\[[^\\]]*\\]\\([^)]+\\)"), "")
        s = s.replace(Regex("\\[([^\\]]+)\\]\\([^)]+\\)"), "$1")
        // Strip markdown headers
        s = s.replace(Regex("(?m)^#{1,6}\\s+"), "")
        // Strip bullet points and list markers
        s = s.replace(Regex("(?m)^\\s*[-*•]\\s+"), "")
        s = s.replace(Regex("(?m)^\\s*\\d+\\.\\s+"), "")
        // Strip bold and italics
        s = s.replace(Regex("\\*\\*([^*]+)\\*\\*"), "$1")
        s = s.replace(Regex("\\*([^*]+)\\*"), "$1")
        s = s.replace(Regex("__([^_]+)__"), "$1")
        s = s.replace(Regex("_([^_]+)_"), "$1")
        // Strip emoji unicode icons
        s = s.replace(Regex("[\\p{So}\\p{Cn}]"), " ")
        // Natural speech conversions for scientific and educational notation
        s = s.replace("6CO₂", "6 carbon dioxide")
            .replace("CO₂", "carbon dioxide")
            .replace("6H₂O", "6 water")
            .replace("H₂O", "water")
            .replace("C₆H₁₂O₆", "glucose")
            .replace("6O₂", "6 oxygen")
            .replace("O₂", "oxygen")
            .replace("ΔE", "change in energy")
            .replace("→", " produces ")
            .replace("->", " produces ")
            .replace("%", " percent ")
            .replace("&", " and ")
            .replace("UGX", "Uganda shillings")
        // Collapse repeated whitespace
        s = s.replace(Regex("\\s+"), " ").trim()
        return s
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
