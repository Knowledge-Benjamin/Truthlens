package com.truthlens.llm

import com.llamatik.core.platform.GenStream
import com.llamatik.core.platform.LlamaBridge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File

object LlamatikEngine {
    private var isModelLoaded = false
    private var currentModelPath: String? = null

    fun isNativeBridgeLoaded(): Boolean = LlamaBridge.isLoaded()
    fun isModelLoaded(): Boolean = isModelLoaded
    fun getActiveModelPath(): String? = currentModelPath

    /**
     * Initializes a GGUF language model for on-device inference using llama.cpp JNI.
     */
    fun initialize(modelPath: String): Boolean = initModel(modelPath)

    fun initModel(modelPath: String): Boolean {
        return try {
            val file = File(modelPath)
            if (!file.exists()) {
                println("LlamatikEngine: Model file does not exist at $modelPath")
                isModelLoaded = false
                return false
            }

            LlamaBridge.updateGenerateParams(
                temperature = 0.7f,
                maxTokens = 512,
                topP = 0.95f,
                topK = 40,
                repeatPenalty = 1.1f,
                contextLength = 2048,
                numThreads = 4,
                gpuLayers = 0
            )

            val success = LlamaBridge.initGenerateModel(modelPath)
            isModelLoaded = success
            if (success) {
                currentModelPath = modelPath
                println("LlamatikEngine: Successfully loaded model at $modelPath")
            }
            success
        } catch (e: Throwable) {
            println("LlamatikEngine initModel exception: ${e.message}")
            isModelLoaded = false
            false
        }
    }

    /**
     * Generates text streaming from the loaded GGUF model via llama_jni.
     */
    fun generateStreaming(prompt: String): Flow<String> = callbackFlow {
        if (isModelLoaded && LlamaBridge.isLoaded()) {
            val streamCallback = object : GenStream {
                override fun onDelta(delta: String) {
                    trySend(delta)
                }

                override fun onComplete() {
                    channel.close()
                }

                override fun onError(error: String) {
                    trySend("\n[Llamatik Engine Error: $error]")
                    channel.close()
                }
            }

            try {
                LlamaBridge.generateStream(prompt, streamCallback)
            } catch (e: Throwable) {
                trySend("\n[LlamaBridge Exception: ${e.message}]")
                channel.close()
            }

            awaitClose {
                LlamaBridge.nativeCancelGenerate()
            }
        } else {
            // Native binaries are bundled and loaded, guide user to active model
            val response = if (LlamaBridge.isLoaded()) {
                "Tendrils on-device engine (`libllama_jni.so`) is active. To run full offline LLM inference on this device, download or import a GGUF model (e.g. SmolLM-135M or Qwen-0.5B) via Profile > Model Manager (Lab) to `/sdcard/Download/Tendrils/models/`.\n\nYour question: \"$prompt\""
            } else {
                "Tendrils Companion: I am listening and ready. How can I assist your learning today?"
            }

            for (word in response.split(" ")) {
                kotlinx.coroutines.delay(28)
                trySend("$word ")
            }
            channel.close()
            awaitClose {}
        }
    }

    fun shutdown() {
        if (isModelLoaded) {
            try {
                LlamaBridge.shutdown()
            } catch (e: Throwable) {
                println("Shutdown error: ${e.message}")
            }
            isModelLoaded = false
        }
    }
}
