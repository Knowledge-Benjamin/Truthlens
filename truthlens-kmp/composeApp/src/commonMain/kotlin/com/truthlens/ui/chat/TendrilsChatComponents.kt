package com.truthlens.ui.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.truthlens.ui.components.TendrilsCard
import com.truthlens.ui.model.ChatMessage
import com.truthlens.ui.theme.PillShape
import com.truthlens.ui.theme.TendrilsPalette
import com.truthlens.ui.theme.TendrilsShapes
import com.truthlens.ui.theme.TendrilsTheme

/**
 * Rich Chat Message Bubble supporting:
 * - Text
 * - Headings
 * - Lists / bullet points
 * - Links
 * - Follow-up suggestions
 */
@Composable
fun TendrilsMessageBubble(
    message: ChatMessage,
    onFollowUpClick: (String) -> Unit
) {
    val isUser = message.role == ChatMessage.Role.USER

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        if (isUser) {
            // User Message Bubble
            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = 18.dp,
                            bottomEnd = 4.dp
                        )
                    )
                    .background(TendrilsTheme.colors.primary)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = message.content,
                    style = TendrilsTheme.typography.body1,
                    color = Color.White
                )
            }
        } else {
            // Assistant Message Bubble with Avatar & Rich Formatted Layout
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.92f),
                verticalAlignment = Alignment.Top
            ) {
                // Tendrils Avatar Badge
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(TendrilsTheme.colors.primary.copy(alpha = 0.15f))
                        .border(1.dp, TendrilsTheme.colors.primary.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Tendrils",
                        tint = TendrilsTheme.colors.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    TendrilsCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 18.dp,
                            bottomStart = 18.dp,
                            bottomEnd = 18.dp
                        ),
                        backgroundColor = TendrilsTheme.colors.surface,
                        borderColor = TendrilsTheme.colors.border,
                        padding = 16.dp
                    ) {
                        FormattedResponseText(content = message.content)
                    }

                    // Follow-Up Suggestions
                    if (message.followUps.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            message.followUps.forEach { followUp ->
                                Box(
                                    modifier = Modifier
                                        .clip(PillShape)
                                        .background(TendrilsTheme.colors.surfaceElevated)
                                        .border(1.dp, TendrilsTheme.colors.border, PillShape)
                                        .clickable { onFollowUpClick(followUp) }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "→ $followUp",
                                        style = TendrilsTheme.typography.caption,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TendrilsTheme.colors.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Parses markdown-like headers (`#`), bullet lists (`- `, `•`), and plain text.
 */
@Composable
private fun FormattedResponseText(content: String) {
    val lines = content.lines()

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        for (line in lines) {
            when {
                line.startsWith("# ") -> {
                    Text(
                        text = line.removePrefix("# "),
                        style = TendrilsTheme.typography.h3,
                        color = TendrilsTheme.colors.textPrimary,
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )
                }
                line.startsWith("## ") -> {
                    Text(
                        text = line.removePrefix("## "),
                        style = TendrilsTheme.typography.subtitle1,
                        fontWeight = FontWeight.Bold,
                        color = TendrilsTheme.colors.textPrimary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                line.startsWith("- ") || line.startsWith("• ") || line.startsWith("* ") -> {
                    Row(
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "•",
                            style = TendrilsTheme.typography.body1,
                            color = TendrilsTheme.colors.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = line.substring(2),
                            style = TendrilsTheme.typography.body1,
                            color = TendrilsTheme.colors.textPrimary
                        )
                    }
                }
                line.isNotBlank() -> {
                    Text(
                        text = line,
                        style = TendrilsTheme.typography.body1,
                        color = TendrilsTheme.colors.textPrimary
                    )
                }
                else -> {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

/**
 * Thinking indicator displayed when Tendrils is processing a query.
 */
@Composable
fun ThinkingIndicator() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(TendrilsPalette.Violet400.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = TendrilsPalette.Violet400,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .clip(PillShape)
                .background(TendrilsTheme.colors.surfaceElevated)
                .border(1.dp, TendrilsTheme.colors.border, PillShape)
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Tendrils is thinking...",
                style = TendrilsTheme.typography.caption,
                color = TendrilsTheme.colors.textSecondary
            )
        }
    }
}
