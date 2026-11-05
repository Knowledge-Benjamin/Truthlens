package com.truthlens.voice

/**
 * Native Speech-To-Text (STT) Microphone Recording & Transcription Engine.
 * Streams partial spoken words as the user talks and delivers the final transcribed text.
 */
expect object TendrilsStt {
    fun startListening(
        languageCode: String = "en-US",
        onPartialResult: (String) -> Unit = {},
        onFinalResult: (String) -> Unit,
        onError: (String) -> Unit = {}
    )
    fun stopListening()
    fun isListening(): Boolean
}
