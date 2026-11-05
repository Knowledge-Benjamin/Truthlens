package com.truthlens.voice

/**
 * Native Text-To-Speech (TTS) Engine Interface.
 * Defaults to native Android TextToSpeech engine, structured to allow plug-and-play
 * ONNX voice models (e.g. Piper, VITS, Kokoro) in future milestones.
 */
expect object TendrilsTts {
    fun speak(text: String, languageCode: String = "en", pitch: Float = 1.0f, speed: Float = 1.0f)
    fun stop()
    fun isSpeaking(): Boolean
}
