package com.truthlens.ai

actual object NativeAiBridge {
    actual suspend fun runTfliteInspection(mediaName: String): TfliteInspectionResult {
        return TfliteInspectionResult(
            isDeepfake = true,
            fakeProbability = 0.88f,
            c2paVerified = false,
            summary = "Desktop preview: TFLite engine is active for Android arm64/x86_64 target.",
            executionTimeMs = 120
        )
    }

    actual suspend fun runOcrExtraction(documentName: String): OcrExtractionResult {
        val notes = """
ENERGY CONSERVATION PRINCIPLES (Chapter 4)
• Total energy in an isolated system remains constant.
• Energy cannot be created or destroyed; it transforms from one form into another.
• Formula: ΔE = Q - W (First Law of Thermodynamics)
        """.trimIndent()
        return OcrExtractionResult(
            extractedText = notes,
            blockCount = 3,
            lineCount = 5,
            executionTimeMs = 85
        )
    }
}
