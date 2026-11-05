package com.truthlens.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.truthlens.ui.components.AvatarManager
import com.truthlens.ui.components.TendrilOrganism
import com.truthlens.ui.components.TendrilsCard
import com.truthlens.ui.model.ChatMessage
import com.truthlens.ui.model.CompanionState
import com.truthlens.ui.theme.PillShape
import com.truthlens.ui.theme.TendrilsPalette
import com.truthlens.ui.theme.TendrilsTheme

/**
 * Rich Chat Message Bubble supporting:
 * - Clean Bold & Inline formatting (strips all raw *, **, ***** artifacts)
 * - Headings
 * - Bullet & Numbered Lists
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
            Row(
                modifier = Modifier.fillMaxWidth(0.94f),
                verticalAlignment = Alignment.Top
            ) {
                // Selected Companion Avatar Badge
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(TendrilsTheme.colors.primary.copy(alpha = 0.14f))
                        .border(1.dp, TendrilsTheme.colors.primary.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    TendrilOrganism(
                        state = CompanionState.IDLE,
                        size = 28.dp,
                        archetype = AvatarManager.selectedArchetype,
                        interactiveOnTap = false
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
                                        text = "→ ${cleanAsterisks(followUp)}",
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
 * Strips raw ***** / *** / ** markdown artifacts and converts **bold** segments into styled AnnotatedString spans.
 */
fun parseCleanAnnotatedLine(rawLine: String): AnnotatedString {
    // First remove runs of 3 or more asterisks (e.g., ***, ****, *****) completely
    val preCleaned = rawLine
        .replace(Regex("\\*{3,}"), "")
        .replace(Regex("^\\*+\\s*"), "")
        .replace(Regex("\\s*\\*+$"), "")

    return buildAnnotatedString {
        val parts = preCleaned.split("**")
        parts.forEachIndexed { index, part ->
            val segment = part.replace("*", "").replace("`", "")
            if (index % 2 == 1) {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(segment)
                }
            } else {
                append(segment)
            }
        }
    }
}

private fun cleanAsterisks(raw: String): String {
    return raw.replace(Regex("\\*+"), "").replace("`", "").trim()
}

@Composable
private fun FormattedResponseText(content: String) {
    val lines = content.lines()

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        for (rawLine in lines) {
            val line = rawLine.trim()
            if (line.matches(Regex("^[\\*\\-_]{3,}$"))) {
                // Skip horizontal markdown divider lines like ***** or ---
                continue
            }
            when {
                line.startsWith("# ") -> {
                    Text(
                        text = cleanAsterisks(line.removePrefix("# ")),
                        style = TendrilsTheme.typography.h3,
                        color = TendrilsTheme.colors.textPrimary,
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )
                }
                line.startsWith("## ") -> {
                    Text(
                        text = cleanAsterisks(line.removePrefix("## ")),
                        style = TendrilsTheme.typography.subtitle1,
                        fontWeight = FontWeight.Bold,
                        color = TendrilsTheme.colors.textPrimary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                line.startsWith("### ") -> {
                    Text(
                        text = cleanAsterisks(line.removePrefix("### ")),
                        style = TendrilsTheme.typography.body1,
                        fontWeight = FontWeight.Bold,
                        color = TendrilsTheme.colors.primary,
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
                            text = parseCleanAnnotatedLine(line.substring(2)),
                            style = TendrilsTheme.typography.body1,
                            color = TendrilsTheme.colors.textPrimary
                        )
                    }
                }
                line.isNotBlank() -> {
                    Text(
                        text = parseCleanAnnotatedLine(line),
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
            TendrilOrganism(
                state = CompanionState.THINKING,
                size = 26.dp,
                archetype = AvatarManager.selectedArchetype,
                interactiveOnTap = false
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
