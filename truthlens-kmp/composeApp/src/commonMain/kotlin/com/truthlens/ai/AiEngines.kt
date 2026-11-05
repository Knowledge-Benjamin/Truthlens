package com.truthlens.ai

data class TfliteInspectionResult(
    val isDeepfake: Boolean,
    val fakeProbability: Float,
    val c2paVerified: Boolean,
    val summary: String,
    val executionTimeMs: Long
)

data class OcrExtractionResult(
    val extractedText: String,
    val blockCount: Int,
    val lineCount: Int,
    val executionTimeMs: Long
)

data class VisionAnalysisResult(
    val detectedObjects: List<String>,
    val sceneDescription: String,
    val extractedText: String?,
    val isAuthentic: Boolean,
    val confidence: Float,
    val executionTimeMs: Long
)

expect object NativeAiBridge {
    suspend fun runTfliteInspection(mediaName: String): TfliteInspectionResult
    suspend fun runOcrExtraction(documentName: String): OcrExtractionResult
    suspend fun runVisionAnalysis(imageName: String): VisionAnalysisResult
}
