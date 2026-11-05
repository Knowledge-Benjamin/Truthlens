package com.truthlens.llm

import java.io.File
import java.net.URL

actual class ModelDownloader actual constructor() {
    actual suspend fun downloadModel(url: String, fileName: String, onProgress: (Float) -> Unit): String {
        val cacheDir = File(System.getProperty("user.home"), ".truthlens")
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        val targetFile = File(cacheDir, fileName)
        
        if (targetFile.exists() && targetFile.length() > 0) {
            onProgress(1.0f)
            return targetFile.absolutePath
        }

        // Simplistic blocking download for desktop. In a real app, use Ktor with streaming for proper progress.
        onProgress(0.1f)
        val connection = URL(url).openConnection()
        val totalBytes = connection.contentLengthLong
        connection.getInputStream().use { input ->
            targetFile.outputStream().use { output ->
                val buffer = ByteArray(8 * 1024)
                var bytesCopied = 0L
                var bytes = input.read(buffer)
                while (bytes >= 0) {
                    output.write(buffer, 0, bytes)
                    bytesCopied += bytes
                    if (totalBytes > 0) {
                        onProgress(bytesCopied.toFloat() / totalBytes.toFloat())
                    }
                    bytes = input.read(buffer)
                }
            }
        }
        onProgress(1.0f)
        return targetFile.absolutePath
    }
}
