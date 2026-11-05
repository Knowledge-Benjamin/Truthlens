package com.truthlens.voice

actual object TendrilsStt {
    private var listening = false

    actual fun startListening(
        languageCode: String,
        onPartialResult: (String) -> Unit,
        onFinalResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        listening = true
        onPartialResult("Listening...")
        Thread {
            try {
                Thread.sleep(1200)
            } catch (_: Exception) {}
            listening = false
            onFinalResult("How do search engines work?")
        }.start()
    }

    actual fun stopListening() {
        listening = false
    }

    actual fun isListening(): Boolean = listening
}
