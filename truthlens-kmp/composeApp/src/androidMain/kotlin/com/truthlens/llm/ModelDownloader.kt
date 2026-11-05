package com.truthlens.llm

import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

actual class ModelDownloader actual constructor() {
    actual suspend fun downloadModel(
        url: String,
        fileName: String,
        onProgress: (Float) -> Unit
    ): String = withContext(Dispatchers.IO) {
        // Resolve Target Directory: /sdcard/Download/Tendrils/models/
        val publicDownloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val tendrilsDir = File(publicDownloads, "Tendrils/models").apply {
            if (!exists()) mkdirs()
        }

        val targetDir = if (tendrilsDir.exists() && (tendrilsDir.canWrite() || tendrilsDir.isDirectory)) {
            tendrilsDir
        } else {
            val fallback = File(System.getProperty("java.io.tmpdir") ?: "/sdcard/Download", "Tendrils/models")
            fallback.mkdirs()
            fallback
        }

        // Clean filename in case it has query params like ?download=true
        val cleanName = if (fileName.isNotBlank()) {
            fileName.substringBefore("?")
        } else {
            url.substringAfterLast("/").substringBefore("?").ifBlank { "model.gguf" }
        }

        val targetFile = File(targetDir, cleanName)

        // If already downloaded and complete (e.g. > 1 MB), return immediately
        if (targetFile.exists() && targetFile.length() > 1024 * 1024) {
            onProgress(1.0f)
            return@withContext targetFile.absolutePath
        }

        onProgress(0.05f)

        // Follow HTTP redirects explicitly across domains (HuggingFace -> cdn-lfs.huggingface.co)
        var currentUrl = url
        var connection: HttpURLConnection? = null
        var redirectCount = 0
        val maxRedirects = 8

        while (redirectCount < maxRedirects) {
            val parsedUrl = URL(currentUrl)
            val conn = parsedUrl.openConnection() as HttpURLConnection
            conn.instanceFollowRedirects = false
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 14; Tendrils-Companion)")
            conn.setRequestProperty("Accept", "*/*")
            conn.connectTimeout = 20000
            conn.readTimeout = 45000
            conn.connect()

            val status = conn.responseCode
            if (status == HttpURLConnection.HTTP_MOVED_TEMP ||
                status == HttpURLConnection.HTTP_MOVED_PERM ||
                status == HttpURLConnection.HTTP_SEE_OTHER ||
                status == 307 || status == 308
            ) {
                val newLoc = conn.getHeaderField("Location")
                conn.disconnect()
                if (!newLoc.isNullOrBlank()) {
                    currentUrl = if (newLoc.startsWith("http://") || newLoc.startsWith("https://")) {
                        newLoc
                    } else {
                        URL(parsedUrl, newLoc).toString()
                    }
                    redirectCount++
                    continue
                }
            }

            connection = conn
            break
        }

        val finalConn = connection ?: throw IllegalStateException("Failed to resolve URL: $url")
        val responseCode = finalConn.responseCode
        if (responseCode !in 200..299) {
            finalConn.disconnect()
            throw IllegalStateException("HTTP Error $responseCode: ${finalConn.responseMessage}")
        }

        val totalLength = finalConn.contentLengthLong

        finalConn.inputStream.use { input ->
            FileOutputStream(targetFile).use { output ->
                val buffer = ByteArray(64 * 1024)
                var bytesRead: Int
                var totalBytesRead = 0L
                var lastProgressReport = 0L

                while (input.read(buffer).also { bytesRead = it } != -1) {
                    output.write(buffer, 0, bytesRead)
                    totalBytesRead += bytesRead

                    val now = System.currentTimeMillis()
                    if (now - lastProgressReport > 100 || totalBytesRead == totalLength) {
                        lastProgressReport = now
                        if (totalLength > 0) {
                            val progress = (totalBytesRead.toFloat() / totalLength.toFloat()).coerceIn(0.05f, 0.99f)
                            onProgress(progress)
                        } else {
                            onProgress(0.5f)
                        }
                    }
                }
                output.flush()
            }
        }
        finalConn.disconnect()

        onProgress(1.0f)
        targetFile.absolutePath
    }
}
