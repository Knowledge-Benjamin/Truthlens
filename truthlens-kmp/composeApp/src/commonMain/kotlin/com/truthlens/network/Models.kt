package com.truthlens.network

import kotlinx.serialization.Serializable

@Serializable
data class DetectResponse(
    val id: String,
    val filename: String,
    val fileType: String,
    val prediction: String,
    val confidence: Double,
    val fakeScore: Double,
    val realScore: Double,
    val analysisDetails: List<AnalysisDetail>,
    val timestamp: String,
    val modelUsed: String,
    val processingTime: Long
)

@Serializable
data class AnalysisDetail(
    val label: String,
    val score: Double,
    val description: String,
    val icon: String
)

@Serializable
data class HealthResponse(
    val status: String,
    val visual_model: String,
    val audio_model: String,
    val timestamp: String
)