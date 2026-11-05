package com.truthlens.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.truthlens.ui.components.TendrilsCard
import com.truthlens.ui.theme.PillShape
import com.truthlens.ui.theme.TendrilsPalette
import com.truthlens.ui.theme.TendrilsTheme

/**
 * ActivityScreen — Recent interactions, learning log, and saved items.
 * Clean, organized sections with empty state handling and local data controls.
 */
@Composable
fun ActivityScreen() {
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Chats", "Tasks", "Saved")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TendrilsTheme.colors.background)
            .statusBarsPadding()
    ) {
        // --- HEADER ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Activity",
                    style = TendrilsTheme.typography.h1,
                    color = TendrilsTheme.colors.textPrimary
                )
                Text(
                    text = "Your exploration history & saved insights",
                    style = TendrilsTheme.typography.body2,
                    color = TendrilsTheme.colors.textSecondary
                )
            }

            IconButton(
                onClick = { /* Clear local activity dialog */ }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Clear activity",
                    tint = TendrilsTheme.colors.textMuted
                )
            }
        }

        // --- FILTER PILLS ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter ->
                val isSelected = selectedFilter == filter
                TendrilsCard(
                    shape = PillShape,
                    padding = 8.dp,
                    backgroundColor = if (isSelected) TendrilsTheme.colors.primary.copy(alpha = 0.15f) else TendrilsTheme.colors.surface,
                    borderColor = if (isSelected) TendrilsTheme.colors.primary else TendrilsTheme.colors.border,
                    onClick = { selectedFilter = filter }
                ) {
                    Text(
                        text = filter,
                        style = TendrilsTheme.typography.body2,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) TendrilsTheme.colors.primary else TendrilsTheme.colors.textSecondary,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- CONTENT / EMPTY STATE ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Recent Demonstration Items
            Text(
                text = "Recent Explorations",
                style = TendrilsTheme.typography.h3,
                color = TendrilsTheme.colors.textPrimary
            )

            TendrilsCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = TendrilsTheme.colors.surface,
                padding = 16.dp
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Introduction to Photosynthesis",
                            style = TendrilsTheme.typography.subtitle1,
                            fontWeight = FontWeight.SemiBold,
                            color = TendrilsTheme.colors.textPrimary
                        )
                        Text(
                            text = "Today",
                            style = TendrilsTheme.typography.caption,
                            color = TendrilsTheme.colors.textMuted
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Explained how plants convert sunlight and carbon dioxide into clean energy.",
                        style = TendrilsTheme.typography.body2,
                        color = TendrilsTheme.colors.textSecondary
                    )
                }
            }

            TendrilsCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = TendrilsTheme.colors.surface,
                padding = 16.dp
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Link Safety Check: news-update.cc",
                            style = TendrilsTheme.typography.subtitle1,
                            fontWeight = FontWeight.SemiBold,
                            color = TendrilsTheme.colors.textPrimary
                        )
                        Text(
                            text = "Yesterday",
                            style = TendrilsTheme.typography.caption,
                            color = TendrilsTheme.colors.textMuted
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Analyzed domain attributes and identified mismatch in publisher metadata.",
                        style = TendrilsTheme.typography.body2,
                        color = TendrilsTheme.colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Information card on privacy
            TendrilsCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = TendrilsTheme.colors.surfaceElevated,
                borderColor = TendrilsTheme.colors.border,
                padding = 14.dp
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Privacy note",
                        tint = TendrilsTheme.colors.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Activity is preserved strictly locally on your device and can be erased anytime.",
                        style = TendrilsTheme.typography.caption,
                        color = TendrilsTheme.colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
