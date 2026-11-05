package com.truthlens.ui.screens.profile

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.truthlens.ui.components.TendrilsCard
import com.truthlens.ui.model.UgandanLanguageDirectory
import com.truthlens.ui.model.UgandanLanguage
import com.truthlens.ui.theme.*

/**
 * Shared Modal Dialog wrapper for Settings Sub-Screens
 */
@Composable
fun SettingsSubScreenDialog(
    title: String,
    icon: ImageVector,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
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
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = title,
                    style = TendrilsTheme.typography.h3,
                    color = TendrilsTheme.colors.textPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                content = content
            )
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onDismiss,
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = TendrilsTheme.colors.primary,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Done", style = TendrilsTheme.typography.button)
                }
            }
        },
        backgroundColor = TendrilsTheme.colors.surface,
        shape = TendrilsTheme.shapes.large,
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}

// ==========================================
// 1. LANGUAGE & VOICE SETTINGS
// ==========================================
@Composable
fun LanguageVoiceSettingsDialog(onDismiss: () -> Unit) {
    var selectedLang by remember { mutableStateOf("English") }
    var selectedRegion by remember { mutableStateOf("All") }
    var selectedVoice by remember { mutableStateOf("Gentle Sprout (Warm & Calm)") }
    var speechSpeed by remember { mutableStateOf(1.0f) }
    var voiceMode by remember { mutableStateOf("Text and Voice") }

    val regions = listOf("All", "Central", "Eastern", "Northern", "Western", "National")

    val displayedLanguages = remember(selectedRegion) {
        if (selectedRegion == "All") {
            UgandanLanguageDirectory.languages
        } else {
            UgandanLanguageDirectory.languages.filter { it.region.contains(selectedRegion, ignoreCase = true) }
        }
    }

    val activeUgLang = UgandanLanguageDirectory.languages.find { it.name.equals(selectedLang, ignoreCase = true) }
        ?: UgandanLanguageDirectory.languages.first()

    SettingsSubScreenDialog(
        title = "Language & Voice",
        icon = Icons.Default.Info,
        onDismiss = onDismiss
    ) {
        Text(
            text = "Primary Ugandan Language (17 Supported)",
            style = TendrilsTheme.typography.subtitle1,
            fontWeight = FontWeight.SemiBold,
            color = TendrilsTheme.colors.textPrimary
        )

        // Region Filter Chips
        androidx.compose.foundation.lazy.LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        ) {
            items(regions.size) { idx ->
                val reg = regions[idx]
                val isSel = selectedRegion == reg
                Box(
                    modifier = Modifier
                        .clip(PillShape)
                        .background(if (isSel) TendrilsTheme.colors.primary else TendrilsTheme.colors.surfaceElevated)
                        .clickable { selectedRegion = reg }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = reg,
                        style = TendrilsTheme.typography.caption,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSel) Color.White else TendrilsTheme.colors.textSecondary
                    )
                }
            }
        }

        // Language selection scrollable list
        androidx.compose.foundation.lazy.LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        ) {
            items(displayedLanguages.size) { index ->
                val ugLang = displayedLanguages[index]
                val isSel = selectedLang == ugLang.name
                TendrilsCard(
                    shape = PillShape,
                    padding = 8.dp,
                    backgroundColor = if (isSel) TendrilsTheme.colors.primary.copy(alpha = 0.16f) else TendrilsTheme.colors.surfaceElevated,
                    borderColor = if (isSel) TendrilsTheme.colors.primary else TendrilsTheme.colors.border,
                    onClick = { selectedLang = ugLang.name }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    ) {
                        Text(
                            text = ugLang.name,
                            style = TendrilsTheme.typography.caption,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (isSel) TendrilsTheme.colors.primary else TendrilsTheme.colors.textPrimary
                        )
                        Text(
                            text = ugLang.nativeName,
                            style = TendrilsTheme.typography.caption,
                            fontSize = 10.sp,
                            color = if (isSel) TendrilsTheme.colors.primary.copy(alpha = 0.8f) else TendrilsTheme.colors.textMuted
                        )
                    }
                }
            }
        }

        // Selected Language Greeting Preview
        TendrilsCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = TendrilsTheme.colors.surfaceElevated,
            padding = 10.dp
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = TendrilsTheme.colors.primary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "${activeUgLang.name} • ${activeUgLang.region}",
                        style = TendrilsTheme.typography.caption,
                        fontWeight = FontWeight.Bold,
                        color = TendrilsTheme.colors.primary
                    )
                    Text(
                        text = "\"${activeUgLang.sampleGreeting}\"",
                        style = TendrilsTheme.typography.body2,
                        color = TendrilsTheme.colors.textPrimary
                    )
                }
            }
        }

        Divider(color = TendrilsTheme.colors.border)

        Text("Companion Voice Persona", style = TendrilsTheme.typography.subtitle1, fontWeight = FontWeight.SemiBold, color = TendrilsTheme.colors.textPrimary)
        val voices = listOf("Gentle Sprout (Warm & Calm)", "Morning Light (Clear & Energetic)", "Deep Root (Quiet & Reassuring)")
        voices.forEach { voice ->
            val isSel = selectedVoice == voice
            TendrilsCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = if (isSel) TendrilsTheme.colors.primary.copy(alpha = 0.12f) else TendrilsTheme.colors.surfaceElevated,
                borderColor = if (isSel) TendrilsTheme.colors.primary else TendrilsTheme.colors.border,
                padding = 10.dp,
                onClick = { selectedVoice = voice }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = isSel,
                        onClick = { selectedVoice = voice },
                        colors = RadioButtonDefaults.colors(selectedColor = TendrilsTheme.colors.primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = voice, style = TendrilsTheme.typography.body2, color = TendrilsTheme.colors.textPrimary)
                }
            }
        }

        Divider(color = TendrilsTheme.colors.border)

        val speedDisplay = ((speechSpeed * 10).toInt() / 10f).toString()
        Text("Speech Speed: ${speedDisplay}x", style = TendrilsTheme.typography.subtitle1, fontWeight = FontWeight.SemiBold, color = TendrilsTheme.colors.textPrimary)
        Slider(
            value = speechSpeed,
            onValueChange = { speechSpeed = it },
            valueRange = 0.75f..1.5f,
            steps = 2,
            colors = SliderDefaults.colors(thumbColor = TendrilsTheme.colors.primary, activeTrackColor = TendrilsTheme.colors.primary)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Native TTS Audio Preview Button
        Button(
            onClick = {
                val sampleGreeting = activeUgLang.sampleGreeting
                com.truthlens.voice.TendrilsTts.speak(
                    text = sampleGreeting,
                    languageCode = activeUgLang.code,
                    speed = speechSpeed
                )
            },
            modifier = Modifier.fillMaxWidth().height(42.dp),
            shape = PillShape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = TendrilsTheme.colors.surfaceElevated,
                contentColor = TendrilsTheme.colors.primary
            )
        ) {
            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Preview Voice (Native TTS Engine)", style = TendrilsTheme.typography.caption, fontWeight = FontWeight.Bold)
        }
    }
}

// ==========================================
// 2. PRIVACY & LOCAL DATA SETTINGS
// ==========================================
@Composable
fun PrivacySettingsDialog(
    onDismiss: () -> Unit,
    onClearAllData: () -> Unit
) {
    var rememberPreferences by remember { mutableStateOf(true) }
    var localHistoryLogging by remember { mutableStateOf(true) }
    var preferLocalAiOnly by remember { mutableStateOf(false) }

    SettingsSubScreenDialog(
        title = "Privacy & Local Data",
        icon = Icons.Default.Check,
        onDismiss = onDismiss
    ) {
        TendrilsCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = TendrilsPalette.Sprout500.copy(alpha = 0.12f),
            borderColor = TendrilsPalette.Sprout500.copy(alpha = 0.4f),
            padding = 12.dp
        ) {
            Text(
                text = "Tendrils is designed privacy-first: all conversations, preferences, and personal memory are stored locally on your device only.",
                style = TendrilsTheme.typography.caption,
                color = TendrilsTheme.colors.textPrimary
            )
        }

        SettingToggleItem(
            title = "Personal Memory",
            description = "Allow Tendrils to remember your topic preferences across sessions locally.",
            checked = rememberPreferences,
            onCheckedChange = { rememberPreferences = it }
        )

        SettingToggleItem(
            title = "Keep Chat History",
            description = "Save exploration threads locally in your Activity tab.",
            checked = localHistoryLogging,
            onCheckedChange = { localHistoryLogging = it }
        )

        SettingToggleItem(
            title = "Strictly Local Processing",
            description = "Do not connect to remote servers for any query, even for link inspection.",
            checked = preferLocalAiOnly,
            onCheckedChange = { preferLocalAiOnly = it }
        )

        Divider(color = TendrilsTheme.colors.border)

        Button(
            onClick = onClearAllData,
            modifier = Modifier.fillMaxWidth(),
            shape = PillShape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = TendrilsPalette.Coral500.copy(alpha = 0.15f),
                contentColor = TendrilsPalette.Coral500
            )
        ) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Clear All Local Data from Device", style = TendrilsTheme.typography.button)
        }
    }
}

// ==========================================
// 3. PERMISSIONS OVERVIEW (Truthful System Presentation)
// ==========================================
@Composable
fun PermissionsSettingsDialog(onDismiss: () -> Unit) {
    SettingsSubScreenDialog(
        title = "System Permissions",
        icon = Icons.Default.Star,
        onDismiss = onDismiss
    ) {
        Text(
            text = "Permissions are requested only when you first use the corresponding feature.",
            style = TendrilsTheme.typography.body2,
            color = TendrilsTheme.colors.textSecondary
        )

        PermissionRowItem(
            permissionName = "Microphone",
            description = "Enables spoken queries and conversational voice mode (Flow B).",
            status = "Optional • Not Requested",
            icon = Icons.Default.Share
        )

        PermissionRowItem(
            permissionName = "Camera & Photos",
            description = "Used to analyze pictures and spot manipulation artifacts (Flow D).",
            status = "Optional • Not Requested",
            icon = Icons.Default.Search
        )

        PermissionRowItem(
            permissionName = "Notifications",
            description = "Enables gentle reminders for study intervals and daily goals.",
            status = "Optional • Allowed",
            icon = Icons.Default.Notifications
        )

        PermissionRowItem(
            permissionName = "Files & Documents",
            description = "Allows reading PDFs and study materials for summarisation (Flow E).",
            status = "Optional • Not Requested",
            icon = Icons.Default.List
        )
    }
}

// ==========================================
// 4. FAMILY EXPERIENCE SETTINGS
// ==========================================
@Composable
fun FamilySettingsDialog(onDismiss: () -> Unit) {
    var familyRole by remember { mutableStateOf("Parent View") }

    SettingsSubScreenDialog(
        title = "Family Experience",
        icon = Icons.Default.Person,
        onDismiss = onDismiss
    ) {
        Text(
            text = "Transparent pairing for guardians, teens, and children without hidden surveillance or covert tracking.",
            style = TendrilsTheme.typography.body2,
            color = TendrilsTheme.colors.textSecondary
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Parent View", "Child/Teen View").forEach { role ->
                val isSel = familyRole == role
                TendrilsCard(
                    modifier = Modifier.weight(1f),
                    shape = PillShape,
                    padding = 8.dp,
                    backgroundColor = if (isSel) TendrilsTheme.colors.primary.copy(alpha = 0.16f) else TendrilsTheme.colors.surfaceElevated,
                    borderColor = if (isSel) TendrilsTheme.colors.primary else TendrilsTheme.colors.border,
                    onClick = { familyRole = role }
                ) {
                    Text(
                        text = role,
                        style = TendrilsTheme.typography.caption,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSel) TendrilsTheme.colors.primary else TendrilsTheme.colors.textSecondary
                    )
                }
            }
        }

        Divider(color = TendrilsTheme.colors.border)

        if (familyRole == "Parent View") {
            Text("Parent Dashboard (UI Foundation)", style = TendrilsTheme.typography.subtitle1, fontWeight = FontWeight.SemiBold, color = TendrilsTheme.colors.textPrimary)
            FamilyControlRow("Content Filter Tier", "Curated for Children (Age 10-14)")
            FamilyControlRow("Digital Safety Guidance", "High (Highlights suspicious media)")
            FamilyControlRow("Pairing Status", "1 Child Device Linked")
        } else {
            Text("Child / Teen Privacy View", style = TendrilsTheme.typography.subtitle1, fontWeight = FontWeight.SemiBold, color = TendrilsTheme.colors.textPrimary)
            TendrilsCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = TendrilsTheme.colors.surfaceElevated,
                padding = 12.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("• Your parent can see agreed safety tiers, but not private chat secrets.", style = TendrilsTheme.typography.caption, color = TendrilsTheme.colors.textSecondary)
                    Text("• Content filters are clearly visible and can be discussed together.", style = TendrilsTheme.typography.caption, color = TendrilsTheme.colors.textSecondary)
                }
            }
        }
    }
}

// ==========================================
// 5. ACCESSIBILITY SETTINGS
// ==========================================
@Composable
fun AccessibilitySettingsDialog(onDismiss: () -> Unit) {
    var largerText by remember { mutableStateOf(false) }
    var reducedMotion by remember { mutableStateOf(false) }
    var highContrast by remember { mutableStateOf(false) }

    SettingsSubScreenDialog(
        title = "Accessibility",
        icon = Icons.Default.Favorite,
        onDismiss = onDismiss
    ) {
        SettingToggleItem(
            title = "Larger Text Scale",
            description = "Increases body and caption text sizes for reading comfort.",
            checked = largerText,
            onCheckedChange = { largerText = it }
        )

        SettingToggleItem(
            title = "Reduced Motion",
            description = "Disables continuous pulsating tendril animations to prevent motion sensitivity.",
            checked = reducedMotion,
            onCheckedChange = { reducedMotion = it }
        )

        SettingToggleItem(
            title = "High Contrast Mode",
            description = "Enhances card borders and text contrast ratios.",
            checked = highContrast,
            onCheckedChange = { highContrast = it }
        )
    }
}

// ==========================================
// 6. EDIT PROFILE DIALOG
// ==========================================
@Composable
fun EditProfileDialog(
    currentName: String,
    currentExperience: com.truthlens.ui.model.ExperienceMode = com.truthlens.ui.model.ExperienceMode.PERSONAL,
    onSaveName: (String) -> Unit,
    onSaveExperience: (com.truthlens.ui.model.ExperienceMode) -> Unit = {},
    onDismiss: () -> Unit
) {
    var nameInput by remember { mutableStateOf(currentName) }
    var experienceInput by remember { mutableStateOf(currentExperience) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Edit Profile", style = TendrilsTheme.typography.h3, color = TendrilsTheme.colors.textPrimary)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Choose the display name Tendrils uses when greeting you.",
                    style = TendrilsTheme.typography.body2,
                    color = TendrilsTheme.colors.textSecondary
                )
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Display Name") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = TendrilsTheme.colors.textPrimary,
                        focusedBorderColor = TendrilsTheme.colors.primary,
                        unfocusedBorderColor = TendrilsTheme.colors.border
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Experience Mode",
                    style = TendrilsTheme.typography.subtitle1,
                    fontWeight = FontWeight.SemiBold,
                    color = TendrilsTheme.colors.textPrimary
                )

                com.truthlens.ui.model.ExperienceMode.entries.forEach { mode ->
                    val isSelected = experienceInput == mode
                    TendrilsCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = if (isSelected) TendrilsTheme.colors.primary.copy(alpha = 0.12f) else TendrilsTheme.colors.surfaceElevated,
                        borderColor = if (isSelected) TendrilsTheme.colors.primary else TendrilsTheme.colors.border,
                        padding = 10.dp,
                        onClick = { experienceInput = mode }
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = mode.title,
                                    style = TendrilsTheme.typography.body2,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) TendrilsTheme.colors.primary else TendrilsTheme.colors.textPrimary
                                )
                                if (isSelected) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = TendrilsTheme.colors.primary, modifier = Modifier.size(16.dp))
                                }
                            }
                            Text(
                                text = mode.subtitle,
                                style = TendrilsTheme.typography.caption,
                                color = TendrilsTheme.colors.textMuted
                            )
                        }
                    }
                }
            }
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = TendrilsTheme.colors.textMuted)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (nameInput.isNotBlank()) {
                            onSaveName(nameInput.trim())
                        }
                        onSaveExperience(experienceInput)
                        onDismiss()
                    },
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = TendrilsTheme.colors.primary,
                        contentColor = Color.White
                    )
                ) {
                    Text("Save")
                }
            }
        },
        backgroundColor = TendrilsTheme.colors.surface,
        shape = TendrilsTheme.shapes.medium
    )
}

// Helper components
@Composable
private fun SettingToggleItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = TendrilsTheme.typography.body1, fontWeight = FontWeight.SemiBold, color = TendrilsTheme.colors.textPrimary)
            Text(text = description, style = TendrilsTheme.typography.caption, color = TendrilsTheme.colors.textSecondary)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = TendrilsTheme.colors.primary,
                checkedTrackColor = TendrilsTheme.colors.primary.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
private fun PermissionRowItem(
    permissionName: String,
    description: String,
    status: String,
    icon: ImageVector
) {
    TendrilsCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = TendrilsTheme.colors.surfaceElevated,
        padding = 12.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = TendrilsTheme.colors.primary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = permissionName, style = TendrilsTheme.typography.body2, fontWeight = FontWeight.SemiBold, color = TendrilsTheme.colors.textPrimary)
                    Box(
                        modifier = Modifier
                            .background(TendrilsTheme.colors.surface, PillShape)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(text = status, style = TendrilsTheme.typography.caption, color = TendrilsTheme.colors.textMuted, fontSize = 10.sp)
                    }
                }
                Text(text = description, style = TendrilsTheme.typography.caption, color = TendrilsTheme.colors.textSecondary)
            }
        }
    }
}

@Composable
private fun FamilyControlRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = TendrilsTheme.typography.body2, color = TendrilsTheme.colors.textPrimary)
        Text(text = value, style = TendrilsTheme.typography.caption, fontWeight = FontWeight.Bold, color = TendrilsTheme.colors.primary)
    }
}
