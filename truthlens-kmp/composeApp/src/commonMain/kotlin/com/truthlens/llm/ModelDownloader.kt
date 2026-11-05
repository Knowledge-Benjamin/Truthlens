package com.truthlens.llm

expect class ModelDownloader() {
    /**
     * Downloads the model from the given URL and returns the absolute local path.
     * If the model is already downloaded, it immediately returns the local path.
     */
    suspend fun downloadModel(url: String, fileName: String, onProgress: (Float) -> Unit): String
}
