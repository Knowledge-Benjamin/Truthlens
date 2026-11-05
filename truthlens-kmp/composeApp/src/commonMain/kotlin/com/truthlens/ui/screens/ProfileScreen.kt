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
import com.truthlens.ui.theme.LocalThemeMode
import com.truthlens.ui.theme.PillShape
import com.truthlens.ui.theme.TendrilsPalette
import com.truthlens.ui.theme.TendrilsTheme
import com.truthlens.ui.theme.ThemeMode

/**
 * ProfileScreen — User profile, interactive appearance switcher,
 * and organized settings destinations.
 */
@Composable
fun ProfileScreen() {
    val themeModeState = LocalThemeMode.current

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
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Profile",
                style = TendrilsTheme.typography.h1,
                color = TendrilsTheme.colors.textPrimary
            )
            Text(
                text = "Manage your preferences & companion experience",
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
            // User Card
            TendrilsCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = TendrilsTheme.colors.surface,
                padding = 18.dp
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(TendrilsTheme.colors.primary.copy(alpha = 0.2f))
                            .border(2.dp, TendrilsTheme.colors.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "E",
                            style = TendrilsTheme.typography.h2,
                            color = TendrilsTheme.colors.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Explorer",
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
                                text = "Personal Experience",
                                style = TendrilsTheme.typography.caption,
                                color = TendrilsTheme.colors.textSecondary
                            )
                        }
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
                        text = "Choose Theme",
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

            // --- SETTINGS GROUPS ---
            Text(
                text = "Preferences & Privacy",
                style = TendrilsTheme.typography.h3,
                color = TendrilsTheme.colors.textPrimary
            )

            TendrilsCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = TendrilsTheme.colors.surface,
                padding = 8.dp
            ) {
                Column {
                    SettingsRowItem("Language and Voice", "English • Gentle voice", Icons.Default.Info)
                    Divider(color = TendrilsTheme.colors.border, thickness = 1.dp)
                    SettingsRowItem("Privacy & Local Data", "Files kept strictly on-device", Icons.Default.Check)
                    Divider(color = TendrilsTheme.colors.border, thickness = 1.dp)
                    SettingsRowItem("Permissions", "Camera, Mic, Notifications", Icons.Default.Star)
                    Divider(color = TendrilsTheme.colors.border, thickness = 1.dp)
                    SettingsRowItem("Family Settings", "Optional pairing for kids & teens", Icons.Default.Person)
                }
            }

            // --- ABOUT ---
            TendrilsCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = TendrilsTheme.colors.surfaceElevated,
                borderColor = TendrilsTheme.colors.border,
                padding = 16.dp
            ) {
                Column {
                    Text(
                        text = "Tendrils AI Companion",
                        style = TendrilsTheme.typography.subtitle1,
                        fontWeight = FontWeight.SemiBold,
                        color = TendrilsTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Version 1.0.0-alpha • Milestone 1 (UI Foundation)",
                        style = TendrilsTheme.typography.caption,
                        color = TendrilsTheme.colors.textMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
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
private fun SettingsRowItem(
    title: String,
    subtitle: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Milestone 4 detailed navigation */ }
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TendrilsTheme.colors.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = TendrilsTheme.typography.body1,
                color = TendrilsTheme.colors.textPrimary
            )
            Text(
                text = subtitle,
                style = TendrilsTheme.typography.caption,
                color = TendrilsTheme.colors.textMuted
            )
        }
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            tint = TendrilsTheme.colors.textMuted,
            modifier = Modifier.size(16.dp)
        )
    }
}
