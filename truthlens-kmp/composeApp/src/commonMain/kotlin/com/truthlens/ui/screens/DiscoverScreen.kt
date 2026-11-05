package com.truthlens.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.truthlens.ui.components.TendrilsCard
import com.truthlens.ui.theme.PillShape
import com.truthlens.ui.theme.TendrilsPalette
import com.truthlens.ui.theme.TendrilsTheme

/**
 * DiscoverScreen — Explore Tendrils Features
 * Clear categories: Learn, Understand, Create, Digital Safety, Device Assistance.
 * Unimplemented features are clearly marked as Coming Soon without false claims.
 */
@Composable
fun DiscoverScreen() {
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
                text = "Discover",
                style = TendrilsTheme.typography.h1,
                color = TendrilsTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Explore what you and Tendrils can achieve together",
                style = TendrilsTheme.typography.body2,
                color = TendrilsTheme.colors.textSecondary
            )
        }

        // --- CATEGORIES LIST ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DiscoverCategorySection(
                title = "Learn",
                subtitle = "Deepen knowledge and master complex concepts",
                items = listOf(
                    DiscoverItem("Ask & Explain", "Break down difficult topics with tailored depth", Icons.Default.Info, isAvailable = true),
                    DiscoverItem("Study Assistance", "Interactive quizzes and revision routines", Icons.Default.Star, isAvailable = false),
                    DiscoverItem("Creative Learning", "Explore science, history, and languages", Icons.Default.Favorite, isAvailable = false)
                )
            )

            DiscoverCategorySection(
                title = "Understand",
                subtitle = "Multimodal comprehension of media and documents",
                items = listOf(
                    DiscoverItem("Explain a Photo", "Visual insights and scene interpretation", Icons.Default.Search, isAvailable = false),
                    DiscoverItem("Document Reader", "Summarise documents and extract text", Icons.Default.List, isAvailable = false)
                )
            )

            DiscoverCategorySection(
                title = "Create",
                subtitle = "Brainstorming and thoughtful writing assistance",
                items = listOf(
                    DiscoverItem("Writing Assistant", "Structure essays, stories, and ideas", Icons.Default.Edit, isAvailable = false),
                    DiscoverItem("Idea Generation", "Brainstorm concepts and problem-solving steps", Icons.Default.Star, isAvailable = false)
                )
            )

            DiscoverCategorySection(
                title = "Digital Safety",
                subtitle = "Recognise scams, manipulated media, and suspicious links",
                items = listOf(
                    DiscoverItem("Link & Message Checker", "Spot phishing signs and suspicious domains", Icons.Default.Check, isAvailable = false),
                    DiscoverItem("Deepfake Awareness", "Understand AI generation techniques and markers", Icons.Default.Info, isAvailable = false)
                )
            )

            DiscoverCategorySection(
                title = "Device Assistance",
                subtitle = "Safe, permission-controlled companion workflows",
                items = listOf(
                    DiscoverItem("Gentle Reminders", "Keep track of study sessions and goals", Icons.Default.Notifications, isAvailable = false),
                    DiscoverItem("Productivity Routine", "Guided focus and quiet reflection intervals", Icons.Default.DateRange, isAvailable = false)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private data class DiscoverItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val isAvailable: Boolean
)

@Composable
private fun DiscoverCategorySection(
    title: String,
    subtitle: String,
    items: List<DiscoverItem>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = TendrilsTheme.typography.h3,
            color = TendrilsTheme.colors.textPrimary
        )
        Text(
            text = subtitle,
            style = TendrilsTheme.typography.caption,
            color = TendrilsTheme.colors.textMuted
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items.forEach { item ->
                TendrilsCard(
                    modifier = Modifier.fillMaxWidth(),
                    padding = 14.dp,
                    backgroundColor = TendrilsTheme.colors.surface,
                    borderColor = TendrilsTheme.colors.border
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(TendrilsTheme.colors.surfaceElevated, PillShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = if (item.isAvailable) TendrilsTheme.colors.primary else TendrilsTheme.colors.textMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                style = TendrilsTheme.typography.body1,
                                fontWeight = FontWeight.SemiBold,
                                color = TendrilsTheme.colors.textPrimary
                            )
                            Text(
                                text = item.description,
                                style = TendrilsTheme.typography.body2,
                                color = TendrilsTheme.colors.textSecondary
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        if (item.isAvailable) {
                            Box(
                                modifier = Modifier
                                    .background(TendrilsPalette.Sprout500.copy(alpha = 0.15f), PillShape)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Ready",
                                    color = TendrilsTheme.colors.primary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .background(TendrilsTheme.colors.surfaceElevated, PillShape)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Milestone 3",
                                    color = TendrilsTheme.colors.textMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
