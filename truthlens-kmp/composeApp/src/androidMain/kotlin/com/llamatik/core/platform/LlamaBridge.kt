package com.llamatik.core.platform

interface GenStream {
    fun onDelta(delta: String)
    fun onComplete()
    fun onError(error: String)
}

object LlamaBridge {
    private var isNativeLoaded = false

    init {
        try {
            System.loadLibrary("llama_jni")
            isNativeLoaded = true
        } catch (e: Throwable) {
            println("LlamaBridge System.loadLibrary warning: ${e.message}")
            isNativeLoaded = false
        }
    }

    fun isLoaded(): Boolean = isNativeLoaded

    external fun initGenerateModel(modelPath: String): Boolean
    external fun generate(prompt: String): String
    external fun generateWithContext(context: String, prompt: String, grammar: String): String

    private external fun nativeUpdateGenerationParams(
        temp: Float,
        topK: Int,
        topP: Float,
        minP: Int,
        repeatPenalty: Float,
        repeatLastN: Int,
        maxTokens: Int,
        ignoreEos: Boolean,
        penalizeNl: Boolean,
        numThreads: Int,
        gpuLayers: Int
    )

    fun updateGenerateParams(
        temperature: Float = 0.7f,
        maxTokens: Int = 512,
        topP: Float = 0.95f,
        topK: Int = 40,
        repeatPenalty: Float = 1.1f,
        contextLength: Int = 2048,
        numThreads: Int = 4,
        gpuLayers: Int = 0
    ) {
        if (!isNativeLoaded) return
        try {
            nativeUpdateGenerationParams(
                temperature,
                topK,
                topP,
                0,
                repeatPenalty,
                64,
                maxTokens,
                false,
                false,
                numThreads,
                gpuLayers
            )
        } catch (e: Throwable) {
            println("updateGenerateParams error: ${e.message}")
        }
    }

    private external fun nativeGenerateStream(prompt: String, callback: GenStream)

    fun generateStream(prompt: String, callback: GenStream) {
        if (!isNativeLoaded) {
            callback.onError("Native llama_jni library not loaded.")
            return
        }
        try {
            nativeGenerateStream(prompt, callback)
        } catch (e: Throwable) {
            callback.onError(e.message ?: "Generation error")
        }
    }

    external fun shutdown()
    external fun nativeCancelGenerate()
}
