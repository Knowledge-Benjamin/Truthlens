package com.truthlens.llm

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object LlamatikEngine {

    fun initModel(modelPath: String) {
        // UI milestone mock / engine stub
    }

    fun generateStreaming(prompt: String): Flow<String> = flow {
        delay(200)
        val sampleResponse = "Hello! I am your Tendrils companion. How can I assist you with your learning or exploration today?"
        for (word in sampleResponse.split(" ")) {
            delay(60)
            emit("$word ")
        }
    }
}
