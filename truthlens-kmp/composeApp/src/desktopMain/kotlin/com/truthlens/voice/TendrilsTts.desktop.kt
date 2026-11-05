package com.truthlens.voice

actual object TendrilsTts {
    private var currentlySpeaking = false

    actual fun speak(text: String, languageCode: String, pitch: Float, speed: Float) {
        currentlySpeaking = true
        println("Tendrils Desktop TTS speaking [lang=$languageCode, speed=$speed]: $text")
        // Ready for desktop ONNX voice model in future milestone
        Thread {
            try {
                Thread.sleep(1500)
            } catch (_: Exception) {}
            currentlySpeaking = false
        }.start()
    }

    actual fun stop() {
        currentlySpeaking = false
    }

    actual fun isSpeaking(): Boolean = currentlySpeaking
}
