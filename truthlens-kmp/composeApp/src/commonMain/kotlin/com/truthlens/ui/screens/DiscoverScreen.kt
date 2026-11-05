package com.truthlens.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import com.truthlens.ui.components.TendrilsCard
import com.truthlens.ui.navigation.HomeTab
import com.truthlens.ui.screens.discover.DiscoverFeature
import com.truthlens.ui.screens.discover.FeatureDetailDialog
import com.truthlens.ui.theme.PillShape
import com.truthlens.ui.theme.TendrilsPalette
import com.truthlens.ui.theme.TendrilsTheme

/**
 * DiscoverScreen — Explore Tendrils Capabilities
 * Milestone 3:
 * - 5 Comprehensive categories: Learn, Understand, Create, Digital Safety, Device Assistance
 * - Category filter chips & quick search
 * - Interactive feature dialogs for active features
 * - Honest, non-deceptive Coming Soon states for future runtime integrations
 */
@Composable
fun DiscoverScreen() {
    val tabNavigator = LocalTabNavigator.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var activeFeatureDetail by remember { mutableStateOf<DiscoverFeature?>(null) }

    val categories = listOf("All", "Learn", "Understand", "Create", "Safety", "Device")

    val allFeatures = remember {
        listOf(
            // LEARN
            DiscoverFeature(
                id = "learn_explain",
                category = "Learn",
                name = "Ask & Explain",
                description = "Break down difficult topics with selectable depth (Simple, Intermediate, or Detailed).",
                icon = Icons.Default.Info,
                isInteractive = true,
                statusLabel = "Interactive"
            ),
            DiscoverFeature(
                id = "study_assist",
                category = "Learn",
                name = "Study Assistance",
                description = "Interactive recall flashcards, study schedules, and concept breakdowns.",
                icon = Icons.Default.Star,
                isInteractive = true,
                statusLabel = "Interactive"
            ),
            DiscoverFeature(
                id = "learn_lang",
                category = "Learn",
                name = "Language Learning",
                description = "Conversational dialogue practice with bilingual translations and grammar tips.",
                icon = Icons.Default.Favorite,
                isInteractive = false,
                statusLabel = "Milestone 5"
            ),
            DiscoverFeature(
                id = "learn_creative",
                category = "Learn",
                name = "Creative Learning",
                description = "Exploratory thought experiments, science questions, and historical stories.",
                icon = Icons.Default.Star,
                isInteractive = false,
                statusLabel = "Milestone 5"
            ),

            // UNDERSTAND
            DiscoverFeature(
                id = "understand_photo",
                category = "Understand",
                name = "Explain a Photograph",
                description = "Visual understanding and scene interpretation from camera or gallery images.",
                icon = Icons.Default.Search,
                isInteractive = false,
                statusLabel = "Milestone 5"
            ),
            DiscoverFeature(
                id = "understand_ocr",
                category = "Understand",
                name = "Document & Text Reader",
                description = "Read text from documents, PDFs, or photos and produce clean summaries.",
                icon = Icons.Default.List,
                isInteractive = true,
                statusLabel = "Interactive"
            ),
            DiscoverFeature(
                id = "understand_msg",
                category = "Understand",
                name = "Explain Selected Message",
                description = "Clarify ambiguous wording, subtext, or confusing terminology in messages.",
                icon = Icons.Default.Email,
                isInteractive = false,
                statusLabel = "Milestone 5"
            ),

            // CREATE
            DiscoverFeature(
                id = "create_write",
                category = "Create",
                name = "Writing Assistant",
                description = "Outlines, essay structure, poetry, and constructive writing feedback.",
                icon = Icons.Default.Edit,
                isInteractive = true,
                statusLabel = "Interactive"
            ),
            DiscoverFeature(
                id = "create_brainstorm",
                category = "Create",
                name = "Idea Generation",
                description = "Brainstorm inventive projects, science fair ideas, and story plots.",
                icon = Icons.Default.Star,
                isInteractive = true,
                statusLabel = "Interactive"
            ),

            // DIGITAL SAFETY
            DiscoverFeature(
                id = "safety_link",
                category = "Safety",
                name = "Suspicious Link Checker",
                description = "Inspect domain age, typosquatting, and phishing markers before clicking.",
                icon = Icons.Default.Check,
                isInteractive = true,
                statusLabel = "Interactive"
            ),
            DiscoverFeature(
                id = "safety_scam",
                category = "Safety",
                name = "Explain Possible Scams",
                description = "Identify pressure tactics, fake urgent requests, and social engineering.",
                icon = Icons.Default.Warning,
                isInteractive = false,
                statusLabel = "Milestone 5"
            ),
            DiscoverFeature(
                id = "safety_privacy",
                category = "Safety",
                name = "Online Privacy Guide",
                description = "Learn how tracking works, how cookies behave, and how to stay safe.",
                icon = Icons.Default.Lock,
                isInteractive = false,
                statusLabel = "Milestone 5"
            ),

            // DEVICE ASSISTANCE
            DiscoverFeature(
                id = "device_nav",
                category = "Device",
                name = "App Navigation",
                description = "Guided walkthroughs through installed apps with explicit user consent.",
                icon = Icons.Default.Place,
                isInteractive = false,
                statusLabel = "Milestone 5 (On-demand perm)"
            ),
            DiscoverFeature(
                id = "device_remind",
                category = "Device",
                name = "Gentle Reminders",
                description = "Device-local alerts for study sessions, hydration, and organic breaks.",
                icon = Icons.Default.Notifications,
                isInteractive = false,
                statusLabel = "Milestone 5"
            )
        )
    }

    // Filter features by search query and category
    val filteredFeatures = remember(searchQuery, selectedCategory) {
        allFeatures.filter { feat ->
            val matchesCategory = selectedCategory == "All" || feat.category.equals(selectedCategory, ignoreCase = true)
            val matchesQuery = searchQuery.isBlank() ||
                    feat.name.contains(searchQuery, ignoreCase = true) ||
                    feat.description.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

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
                text = "Discover",
                style = TendrilsTheme.typography.h1,
                color = TendrilsTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Explore capabilities and guided companions",
                style = TendrilsTheme.typography.body2,
                color = TendrilsTheme.colors.textSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar
            TendrilsCard(
                modifier = Modifier.fillMaxWidth(),
                shape = PillShape,
                backgroundColor = TendrilsTheme.colors.surface,
                borderColor = TendrilsTheme.colors.border,
                padding = 4.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TendrilsTheme.colors.textMuted,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(20.dp)
                    )
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = "Search features (e.g., links, explain, study)...",
                                style = TendrilsTheme.typography.body2,
                                color = TendrilsTheme.colors.textMuted
                            )
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            textColor = TendrilsTheme.colors.textPrimary,
                            cursorColor = TendrilsTheme.colors.primary
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = TendrilsTheme.colors.textMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category Filter Pills
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    val isSel = selectedCategory == cat
                    TendrilsCard(
                        shape = PillShape,
                        padding = 8.dp,
                        backgroundColor = if (isSel) TendrilsTheme.colors.primary.copy(alpha = 0.16f) else TendrilsTheme.colors.surface,
                        borderColor = if (isSel) TendrilsTheme.colors.primary else TendrilsTheme.colors.border,
                        onClick = { selectedCategory = cat }
                    ) {
                        Text(
                            text = cat,
                            style = TendrilsTheme.typography.caption,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) TendrilsTheme.colors.primary else TendrilsTheme.colors.textSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )
                    }
                }
            }
        }

        // --- FEATURES LIST ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (filteredFeatures.isEmpty()) {
                TendrilsCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = TendrilsTheme.colors.surfaceElevated,
                    padding = 24.dp
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No matching features found",
                            style = TendrilsTheme.typography.body1,
                            fontWeight = FontWeight.SemiBold,
                            color = TendrilsTheme.colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try searching for another keyword or select \"All\".",
                            style = TendrilsTheme.typography.caption,
                            color = TendrilsTheme.colors.textMuted
                        )
                    }
                }
            } else {
                filteredFeatures.forEach { feature ->
                    FeatureCardItem(
                        feature = feature,
                        onClick = { activeFeatureDetail = feature }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Modal Sheet for active feature
    activeFeatureDetail?.let { feature ->
        FeatureDetailDialog(
            feature = feature,
            onDismiss = { activeFeatureDetail = null },
            onSendToChat = { query ->
                tabNavigator.current = HomeTab
            }
        )
    }
}

@Composable
private fun FeatureCardItem(
    feature: DiscoverFeature,
    onClick: () -> Unit
) {
    TendrilsCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = TendrilsTheme.colors.surface,
        borderColor = TendrilsTheme.colors.border,
        padding = 14.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (feature.isInteractive) TendrilsTheme.colors.primary.copy(alpha = 0.15f) else TendrilsTheme.colors.surfaceElevated,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = feature.icon,
                    contentDescription = feature.name,
                    tint = if (feature.isInteractive) TendrilsTheme.colors.primary else TendrilsTheme.colors.textMuted,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = feature.name,
                        style = TendrilsTheme.typography.body1,
                        fontWeight = FontWeight.SemiBold,
                        color = TendrilsTheme.colors.textPrimary
                    )
                    // Status Badge
                    Box(
                        modifier = Modifier
                            .background(
                                if (feature.isInteractive) TendrilsPalette.Sprout500.copy(alpha = 0.15f) else TendrilsTheme.colors.surfaceElevated,
                                PillShape
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = feature.statusLabel,
                            style = TendrilsTheme.typography.caption,
                            fontWeight = if (feature.isInteractive) FontWeight.Bold else FontWeight.Normal,
                            color = if (feature.isInteractive) TendrilsTheme.colors.primary else TendrilsTheme.colors.textMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = feature.description,
                    style = TendrilsTheme.typography.body2,
                    color = TendrilsTheme.colors.textSecondary
                )
            }
        }
    }
}
