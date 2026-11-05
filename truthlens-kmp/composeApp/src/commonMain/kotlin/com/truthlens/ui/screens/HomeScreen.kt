package com.truthlens.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import com.truthlens.ui.chat.TendrilsMessageBubble
import com.truthlens.ui.chat.ThinkingIndicator
import com.truthlens.ui.components.CompanionAvatar
import com.truthlens.ui.components.TendrilsCard
import com.truthlens.ui.model.ChatMessage
import com.truthlens.ui.model.CompanionState
import com.truthlens.ui.navigation.ProfileTab
import com.truthlens.ui.screens.flows.*
import com.truthlens.ui.theme.PillShape
import com.truthlens.ui.theme.TendrilsPalette
import com.truthlens.ui.theme.TendrilsTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * HomeScreen — Personal AI Companion
 * Milestone 2 Implementation:
 * - Top greeting & profile access
 * - Central CompanionAvatar with 8 distinct assistant states
 * - Contextual suggestions
 * - Interactive conversation stream with rich responses
 * - Floating input bar with mic and attachment actions
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen() {
    val tabNavigator = LocalTabNavigator.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var companionState by remember { mutableStateOf(CompanionState.IDLE) }
    var inputText by remember { mutableStateOf("") }
    var showAttachmentSheet by remember { mutableStateOf(false) }
    var activeFlowModal by remember { mutableStateOf<String?>(null) }

    // Conversation state
    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(
                    id = "msg_init",
                    role = ChatMessage.Role.ASSISTANT,
                    content = "# Hello, Explorer!\nI am Tendrils, your personal companion. What would you like to explore or learn today?\n- Ask me to explain any complex subject\n- Test suspicious links or messages\n- Brainstorm creative stories and study plans",
                    followUps = listOf(
                        "Explain photosynthesis simply",
                        "How do I spot a fake online link?",
                        "Help me create a revision schedule"
                    )
                )
            )
        )
    }

    val suggestions = listOf(
        "Explain something",
        "Help me learn",
        "Check a link",
        "Read a document",
        "Ask anything"
    )

    // Function to handle sending a query with real LlamatikEngine streaming
    val onSendMessage: (String) -> Unit = { query ->
        if (query.isNotBlank() && companionState != CompanionState.THINKING) {
            val userMsg = ChatMessage(
                id = "u_${messages.size}",
                role = ChatMessage.Role.USER,
                content = query
            )
            messages = messages + userMsg
            inputText = ""

            // Trigger Assistant State Sequence: Thinking -> Speaking (Streaming) -> Completed -> Idle
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size - 1)
                companionState = CompanionState.THINKING
                delay(600)

                val assistantMsgId = "a_${messages.size}"
                var accumulated = ""
                val initialAssistantMsg = ChatMessage(
                    id = assistantMsgId,
                    role = ChatMessage.Role.ASSISTANT,
                    content = "",
                    isStreaming = true
                )
                messages = messages + initialAssistantMsg
                companionState = CompanionState.SPEAKING

                // Real streaming from Llamatik native engine
                com.truthlens.llm.LlamatikEngine.generateStreaming(query).collect { token ->
                    accumulated += token
                    messages = messages.map { msg ->
                        if (msg.id == assistantMsgId) {
                            msg.copy(content = accumulated)
                        } else msg
                    }
                    listState.animateScrollToItem(messages.size - 1)
                }

                val followUps = generateFollowUps(query)
                messages = messages.map { msg ->
                    if (msg.id == assistantMsgId) {
                        msg.copy(
                            content = accumulated.ifBlank { generateCompanionResponse(query) },
                            followUps = followUps,
                            isStreaming = false
                        )
                    } else msg
                }

                delay(300)
                companionState = CompanionState.COMPLETED
                delay(1000)
                companionState = CompanionState.IDLE
            }
        }
    }

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
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Welcome back,",
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
                // Quick State Indicator Pill
                Box(
                    modifier = Modifier
                        .clip(PillShape)
                        .background(companionState.auraColor)
                        .border(1.dp, companionState.primaryColor.copy(alpha = 0.4f), PillShape)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = companionState.label,
                        style = TendrilsTheme.typography.caption,
                        fontWeight = FontWeight.Bold,
                        color = companionState.primaryColor
                    )
                }

                // Avatar / Profile Access Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(TendrilsTheme.colors.primary.copy(alpha = 0.2f))
                        .border(1.5.dp, TendrilsTheme.colors.primary, CircleShape)
                        .clickable { tabNavigator.current = ProfileTab },
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

        // --- MAIN SCROLLABLE CONTENT (Avatar + Chat Thread) ---
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            // Central Companion Avatar
            item {
                Spacer(modifier = Modifier.height(10.dp))
                CompanionAvatar(
                    state = companionState,
                    onStateSelected = { companionState = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Suggestions Carousel
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    items(suggestions) { suggestion ->
                        TendrilsCard(
                            shape = PillShape,
                            padding = 8.dp,
                            backgroundColor = TendrilsTheme.colors.surfaceElevated,
                            borderColor = TendrilsTheme.colors.border,
                            onClick = { onSendMessage(suggestion) }
                        ) {
                            Text(
                                text = suggestion,
                                style = TendrilsTheme.typography.caption,
                                color = TendrilsTheme.colors.textPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )
                        }
                    }
                }
            }

            // Messages
            items(messages) { msg ->
                TendrilsMessageBubble(
                    message = msg,
                    onFollowUpClick = { followUp -> onSendMessage(followUp) }
                )
            }

            // Thinking State Indicator
            if (companionState == CompanionState.THINKING) {
                item {
                    ThinkingIndicator()
                }
            }

            // Spacer for comfortable scrolling
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // --- FLOATING INPUT BAR ---
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
                    onClick = { showAttachmentSheet = true },
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

                // Voice / Mic Demonstration Button
                IconButton(
                    onClick = {
                        // Toggle Listening state demo
                        if (companionState == CompanionState.LISTENING) {
                            companionState = CompanionState.IDLE
                        } else {
                            companionState = CompanionState.LISTENING
                            coroutineScope.launch {
                                delay(2500)
                                if (companionState == CompanionState.LISTENING) {
                                    inputText = "Can you explain how search engines work?"
                                    companionState = CompanionState.IDLE
                                }
                            }
                        }
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Voice mode",
                        tint = if (companionState == CompanionState.LISTENING) TendrilsPalette.Indigo400 else TendrilsTheme.colors.textMuted
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
                        onClick = { onSendMessage(inputText) },
                        enabled = inputText.isNotBlank() && companionState != CompanionState.THINKING
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Send",
                            tint = if (inputText.isNotBlank()) Color.White else TendrilsTheme.colors.textMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }

    // Attachment Modal Dialog
    if (showAttachmentSheet) {
        AlertDialog(
            onDismissRequest = { showAttachmentSheet = false },
            title = {
                Text(
                    text = "Add to Conversation",
                    style = TendrilsTheme.typography.h3,
                    color = TendrilsTheme.colors.textPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Choose an item for Tendrils to understand, inspect, or automate:",
                        style = TendrilsTheme.typography.body2,
                        color = TendrilsTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    AttachmentOption("Image or Photograph", "Flow D • Deepfake scan & C2PA check", Icons.Default.Search) {
                        showAttachmentSheet = false
                        activeFlowModal = "flow_d"
                    }
                    AttachmentOption("Document or Article", "Flow E • OCR text reader & study card", Icons.Default.Info) {
                        showAttachmentSheet = false
                        activeFlowModal = "flow_e"
                    }
                    AttachmentOption("Web Link or Message", "Flow F • Phishing & safety shield", Icons.Default.Check) {
                        showAttachmentSheet = false
                        activeFlowModal = "flow_f"
                    }
                    AttachmentOption("Subject Learning", "Flow C • Interactive explanation depth", Icons.Default.Star) {
                        showAttachmentSheet = false
                        activeFlowModal = "flow_c"
                    }
                    AttachmentOption("Device Task or Routine", "Flow G • Scheduled alert automation", Icons.Default.Refresh) {
                        showAttachmentSheet = false
                        activeFlowModal = "flow_g"
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
                    TextButton(onClick = { showAttachmentSheet = false }) {
                        Text("Close", color = TendrilsTheme.colors.textMuted)
                    }
                }
            },
            backgroundColor = TendrilsTheme.colors.surface,
            shape = TendrilsTheme.shapes.medium
        )
    }

    // Interactive Flow Modals
    when (activeFlowModal) {
        "flow_c" -> FlowCLearningDialog(
            onDismiss = { activeFlowModal = null },
            onSendToChat = { onSendMessage(it) }
        )
        "flow_d" -> FlowDImageUnderstandingDialog(
            onDismiss = { activeFlowModal = null },
            onSendToChat = { onSendMessage(it) }
        )
        "flow_e" -> FlowEOcrReaderDialog(
            onDismiss = { activeFlowModal = null },
            onSendToChat = { onSendMessage(it) }
        )
        "flow_f" -> FlowFLinkSafetyDialog(
            onDismiss = { activeFlowModal = null },
            onSendToChat = { onSendMessage(it) }
        )
        "flow_g" -> FlowGDeviceTaskDialog(
            onDismiss = { activeFlowModal = null }
        )
    }
}

@Composable
private fun AttachmentOption(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    TendrilsCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = TendrilsTheme.colors.surfaceElevated,
        padding = 10.dp,
        onClick = onClick
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = TendrilsTheme.colors.primary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, style = TendrilsTheme.typography.body2, fontWeight = FontWeight.SemiBold, color = TendrilsTheme.colors.textPrimary)
                Text(text = subtitle, style = TendrilsTheme.typography.caption, color = TendrilsTheme.colors.textMuted)
            }
        }
    }
}

// Realistic contextual assistant responses with markdown headers, lists, and advice
private fun generateCompanionResponse(query: String): String {
    val q = query.lowercase()
    return when {
        q.contains("photosynthesis") -> """
# Photosynthesis Explained
Photosynthesis is how plants turn natural sunlight into living energy!

## Key Steps:
- **Sunlight Capture:** Leaves absorb green light wavelengths through chlorophyll.
- **Water Absorption:** Roots draw moisture up from the soil.
- **Clean Energy Output:** The plant releases fresh oxygen for us to breathe and stores glucose as fuel.

Would you like to explore plant cells or look at an illustrated diagram next?
        """.trimIndent()

        q.contains("link") || q.contains("fake") -> """
# Digital Safety Guide: Spotting Suspicious Links
Before opening an unknown message or link, check these core indicators:

## Warning Signs to Watch:
- **Misspelled Domains:** Watch out for `rn` instead of `m` or extra subdomains.
- **Urgent Emotional Tone:** Scams often claim an account will be deleted within 10 minutes.
- **Missing SSL/Provenance:** Legitimate services use verified certificates and consistent branding.

Tip: When unsure, never enter personal credentials. Ask Tendrils to inspect the link preview first!
        """.trimIndent()

        q.contains("study") || q.contains("schedule") -> """
# Smart Study Routine
Here is a calm, sustainable study flow for your goals:

- **25 Min Deep Focus:** Silence distractions and tackle the hardest concept first.
- **5 Min Organic Break:** Stretch, drink water, and rest your eyes away from screens.
- **Active Recall:** Write down 3 things you remember before opening notes again.

What subject are we focusing on today?
        """.trimIndent()

        else -> """
# Great question!
Here is a helpful breakdown on **$query**:

- **Core Idea:** Understanding this starts with recognizing the foundational principles and how they connect.
- **Practical Application:** In daily life, this helps solve problems faster and with more clarity.
- **Next Discovery:** Exploring how this relates to broader subjects like technology and digital literacy.

Feel free to ask a follow-up or choose a suggested topic below!
        """.trimIndent()
    }
}

private fun generateFollowUps(query: String): List<String> {
    val q = query.lowercase()
    return when {
        q.contains("photosynthesis") -> listOf("Why are leaves green?", "How do plant cells differ from animal cells?")
        q.contains("link") || q.contains("fake") -> listOf("How does HTTPS work?", "What should I do if I clicked a scam link?")
        q.contains("study") -> listOf("Give me a 1-hour study planner", "How can I prepare for an exam without stress?")
        else -> listOf("Explain this in simpler terms", "Give me a real-world example", "What are the common mistakes to avoid?")
    }
}