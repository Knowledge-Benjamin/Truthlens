package com.truthlens.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.truthlens.ui.components.TendrilsCard
import com.truthlens.ui.theme.PillShape
import com.truthlens.ui.theme.TendrilsPalette
import com.truthlens.ui.theme.TendrilsTheme

/**
 * HomeScreen — Personal AI Companion Central Hub
 * Designed with organic, calm visual elements, distinct companion state,
 * contextual suggestions, and comfortable floating input area.
 */
@Composable
fun HomeScreen() {
    var inputText by remember { mutableStateOf("") }
    val suggestions = listOf(
        "Explain something",
        "Help me learn",
        "Check a link",
        "Read a document",
        "Ask anything"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TendrilsTheme.colors.background)
            .statusBarsPadding()
    ) {
        // --- TOP SECTION: GREETING & PROFILE ACCESS ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Good morning,",
                    style = TendrilsTheme.typography.caption,
                    color = TendrilsTheme.colors.textMuted
                )
                Text(
                    text = "Explorer",
                    style = TendrilsTheme.typography.h2,
                    color = TendrilsTheme.colors.textPrimary
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Activity / Notification Indicator
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(TendrilsTheme.colors.surface)
                        .border(1.dp, TendrilsTheme.colors.border, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = TendrilsTheme.colors.textSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(TendrilsTheme.colors.primary.copy(alpha = 0.2f))
                        .border(1.dp, TendrilsTheme.colors.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "E",
                        fontWeight = FontWeight.Bold,
                        color = TendrilsTheme.colors.primary,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // --- MAIN SCROLLABLE CONTENT ---
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // --- CENTRE: COMPANION VISUAL & STATE INDICATOR ---
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        if (TendrilsTheme.colors.isDark) {
                            TendrilsPalette.Sprout500.copy(alpha = 0.12f)
                        } else {
                            TendrilsPalette.Sprout500.copy(alpha = 0.08f)
                        }
                    )
                    .border(2.dp, TendrilsTheme.colors.primary.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Organic botanical icon representing Tendrils
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Tendrils Companion",
                    tint = TendrilsTheme.colors.primary,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // State Badge (Idle, ready)
            Box(
                modifier = Modifier
                    .clip(PillShape)
                    .background(TendrilsTheme.colors.surface)
                    .border(1.dp, TendrilsTheme.colors.border, PillShape)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(TendrilsPalette.Emerald400)
                    )
                    Text(
                        text = "Tendrils Companion • Idle",
                        style = TendrilsTheme.typography.caption,
                        color = TendrilsTheme.colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "What would you like to explore together?",
                style = TendrilsTheme.typography.h3,
                color = TendrilsTheme.colors.textPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- SUGGESTIONS CAROUSEL ---
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(suggestions) { suggestion ->
                    TendrilsCard(
                        shape = PillShape,
                        padding = 10.dp,
                        backgroundColor = TendrilsTheme.colors.surfaceElevated,
                        borderColor = TendrilsTheme.colors.border,
                        onClick = { inputText = suggestion }
                    ) {
                        Text(
                            text = suggestion,
                            style = TendrilsTheme.typography.body2,
                            color = TendrilsTheme.colors.textPrimary,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- WELCOME CARD ---
            TendrilsCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = TendrilsTheme.colors.surface,
                padding = 18.dp
            ) {
                Column {
                    Text(
                        text = "About your companion",
                        style = TendrilsTheme.typography.subtitle1,
                        fontWeight = FontWeight.SemiBold,
                        color = TendrilsTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tendrils is your safe, friendly space for curious minds. Ask questions, study new subjects, check suspicious links, and explore the digital world with confidence.",
                        style = TendrilsTheme.typography.body2,
                        color = TendrilsTheme.colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- FLOATING INPUT AREA ---
        TendrilsCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = PillShape,
            backgroundColor = TendrilsTheme.colors.surface,
            borderColor = TendrilsTheme.colors.border,
            padding = 6.dp
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Attachment Button
                IconButton(
                    onClick = { /* Flow D / E */ },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Attach",
                        tint = TendrilsTheme.colors.textMuted
                    )
                }

                // Text Input
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = "Ask Tendrils anything...",
                            style = TendrilsTheme.typography.body1,
                            color = TendrilsTheme.colors.textMuted
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        textColor = TendrilsTheme.colors.textPrimary,
                        cursorColor = TendrilsTheme.colors.primary
                    ),
                    singleLine = true
                )

                // Voice / Mic Button
                IconButton(
                    onClick = { /* Flow B voice demo */ },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share, // voice representation placeholder
                        contentDescription = "Voice mode",
                        tint = TendrilsTheme.colors.textMuted
                    )
                }

                // Send Button
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            if (inputText.isNotBlank()) TendrilsTheme.colors.primary else TendrilsTheme.colors.surfaceElevated
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                inputText = ""
                            }
                        },
                        enabled = inputText.isNotBlank()
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Send",
                            tint = if (inputText.isNotBlank()) Color.White else TendrilsTheme.colors.textMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}