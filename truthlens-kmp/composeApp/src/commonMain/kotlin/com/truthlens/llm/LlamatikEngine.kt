package com.truthlens.llm

import com.llamatik.core.platform.LlamaBridge
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object LlamatikEngine {

    fun initModel(modelPath: String) {
        LlamaBridge.updateGenerateParams(
            temperature = 0.7f,
            maxTokens = 512,
            topP = 0.95f,
            topK = 40,
            repeatPenalty = 1.1f,
            contextLength = 4096,
            numThreads = 4,
            useMmap = true,
            flashAttention = false,
            gpuLayers = 0
        )
        LlamaBridge.initGenerateModel(modelPath)
    }

    fun generateStreaming(prompt: String): Flow<String> = flow {
        // Fallback implementation if Llamatik doesn't expose flow directly on core. 
        // Some KMP bindings expose a callback or a sequence.
        // Assuming LlamaBridge has a streaming method, or we use a block-and-yield approach.
        // For standard Llamatik API, we can use the Generator class or LlamaBridge.generate(prompt)
        val response = LlamaBridge.generate(prompt)
        emit(response)
    }
}
