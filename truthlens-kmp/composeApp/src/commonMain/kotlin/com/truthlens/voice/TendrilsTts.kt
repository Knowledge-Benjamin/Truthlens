package com.truthlens.voice

/**
 * Native Text-To-Speech (TTS) Engine Interface.
 * Defaults to native Android TextToSpeech engine, structured to allow plug-and-play
 * ONNX voice models (e.g. Piper, VITS, Kokoro) in future milestones.
 */
expect object TendrilsTts {
    var isMuted: Boolean
    fun speak(text: String, languageCode: String = "en", pitch: Float = 1.0f, speed: Float = 1.0f)
    fun speakChunk(text: String, flush: Boolean = false, languageCode: String = "en", pitch: Float = 1.0f, speed: Float = 1.0f)
    fun stop()
    fun isSpeaking(): Boolean
}
