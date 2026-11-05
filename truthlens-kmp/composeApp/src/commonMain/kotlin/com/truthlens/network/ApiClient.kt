package com.truthlens.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class ApiClient {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
    }

    private val baseUrl = "http://localhost:8088/api" // Adjust for emulator (10.0.2.2) or actual device IP later

    suspend fun checkHealth(): HealthResponse {
        return client.get("$baseUrl/health").body()
    }

    suspend fun detect(fileBytes: ByteArray, fileName: String, mimeType: String): DetectResponse {
        return client.submitFormWithBinaryData(
            url = "$baseUrl/detect",
            formData = formData {
                append("file", fileBytes, Headers.build {
                    append(HttpHeaders.ContentType, mimeType)
                    append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                })
            }
        ).body()
    }
}