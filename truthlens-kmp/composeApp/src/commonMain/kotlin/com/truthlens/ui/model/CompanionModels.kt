package com.truthlens.ui.model

import androidx.compose.ui.graphics.Color
import com.truthlens.ui.theme.TendrilsPalette

/**
 * 8 Distinct Assistant States mandated by the specification.
 * Each state has a distinct title, description, color, and visual aura style.
 */
enum class CompanionState(
    val label: String,
    val description: String,
    val primaryColor: Color,
    val auraColor: Color
) {
    IDLE(
        label = "Idle",
        description = "Ready and listening",
        primaryColor = TendrilsPalette.Sprout500,
        auraColor = TendrilsPalette.Sprout500.copy(alpha = 0.14f)
    ),
    LISTENING(
        label = "Listening",
        description = "Listening to your voice...",
        primaryColor = TendrilsPalette.Indigo400,
        auraColor = TendrilsPalette.Indigo400.copy(alpha = 0.22f)
    ),
    THINKING(
        label = "Thinking",
        description = "Considering your thoughts...",
        primaryColor = TendrilsPalette.Violet400,
        auraColor = TendrilsPalette.Violet400.copy(alpha = 0.25f)
    ),
    SPEAKING(
        label = "Speaking",
        description = "Sharing explanation...",
        primaryColor = TendrilsPalette.Teal400,
        auraColor = TendrilsPalette.Teal400.copy(alpha = 0.22f)
    ),
    PERFORMING_TASK(
        label = "Performing Task",
        description = "Executing requested action...",
        primaryColor = TendrilsPalette.Amber500,
        auraColor = TendrilsPalette.Amber500.copy(alpha = 0.24f)
    ),
    WAITING_FOR_PERMISSION(
        label = "Waiting for Permission",
        description = "Needs your confirmation to continue",
        primaryColor = TendrilsPalette.Amber600,
        auraColor = TendrilsPalette.Amber600.copy(alpha = 0.25f)
    ),
    COMPLETED(
        label = "Completed",
        description = "Action completed successfully",
        primaryColor = TendrilsPalette.Emerald400,
        auraColor = TendrilsPalette.Emerald400.copy(alpha = 0.20f)
    ),
    ERROR(
        label = "Error",
        description = "Unable to complete • Tap to retry",
        primaryColor = TendrilsPalette.Coral500,
        auraColor = TendrilsPalette.Coral500.copy(alpha = 0.25f)
    )
}

enum class ExperienceMode(val title: String, val subtitle: String) {
    PERSONAL("Personal Use", "Curious everyday exploration, learning, and productivity."),
    TEEN("Teen Experience", "Study tools, homework support, and digital safety guidance."),
    CHILD("Child Experience", "Safe, guided curiosity with transparent parent involvement.")
}

enum class InteractionMode {
    TEXT_AND_VOICE, TEXT_ONLY, VOICE_PREFERRED
}

data class ChatMessage(
    val id: String = "",
    val role: Role,
    val content: String,
    val timestamp: String = "Just now",
    val followUps: List<String> = emptyList(),
    val isStreaming: Boolean = false
) {
    enum class Role {
        USER, ASSISTANT, SYSTEM
    }
}
