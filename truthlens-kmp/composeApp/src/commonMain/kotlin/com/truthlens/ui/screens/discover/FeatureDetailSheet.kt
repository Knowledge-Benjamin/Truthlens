package com.truthlens.ui.screens.discover

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
import com.truthlens.ui.theme.PillShape
import com.truthlens.ui.theme.TendrilsPalette
import com.truthlens.ui.theme.TendrilsTheme

/**
 * Feature data representation for Discover hub
 */
data class DiscoverFeature(
    val id: String,
    val category: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val isInteractive: Boolean,
    val statusLabel: String
)

/**
 * Interactive Feature Detail Modal
 * Shows interactive flows for ready features (Explanation Depth, Link Safety, OCR Reader),
 * or honest Coming Soon / Permission-explained states for future device features.
 */
@Composable
fun FeatureDetailDialog(
    feature: DiscoverFeature,
    onDismiss: () -> Unit,
    onSendToChat: ((String) -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Close",
                        color = TendrilsTheme.colors.textMuted,
                        style = TendrilsTheme.typography.button
                    )
                }
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(TendrilsTheme.colors.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = feature.icon,
                        contentDescription = null,
                        tint = TendrilsTheme.colors.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = feature.name,
                        style = TendrilsTheme.typography.h3,
                        color = TendrilsTheme.colors.textPrimary
                    )
                    Text(
                        text = feature.category,
                        style = TendrilsTheme.typography.caption,
                        color = TendrilsTheme.colors.textMuted
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = feature.description,
                    style = TendrilsTheme.typography.body2,
                    color = TendrilsTheme.colors.textSecondary
                )

                Divider(color = TendrilsTheme.colors.border, thickness = 1.dp)

                if (feature.isInteractive) {
                    when (feature.id) {
                        "learn_explain" -> InteractiveExplainDemo(onSendToChat, onDismiss)
                        "safety_link" -> InteractiveLinkSafetyDemo(onSendToChat, onDismiss)
                        "understand_ocr" -> InteractiveOcrDemo(onSendToChat, onDismiss)
                        "study_assist" -> InteractiveStudyDemo(onSendToChat, onDismiss)
                        else -> GenericInteractivePreview(feature, onSendToChat, onDismiss)
                    }
                } else {
                    ComingSoonCapabilityNotice(feature)
                }
            }
        },
        backgroundColor = TendrilsTheme.colors.surface,
        shape = TendrilsTheme.shapes.large,
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}

// ==========================================
// INTERACTIVE DEMO: EXPLAIN WITH DEPTH LEVELS
// ==========================================
@Composable
private fun InteractiveExplainDemo(
    onSendToChat: ((String) -> Unit)?,
    onDismiss: () -> Unit
) {
    var selectedDepth by remember { mutableStateOf("Simple") }
    val depthLevels = listOf("Simple", "Intermediate", "Detailed")
    var question by remember { mutableStateOf("How do black holes form in space?") }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Select Explanation Depth",
            style = TendrilsTheme.typography.subtitle1,
            fontWeight = FontWeight.SemiBold,
            color = TendrilsTheme.colors.textPrimary
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            depthLevels.forEach { depth ->
                val isSel = selectedDepth == depth
                TendrilsCard(
                    shape = PillShape,
                    padding = 8.dp,
                    backgroundColor = if (isSel) TendrilsTheme.colors.primary.copy(alpha = 0.16f) else TendrilsTheme.colors.surfaceElevated,
                    borderColor = if (isSel) TendrilsTheme.colors.primary else TendrilsTheme.colors.border,
                    onClick = { selectedDepth = depth }
                ) {
                    Text(
                        text = depth,
                        style = TendrilsTheme.typography.caption,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSel) TendrilsTheme.colors.primary else TendrilsTheme.colors.textSecondary,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                }
            }
        }

        OutlinedTextField(
            value = question,
            onValueChange = { question = it },
            label = { Text("Topic or Question") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = TendrilsTheme.colors.textPrimary,
                focusedBorderColor = TendrilsTheme.colors.primary,
                unfocusedBorderColor = TendrilsTheme.colors.border,
                cursorColor = TendrilsTheme.colors.primary
            )
        )

        Button(
            onClick = {
                onSendToChat?.invoke("Please explain ($selectedDepth level): $question")
                onDismiss()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            shape = PillShape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = TendrilsTheme.colors.primary,
                contentColor = Color.White
            )
        ) {
            Text(text = "Ask Tendrils with $selectedDepth Depth")
        }
    }
}

// ==========================================
// INTERACTIVE DEMO: LINK SAFETY CHECKER
// ==========================================
@Composable
private fun InteractiveLinkSafetyDemo(
    onSendToChat: ((String) -> Unit)?,
    onDismiss: () -> Unit
) {
    var urlToTest by remember { mutableStateOf("https://secure-login.bank-update.xyz") }
    var hasAnalyzed by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Inspect Suspicious Link",
            style = TendrilsTheme.typography.subtitle1,
            fontWeight = FontWeight.SemiBold,
            color = TendrilsTheme.colors.textPrimary
        )

        OutlinedTextField(
            value = urlToTest,
            onValueChange = {
                urlToTest = it
                hasAnalyzed = false
            },
            label = { Text("URL to verify") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = TendrilsTheme.colors.textPrimary,
                focusedBorderColor = TendrilsTheme.colors.primary,
                unfocusedBorderColor = TendrilsTheme.colors.border
            )
        )

        Button(
            onClick = { hasAnalyzed = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            shape = PillShape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = TendrilsPalette.Amber500,
                contentColor = Color.White
            )
        ) {
            Text(text = "Analyse Link Attributes")
        }

        if (hasAnalyzed) {
            TendrilsCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = TendrilsPalette.Coral500.copy(alpha = 0.1f),
                borderColor = TendrilsPalette.Coral500.copy(alpha = 0.5f),
                padding = 12.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Potential Concern Detected (Mock Verification)",
                        style = TendrilsTheme.typography.caption,
                        fontWeight = FontWeight.Bold,
                        color = TendrilsPalette.Coral500
                    )
                    Text(
                        text = "• Domain registered recently (under 14 days)\n• Discrepancy between domain name and target institution\n• Recommendation: Do not submit login credentials",
                        style = TendrilsTheme.typography.caption,
                        color = TendrilsTheme.colors.textSecondary
                    )
                }
            }

            Button(
                onClick = {
                    onSendToChat?.invoke("I checked $urlToTest and saw suspicious domain indicators. Can you explain what typosquatting is?")
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = PillShape,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = TendrilsTheme.colors.surfaceElevated,
                    contentColor = TendrilsTheme.colors.primary
                )
            ) {
                Text(text = "Discuss in Chat Thread")
            }
        }
    }
}

// ==========================================
// INTERACTIVE DEMO: OCR & DOCUMENT READING
// ==========================================
@Composable
private fun InteractiveOcrDemo(
    onSendToChat: ((String) -> Unit)?,
    onDismiss: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Document Extraction & Summary Preview",
            style = TendrilsTheme.typography.subtitle1,
            fontWeight = FontWeight.SemiBold,
            color = TendrilsTheme.colors.textPrimary
        )

        TendrilsCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = TendrilsTheme.colors.surfaceElevated,
            padding = 12.dp
        ) {
            Column {
                Text(
                    text = "Sample Document: Renewable Energy Notes.pdf",
                    style = TendrilsTheme.typography.caption,
                    fontWeight = FontWeight.Bold,
                    color = TendrilsTheme.colors.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "\"Solar photovoltaics convert electromagnetic radiation directly into direct current via the photoelectric effect discovered in 1839...\"",
                    style = TendrilsTheme.typography.caption,
                    color = TendrilsTheme.colors.textSecondary
                )
            }
        }

        Button(
            onClick = {
                onSendToChat?.invoke("Please summarise the key concepts of solar photovoltaics and create 3 revision questions.")
                onDismiss()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            shape = PillShape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = TendrilsTheme.colors.primary,
                contentColor = Color.White
            )
        ) {
            Text(text = "Generate Summary & Flashcards")
        }
    }
}

// ==========================================
// INTERACTIVE DEMO: STUDY FLASHCARDS
// ==========================================
@Composable
private fun InteractiveStudyDemo(
    onSendToChat: ((String) -> Unit)?,
    onDismiss: () -> Unit
) {
    var isFlipped by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Quick Study Recall Card",
            style = TendrilsTheme.typography.subtitle1,
            fontWeight = FontWeight.SemiBold,
            color = TendrilsTheme.colors.textPrimary
        )

        TendrilsCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isFlipped = !isFlipped },
            backgroundColor = TendrilsTheme.colors.surfaceElevated,
            borderColor = TendrilsTheme.colors.primary,
            padding = 16.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (!isFlipped) "Question (Tap to flip)" else "Answer",
                    style = TendrilsTheme.typography.caption,
                    color = TendrilsTheme.colors.textMuted
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (!isFlipped) {
                        "What is Content Credentials (C2PA) in digital media?"
                    } else {
                        "An open technical standard that creates tamper-evident provenance metadata verifying who made an image and if AI was used."
                    },
                    style = TendrilsTheme.typography.body2,
                    fontWeight = FontWeight.SemiBold,
                    color = TendrilsTheme.colors.textPrimary
                )
            }
        }

        Button(
            onClick = {
                onSendToChat?.invoke("Create a 5-question digital safety quiz for me.")
                onDismiss()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            shape = PillShape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = TendrilsTheme.colors.primary,
                contentColor = Color.White
            )
        ) {
            Text(text = "Open Full Quiz in Chat")
        }
    }
}

@Composable
private fun GenericInteractivePreview(
    feature: DiscoverFeature,
    onSendToChat: ((String) -> Unit)?,
    onDismiss: () -> Unit
) {
    Button(
        onClick = {
            onSendToChat?.invoke("Let's explore ${feature.name}: ${feature.description}")
            onDismiss()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp),
        shape = PillShape,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = TendrilsTheme.colors.primary,
            contentColor = Color.White
        )
    ) {
        Text(text = "Launch in Conversation")
    }
}

// ==========================================
// COMING SOON CAPABILITY NOTICE
// ==========================================
@Composable
private fun ComingSoonCapabilityNotice(feature: DiscoverFeature) {
    TendrilsCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = TendrilsTheme.colors.surfaceElevated,
        padding = 14.dp
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = TendrilsPalette.Amber500,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Feature in Development (${feature.statusLabel})",
                    style = TendrilsTheme.typography.caption,
                    fontWeight = FontWeight.Bold,
                    color = TendrilsPalette.Amber500
                )
            }
            Text(
                text = "This capability requires integration with native device services or specialized models. Per the project brief, it is clearly marked as coming soon and does not execute simulated fake actions.",
                style = TendrilsTheme.typography.caption,
                color = TendrilsTheme.colors.textSecondary
            )
        }
    }
}
