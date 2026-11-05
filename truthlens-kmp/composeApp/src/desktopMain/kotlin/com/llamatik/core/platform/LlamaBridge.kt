package com.llamatik.core.platform

interface GenStream {
    fun onDelta(delta: String)
    fun onComplete()
    fun onError(error: String)
}

object LlamaBridge {
    fun isLoaded(): Boolean = false

    fun initGenerateModel(modelPath: String): Boolean = false
    fun generate(prompt: String): String = "Desktop LlamaBridge: On-device inference is primarily supported on Android arm64/x86_64 targets."
    fun generateWithContext(context: String, prompt: String, grammar: String): String = generate(prompt)

    fun updateGenerateParams(
        temperature: Float = 0.7f,
        maxTokens: Int = 512,
        topP: Float = 0.95f,
        topK: Int = 40,
        repeatPenalty: Float = 1.1f,
        contextLength: Int = 2048,
        numThreads: Int = 4,
        gpuLayers: Int = 0
    ) {}

    fun generateStream(prompt: String, callback: GenStream) {
        callback.onDelta(generate(prompt))
        callback.onComplete()
    }

    fun shutdown() {}
    fun nativeCancelGenerate() {}
}
