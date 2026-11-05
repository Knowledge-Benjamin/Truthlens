package com.truthlens.voice

actual object TendrilsTts {
    actual var isMuted: Boolean = false
    private var currentlySpeaking = false

    actual fun speak(text: String, languageCode: String, pitch: Float, speed: Float) {
        speakChunk(text, flush = true, languageCode = languageCode, pitch = pitch, speed = speed)
    }

    actual fun speakChunk(text: String, flush: Boolean, languageCode: String, pitch: Float, speed: Float) {
        currentlySpeaking = true
        println("Tendrils Desktop TTS speaking [flush=$flush, lang=$languageCode, speed=$speed]: $text")
        Thread {
            try {
                Thread.sleep(600)
            } catch (_: Exception) {}
            currentlySpeaking = false
        }.start()
    }

    actual fun stop() {
        currentlySpeaking = false
    }

    actual fun isSpeaking(): Boolean = currentlySpeaking
}
