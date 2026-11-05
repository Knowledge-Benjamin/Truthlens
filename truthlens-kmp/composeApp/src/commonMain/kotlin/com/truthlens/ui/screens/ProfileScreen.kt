package com.truthlens.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.truthlens.ui.model.ExperienceMode
import com.truthlens.ui.model.LocalOnboardingController
import com.truthlens.ui.screens.flows.*
import com.truthlens.ui.screens.profile.*
import com.truthlens.ui.theme.LocalThemeMode
import com.truthlens.ui.theme.PillShape
import com.truthlens.ui.theme.TendrilsTheme
import com.truthlens.ui.theme.ThemeMode

/**
 * ProfileScreen — User Profile & Settings Hub
 * Milestone 4 & 5 Implementation:
 * - Profile header with editable display name and ExperienceMode
 * - Live System / Light / Dark theme switcher
 * - Detailed sub-screens: Language & Voice, Privacy & Local Data,
 *   Security & Protection (Background media scanner, C2PA, Phishing),
 *   Model Manager Lab (downloads/tendrils/models, URL downloader, gated in Child mode),
 *   Permissions overview, Family experience, Accessibility
 * - Interactive Feature Flow launch shortcuts (Flows C through H)
 */
@Composable
fun ProfileScreen() {
    val themeModeState = LocalThemeMode.current
    val onboardingController = LocalOnboardingController.current

    var displayName by remember { mutableStateOf("Explorer") }
    var selectedExperience by remember { mutableStateOf(ExperienceMode.PERSONAL) }
    var activeDialog by remember { mutableStateOf<String?>(null) }
    var showClearDataConfirmation by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TendrilsTheme.colors.background)
            .statusBarsPadding()
    ) {
        // --- HEADER ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Text(
                text = "Profile & Settings",
                style = TendrilsTheme.typography.h1,
                color = TendrilsTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Personalise your companion and manage preferences",
                style = TendrilsTheme.typography.body2,
                color = TendrilsTheme.colors.textSecondary
            )
        }

        // --- SCROLLABLE CONTENT ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Header Card
            TendrilsCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = TendrilsTheme.colors.surface,
                padding = 16.dp,
                onClick = { activeDialog = "edit_profile" }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(TendrilsTheme.colors.primary.copy(alpha = 0.2f))
                            .border(2.dp, TendrilsTheme.colors.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = displayName.take(1).uppercase(),
                            style = TendrilsTheme.typography.h2,
                            color = TendrilsTheme.colors.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = displayName,
                            style = TendrilsTheme.typography.h3,
                            color = TendrilsTheme.colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .background(TendrilsTheme.colors.surfaceElevated, PillShape)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${selectedExperience.title} Mode",
                                style = TendrilsTheme.typography.caption,
                                color = TendrilsTheme.colors.textSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = { activeDialog = "edit_profile" }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit name",
                            tint = TendrilsTheme.colors.textMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // --- APPEARANCE & THEME PICKER ---
            Text(
                text = "Appearance",
                style = TendrilsTheme.typography.h3,
                color = TendrilsTheme.colors.textPrimary
            )

            TendrilsCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = TendrilsTheme.colors.surface,
                padding = 14.dp
            ) {
                Column {
                    Text(
                        text = "Theme Selection",
                        style = TendrilsTheme.typography.subtitle1,
                        fontWeight = FontWeight.SemiBold,
                        color = TendrilsTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeOptionChip(
                            label = "System",
                            isSelected = themeModeState.value == ThemeMode.SYSTEM,
                            onClick = { themeModeState.value = ThemeMode.SYSTEM },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeOptionChip(
                            label = "Light",
                            isSelected = themeModeState.value == ThemeMode.LIGHT,
                            onClick = { themeModeState.value = ThemeMode.LIGHT },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeOptionChip(
                            label = "Dark",
                            isSelected = themeModeState.value == ThemeMode.DARK,
                            onClick = { themeModeState.value = ThemeMode.DARK },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // --- PREFERENCES GROUP ---
            Text(
                text = "Companion Preferences",
                style = TendrilsTheme.typography.h3,
                color = TendrilsTheme.colors.textPrimary
            )

            TendrilsCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = TendrilsTheme.colors.surface,
                padding = 6.dp
            ) {
                Column {
                    SettingsRow(
                        title = "Language and Voice",
                        subtitle = "English • Gentle Sprout voice • 1.0x",
                        icon = Icons.Default.Info,
                        onClick = { activeDialog = "language_voice" }
                    )
                    Divider(color = TendrilsTheme.colors.border, thickness = 1.dp)
                    SettingsRow(
                        title = "Accessibility",
                        subtitle = "Text scale, reduced motion, contrast",
                        icon = Icons.Default.Favorite,
                        onClick = { activeDialog = "accessibility" }
                    )
                }
            }

            val isBriefAccount = selectedExperience == ExperienceMode.CHILD || selectedExperience == ExperienceMode.TEEN

            // For Child and Student accounts, display a reassuring guidance banner
            if (isBriefAccount) {
                TendrilsCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = TendrilsTheme.colors.surfaceElevated,
                    borderColor = TendrilsTheme.colors.border,
                    padding = 12.dp
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = TendrilsTheme.colors.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "${selectedExperience.title} Account Active",
                                style = TendrilsTheme.typography.subtitle1,
                                fontWeight = FontWeight.SemiBold,
                                color = TendrilsTheme.colors.textPrimary
                            )
                            Text(
                                text = "Settings are streamlined to essential preferences. Technical lab tools and raw model downloads are locked.",
                                style = TendrilsTheme.typography.caption,
                                color = TendrilsTheme.colors.textSecondary
                            )
                        }
                    }
                }
            }

            // --- PRIVACY & PERMISSIONS GROUP ---
            Text(
                text = if (isBriefAccount) "Safety & Device Privacy" else "Privacy & Safety",
                style = TendrilsTheme.typography.h3,
                color = TendrilsTheme.colors.textPrimary
            )

            TendrilsCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = TendrilsTheme.colors.surface,
                padding = 6.dp
            ) {
                Column {
                    if (!isBriefAccount) {
                        SettingsRow(
                            title = "Security & Protection",
                            subtitle = "Background media scanner • C2PA • Phishing guard",
                            icon = Icons.Default.Check,
                            onClick = { activeDialog = "security" }
                        )
                        Divider(color = TendrilsTheme.colors.border, thickness = 1.dp)
                    }

                    SettingsRow(
                        title = "Privacy & Local Data",
                        subtitle = "Strictly on-device storage • Local memory",
                        icon = Icons.Default.Check,
                        onClick = { activeDialog = "privacy" }
                    )

                    if (!isBriefAccount) {
                        Divider(color = TendrilsTheme.colors.border, thickness = 1.dp)
                        SettingsRow(
                            title = "System Permissions",
                            subtitle = "Camera, Mic, Files (On-demand only)",
                            icon = Icons.Default.Star,
                            onClick = { activeDialog = "permissions" }
                        )
                    }

                    Divider(color = TendrilsTheme.colors.border, thickness = 1.dp)
                    SettingsRow(
                        title = "My Friends & Family Circle",
                        subtitle = "Private code-linked companions • No public directory",
                        icon = Icons.Default.Person,
                        onClick = { activeDialog = "friends_family" }
                    )

                    Divider(color = TendrilsTheme.colors.border, thickness = 1.dp)
                    SettingsRow(
                        title = if (selectedExperience == ExperienceMode.CHILD) "Parental & Family Guidance" else "Family & Safety Experience",
                        subtitle = "Transparent boundaries and safety guidelines without tracking",
                        icon = Icons.Default.Check,
                        onClick = { activeDialog = "family" }
                    )
                }
            }

            // --- TESTING & DEMONSTRATION TOOLS (Adult / Developer Personal Mode Only) ---
            if (!isBriefAccount) {
                Text(
                    text = "Testing & Flows (Developer Lab)",
                    style = TendrilsTheme.typography.h3,
                    color = TendrilsTheme.colors.textPrimary
                )

                TendrilsCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = TendrilsTheme.colors.surface,
                    padding = 6.dp
                ) {
                    Column {
                        SettingsRow(
                            title = "Model Manager (Lab)",
                            subtitle = "Downloads/Tendrils/models • TFLite & GGUF switch",
                            icon = Icons.Default.Star,
                            onClick = { activeDialog = "model_manager" }
                        )
                        Divider(color = TendrilsTheme.colors.border, thickness = 1.dp)
                        SettingsRow(
                            title = "Flow D: Deepfake & Image Scan",
                            subtitle = "TFLite Artifact & Provenance inspection modal",
                            icon = Icons.Default.Search,
                            onClick = { activeDialog = "flow_d" }
                        )
                        Divider(color = TendrilsTheme.colors.border, thickness = 1.dp)
                        SettingsRow(
                            title = "Flow E: OCR Document Reader",
                            subtitle = "Local text extraction & study card generator",
                            icon = Icons.Default.Info,
                            onClick = { activeDialog = "flow_e" }
                        )
                        Divider(color = TendrilsTheme.colors.border, thickness = 1.dp)
                        SettingsRow(
                            title = "Flow F: Phishing Link Shield",
                            subtitle = "Domain reputation & phishing heuristics",
                            icon = Icons.Default.Check,
                            onClick = { activeDialog = "flow_f" }
                        )
                        Divider(color = TendrilsTheme.colors.border, thickness = 1.dp)
                        SettingsRow(
                            title = "Flow G: Device Automation Task",
                            subtitle = "Permission-controlled timer alert automation",
                            icon = Icons.Default.Refresh,
                            onClick = { activeDialog = "flow_g" }
                        )
                        Divider(color = TendrilsTheme.colors.border, thickness = 1.dp)
                        SettingsRow(
                            title = "Flow H: Family Device Pairing",
                            subtitle = "Pairing code & transparent privacy controls",
                            icon = Icons.Default.Person,
                            onClick = { activeDialog = "flow_h" }
                        )
                        Divider(color = TendrilsTheme.colors.border, thickness = 1.dp)
                        SettingsRow(
                            title = "Replay Onboarding Flow",
                            subtitle = "Re-experience the 5-step welcome & setup",
                            icon = Icons.Default.Refresh,
                            onClick = { onboardingController.value = false }
                        )
                    }
                }
            }

            // --- ABOUT ---
            TendrilsCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = TendrilsTheme.colors.surfaceElevated,
                borderColor = TendrilsTheme.colors.border,
                padding = 16.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Tendrils Personal AI Companion",
                        style = TendrilsTheme.typography.subtitle1,
                        fontWeight = FontWeight.SemiBold,
                        color = TendrilsTheme.colors.textPrimary
                    )
                    Text(
                        text = "Milestone 5: Flows, Security & Model Manager Lab",
                        style = TendrilsTheme.typography.caption,
                        fontWeight = FontWeight.Bold,
                        color = TendrilsTheme.colors.primary
                    )
                    Text(
                        text = "Built with Kotlin Multiplatform and Compose. Files and memory are stored strictly on-device.",
                        style = TendrilsTheme.typography.caption,
                        color = TendrilsTheme.colors.textMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // --- SUB-SCREEN DIALOGS ---
    when (activeDialog) {
        "edit_profile" -> EditProfileDialog(
            currentName = displayName,
            currentExperience = selectedExperience,
            onSaveName = { displayName = it },
            onSaveExperience = { selectedExperience = it },
            onDismiss = { activeDialog = null }
        )
        "language_voice" -> LanguageVoiceSettingsDialog(
            onDismiss = { activeDialog = null }
        )
        "security" -> SecuritySettingsDialog(
            onDismiss = { activeDialog = null }
        )
        "model_manager" -> ModelManagerDialog(
            onDismiss = { activeDialog = null }
        )
        "flow_d" -> FlowDImageUnderstandingDialog(
            onDismiss = { activeDialog = null }
        )
        "flow_e" -> FlowEOcrReaderDialog(
            onDismiss = { activeDialog = null }
        )
        "flow_f" -> FlowFLinkSafetyDialog(
            onDismiss = { activeDialog = null }
        )
        "flow_g" -> FlowGDeviceTaskDialog(
            onDismiss = { activeDialog = null }
        )
        "flow_h" -> FlowHFamilyPairingDialog(
            onDismiss = { activeDialog = null }
        )
        "privacy" -> PrivacySettingsDialog(
            onDismiss = { activeDialog = null },
            onClearAllData = {
                activeDialog = null
                showClearDataConfirmation = true
            }
        )
        "friends_family" -> FriendsAndFamilyDialog(
            onDismiss = { activeDialog = null }
        )
        "permissions" -> PermissionsSettingsDialog(
            onDismiss = { activeDialog = null }
        )
        "family" -> FamilySettingsDialog(
            onDismiss = { activeDialog = null }
        )
        "accessibility" -> AccessibilitySettingsDialog(
            onDismiss = { activeDialog = null }
        )
    }

    // Confirmation dialog for data clear
    if (showClearDataConfirmation) {
        AlertDialog(
            onDismissRequest = { showClearDataConfirmation = false },
            title = { Text("Clear All Local Data?", style = TendrilsTheme.typography.h3, color = TendrilsTheme.colors.textPrimary) },
            text = {
                Text(
                    "This will delete all locally stored conversation history, learning progress, and personal preferences from this device.",
                    style = TendrilsTheme.typography.body2,
                    color = TendrilsTheme.colors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = { showClearDataConfirmation = false },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.error,
                        contentColor = Color.White
                    ),
                    shape = PillShape
                ) {
                    Text("Clear Everything")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataConfirmation = false }) {
                    Text("Cancel", color = TendrilsTheme.colors.textMuted)
                }
            },
            backgroundColor = TendrilsTheme.colors.surface,
            shape = TendrilsTheme.shapes.medium
        )
    }
}

@Composable
private fun ThemeOptionChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(PillShape)
            .background(if (isSelected) TendrilsTheme.colors.primary.copy(alpha = 0.15f) else TendrilsTheme.colors.surfaceElevated)
            .border(
                1.dp,
                if (isSelected) TendrilsTheme.colors.primary else TendrilsTheme.colors.border,
                PillShape
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = TendrilsTheme.typography.body2,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) TendrilsTheme.colors.primary else TendrilsTheme.colors.textSecondary
        )
    }
}

@Composable
private fun SettingsRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TendrilsTheme.colors.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = TendrilsTheme.typography.body1,
                fontWeight = FontWeight.SemiBold,
                color = TendrilsTheme.colors.textPrimary
            )
            Text(
                text = subtitle,
                style = TendrilsTheme.typography.caption,
                color = TendrilsTheme.colors.textMuted
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = TendrilsTheme.colors.textMuted,
            modifier = Modifier.size(16.dp)
        )
    }
}
