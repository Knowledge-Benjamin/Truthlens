package com.truthlens.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.truthlens.ui.components.TendrilsCard
import com.truthlens.ui.model.ExperienceMode
import com.truthlens.ui.model.InteractionMode
import com.truthlens.ui.theme.*

/**
 * 5-Step Onboarding Flow (Screens A to E)
 * A: Welcome
 * B: Choose your experience
 * C: Personalisation
 * D: Privacy introduction
 * E: Enter the application
 */
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit
) {
    var currentStep by remember { mutableStateOf(0) }
    var selectedExperience by remember { mutableStateOf(ExperienceMode.PERSONAL) }
    var selectedLanguage by remember { mutableStateOf("English") }
    var interactionMode by remember { mutableStateOf(InteractionMode.TEXT_AND_VOICE) }
    val themeModeState = LocalThemeMode.current
    var selectedInterests by remember {
        mutableStateOf(setOf("Science & Nature", "Digital Safety", "General Curiosity"))
    }

    val totalSteps = 5

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TendrilsTheme.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // --- TOP BAR: BACK & SKIP ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentStep > 0) {
                IconButton(onClick = { currentStep-- }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TendrilsTheme.colors.textPrimary
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(48.dp))
            }

            // Step Progress Dots
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                for (i in 0 until totalSteps) {
                    val isCurrent = i == currentStep
                    val isPassed = i < currentStep
                    Box(
                        modifier = Modifier
                            .height(6.dp)
                            .width(if (isCurrent) 24.dp else 6.dp)
                            .clip(PillShape)
                            .background(
                                when {
                                    isCurrent -> TendrilsTheme.colors.primary
                                    isPassed -> TendrilsTheme.colors.primary.copy(alpha = 0.4f)
                                    else -> TendrilsTheme.colors.border
                                }
                            )
                    )
                }
            }

            if (currentStep < totalSteps - 1) {
                TextButton(onClick = onComplete) {
                    Text(
                        text = "Skip",
                        color = TendrilsTheme.colors.textMuted,
                        style = TendrilsTheme.typography.caption
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(48.dp))
            }
        }

        // --- STEP CONTENT AREA ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            when (currentStep) {
                0 -> WelcomeStep(onNext = { currentStep = 1 })
                1 -> ExperienceStep(
                    selected = selectedExperience,
                    onSelect = { selectedExperience = it },
                    onNext = { currentStep = 2 }
                )
                2 -> PersonalisationStep(
                    language = selectedLanguage,
                    onLanguageChange = { selectedLanguage = it },
                    mode = interactionMode,
                    onModeChange = { interactionMode = it },
                    selectedInterests = selectedInterests,
                    onToggleInterest = { interest ->
                        selectedInterests = if (selectedInterests.contains(interest)) {
                            selectedInterests - interest
                        } else {
                            selectedInterests + interest
                        }
                    },
                    currentTheme = themeModeState.value,
                    onThemeChange = { themeModeState.value = it },
                    onNext = { currentStep = 3 }
                )
                3 -> PrivacyStep(onNext = { currentStep = 4 })
                4 -> EnterAppStep(
                    experience = selectedExperience,
                    language = selectedLanguage,
                    onEnter = onComplete
                )
            }
        }
    }
}

// ==========================================
// SCREEN A: WELCOME
// ==========================================
@Composable
private fun WelcomeStep(onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Botanical living tendril orb
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(TendrilsTheme.colors.primary.copy(alpha = 0.12f))
                .border(2.dp, TendrilsTheme.colors.primary.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = TendrilsTheme.colors.primary,
                modifier = Modifier.size(56.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Welcome to Tendrils",
            style = TendrilsTheme.typography.h1,
            color = TendrilsTheme.colors.textPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Your intelligent, friendly companion designed for curiosity, calm exploration, and digital confidence.",
            style = TendrilsTheme.typography.body1,
            color = TendrilsTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = PillShape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = TendrilsTheme.colors.primary,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Get Started",
                style = TendrilsTheme.typography.button
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ==========================================
// SCREEN B: CHOOSE YOUR EXPERIENCE
// ==========================================
@Composable
private fun ExperienceStep(
    selected: ExperienceMode,
    onSelect: (ExperienceMode) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Choose Your Experience",
            style = TendrilsTheme.typography.h2,
            color = TendrilsTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "This sets initial presentation tone and depth. You can change this at any time.",
            style = TendrilsTheme.typography.body2,
            color = TendrilsTheme.colors.textSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        ExperienceMode.values().forEach { mode ->
            val isSelected = selected == mode
            TendrilsCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = if (isSelected) TendrilsTheme.colors.primary.copy(alpha = 0.12f) else TendrilsTheme.colors.surface,
                borderColor = if (isSelected) TendrilsTheme.colors.primary else TendrilsTheme.colors.border,
                borderWidth = if (isSelected) 2.dp else 1.dp,
                padding = 16.dp,
                onClick = { onSelect(mode) }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelect(mode) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = TendrilsTheme.colors.primary,
                            unselectedColor = TendrilsTheme.colors.border
                        )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = mode.title,
                            style = TendrilsTheme.typography.subtitle1,
                            fontWeight = FontWeight.SemiBold,
                            color = TendrilsTheme.colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = mode.subtitle,
                            style = TendrilsTheme.typography.body2,
                            color = TendrilsTheme.colors.textSecondary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        TendrilsCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = TendrilsTheme.colors.surfaceElevated,
            borderColor = TendrilsTheme.colors.border,
            padding = 12.dp
        ) {
            Text(
                text = "Notice: These are initial guidance preferences, not verified age checks or identity tracking.",
                style = TendrilsTheme.typography.caption,
                color = TendrilsTheme.colors.textMuted
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = PillShape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = TendrilsTheme.colors.primary,
                contentColor = Color.White
            )
        ) {
            Text(text = "Continue", style = TendrilsTheme.typography.button)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

// ==========================================
// SCREEN C: PERSONALISATION
// ==========================================
@Composable
private fun PersonalisationStep(
    language: String,
    onLanguageChange: (String) -> Unit,
    mode: InteractionMode,
    onModeChange: (InteractionMode) -> Unit,
    selectedInterests: Set<String>,
    onToggleInterest: (String) -> Unit,
    currentTheme: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    onNext: () -> Unit
) {
    val languages = listOf("English", "Kiswahili", "French", "Spanish")
    val allInterests = listOf(
        "Science & Nature", "Homework & Study", "Creative Writing",
        "Digital Safety", "Coding & Logic", "General Curiosity", "History"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Personalisation",
            style = TendrilsTheme.typography.h2,
            color = TendrilsTheme.colors.textPrimary
        )
        Text(
            text = "Tailor how Tendrils communicates and looks.",
            style = TendrilsTheme.typography.body2,
            color = TendrilsTheme.colors.textSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Language
        Text(
            text = "Preferred Language",
            style = TendrilsTheme.typography.subtitle1,
            fontWeight = FontWeight.SemiBold,
            color = TendrilsTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            languages.forEach { lang ->
                val isSel = language == lang
                TendrilsCard(
                    modifier = Modifier.weight(1f),
                    shape = PillShape,
                    padding = 8.dp,
                    backgroundColor = if (isSel) TendrilsTheme.colors.primary.copy(alpha = 0.15f) else TendrilsTheme.colors.surface,
                    borderColor = if (isSel) TendrilsTheme.colors.primary else TendrilsTheme.colors.border,
                    onClick = { onLanguageChange(lang) }
                ) {
                    Text(
                        text = lang,
                        style = TendrilsTheme.typography.caption,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSel) TendrilsTheme.colors.primary else TendrilsTheme.colors.textSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Appearance
        Text(
            text = "Appearance",
            style = TendrilsTheme.typography.subtitle1,
            fontWeight = FontWeight.SemiBold,
            color = TendrilsTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThemeMode.values().forEach { tm ->
                val isSel = currentTheme == tm
                TendrilsCard(
                    modifier = Modifier.weight(1f),
                    shape = PillShape,
                    padding = 8.dp,
                    backgroundColor = if (isSel) TendrilsTheme.colors.primary.copy(alpha = 0.15f) else TendrilsTheme.colors.surface,
                    borderColor = if (isSel) TendrilsTheme.colors.primary else TendrilsTheme.colors.border,
                    onClick = { onThemeChange(tm) }
                ) {
                    Text(
                        text = tm.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = TendrilsTheme.typography.caption,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSel) TendrilsTheme.colors.primary else TendrilsTheme.colors.textSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Interests
        Text(
            text = "Topics of Interest (Optional)",
            style = TendrilsTheme.typography.subtitle1,
            fontWeight = FontWeight.SemiBold,
            color = TendrilsTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                allInterests.chunked(2).forEach { rowItems ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowItems.forEach { interest ->
                            val isSel = selectedInterests.contains(interest)
                            TendrilsCard(
                                shape = PillShape,
                                padding = 8.dp,
                                backgroundColor = if (isSel) TendrilsPalette.Sprout500.copy(alpha = 0.16f) else TendrilsTheme.colors.surface,
                                borderColor = if (isSel) TendrilsPalette.Sprout500 else TendrilsTheme.colors.border,
                                onClick = { onToggleInterest(interest) }
                            ) {
                                Text(
                                    text = interest,
                                    style = TendrilsTheme.typography.caption,
                                    color = if (isSel) TendrilsTheme.colors.primary else TendrilsTheme.colors.textSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = PillShape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = TendrilsTheme.colors.primary,
                contentColor = Color.White
            )
        ) {
            Text(text = "Next: Privacy", style = TendrilsTheme.typography.button)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

// ==========================================
// SCREEN D: PRIVACY INTRODUCTION
// ==========================================
@Composable
private fun PrivacyStep(onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Your Privacy Comes First",
            style = TendrilsTheme.typography.h2,
            color = TendrilsTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Tendrils is designed to be safe, transparent, and respectful of your data.",
            style = TendrilsTheme.typography.body2,
            color = TendrilsTheme.colors.textSecondary
        )

        Spacer(modifier = Modifier.height(18.dp))

        PrivacyPillar(
            icon = Icons.Default.Check,
            title = "What stays on your device",
            description = "Chat history, personal preferences, and saved notes remain stored locally on your device. You can clear them anytime."
        )

        PrivacyPillar(
            icon = Icons.Default.Star,
            title = "Permissions are strictly on-demand",
            description = "Tendrils does not ask for camera, microphone, or storage permissions now. They will only be requested when you tap a feature that needs them."
        )

        PrivacyPillar(
            icon = Icons.Default.Info,
            title = "When internet is used",
            description = "Analyzing remote links or querying global knowledge sends only the required context. Personal identifiers are never sent."
        )

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = PillShape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = TendrilsTheme.colors.primary,
                contentColor = Color.White
            )
        ) {
            Text(text = "I Understand", style = TendrilsTheme.typography.button)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun PrivacyPillar(
    icon: ImageVector,
    title: String,
    description: String
) {
    TendrilsCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        backgroundColor = TendrilsTheme.colors.surface,
        borderColor = TendrilsTheme.colors.border,
        padding = 14.dp
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(TendrilsTheme.colors.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TendrilsTheme.colors.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = TendrilsTheme.typography.subtitle1,
                    fontWeight = FontWeight.SemiBold,
                    color = TendrilsTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = TendrilsTheme.typography.body2,
                    color = TendrilsTheme.colors.textSecondary
                )
            }
        }
    }
}

// ==========================================
// SCREEN E: ENTER THE APPLICATION
// ==========================================
@Composable
private fun EnterAppStep(
    experience: ExperienceMode,
    language: String,
    onEnter: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(TendrilsPalette.Emerald400.copy(alpha = 0.16f))
                .border(2.dp, TendrilsPalette.Emerald400, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = TendrilsPalette.Emerald400,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "You're All Set!",
            style = TendrilsTheme.typography.h1,
            color = TendrilsTheme.colors.textPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your Tendrils space is configured for ${experience.title} in $language.",
            style = TendrilsTheme.typography.body1,
            color = TendrilsTheme.colors.textSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        TendrilsCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = TendrilsTheme.colors.surface,
            padding = 16.dp
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Quick Companion Tips",
                    style = TendrilsTheme.typography.subtitle1,
                    fontWeight = FontWeight.SemiBold,
                    color = TendrilsTheme.colors.textPrimary
                )
                Text(
                    text = "• Tap the microphone anytime for spoken exploration.",
                    style = TendrilsTheme.typography.body2,
                    color = TendrilsTheme.colors.textSecondary
                )
                Text(
                    text = "• Tap suggestion pills to kick off guided lessons.",
                    style = TendrilsTheme.typography.body2,
                    color = TendrilsTheme.colors.textSecondary
                )
                Text(
                    text = "• Use Discover tab to test suspicious links or read documents.",
                    style = TendrilsTheme.typography.body2,
                    color = TendrilsTheme.colors.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = onEnter,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = PillShape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = TendrilsTheme.colors.primary,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Enter Tendrils Companion",
                style = TendrilsTheme.typography.button
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
