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
                        "How do search engines work?",
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
                try {
                    try { listState.scrollToItem(messages.size - 1) } catch (_: Throwable) {}
                    companionState = CompanionState.THINKING
                    delay(180)

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

                    var ttsChunkBuffer = ""
                    var isFirstTtsChunk = true

                    // Real streaming from Llamatik engine with early 5-word TTS start
                    com.truthlens.llm.LlamatikEngine.generateStreaming(query).collect { token ->
                        accumulated += token
                        ttsChunkBuffer += token

                        val meaningfulWordCount = ttsChunkBuffer
                            .split(Regex("\\s+"))
                            .count { word -> word.any { ch -> ch.isLetterOrDigit() } }

                        val hasClauseBreak = token.contains("\n") || token.contains(".") ||
                            token.contains("!") || token.contains("?") || token.contains(":")

                        if (isFirstTtsChunk && (meaningfulWordCount >= 5 || (meaningfulWordCount >= 3 && hasClauseBreak))) {
                            com.truthlens.voice.TendrilsTts.speakChunk(ttsChunkBuffer, flush = true)
                            ttsChunkBuffer = ""
                            isFirstTtsChunk = false
                        } else if (!isFirstTtsChunk && ((meaningfulWordCount >= 5 && hasClauseBreak) || meaningfulWordCount >= 9)) {
                            com.truthlens.voice.TendrilsTts.speakChunk(ttsChunkBuffer, flush = false)
                            ttsChunkBuffer = ""
                        }

                        messages = messages.map { msg ->
                            if (msg.id == assistantMsgId) {
                                msg.copy(content = accumulated)
                            } else msg
                        }
                        try { listState.scrollToItem(messages.size - 1) } catch (_: Throwable) {}
                    }

                    val followUps = generateFollowUps(query)
                    val finalContent = accumulated.ifBlank { generateCompanionResponse(query) }
                    messages = messages.map { msg ->
                        if (msg.id == assistantMsgId) {
                            msg.copy(
                                content = finalContent,
                                followUps = followUps,
                                isStreaming = false
                            )
                        } else msg
                    }

                    // Flush any remaining unspoken trailing words (or full fallback response)
                    if (accumulated.isBlank()) {
                        com.truthlens.voice.TendrilsTts.speakChunk(finalContent, flush = true)
                    } else if (ttsChunkBuffer.any { it.isLetterOrDigit() }) {
                        com.truthlens.voice.TendrilsTts.speakChunk(ttsChunkBuffer, flush = isFirstTtsChunk)
                    }

                    delay(300)
                    companionState = CompanionState.COMPLETED
                    delay(1000)
                    companionState = CompanionState.IDLE
                } catch (e: Throwable) {
                    println("HomeScreen message processing error: ${e.message}")
                    companionState = CompanionState.IDLE
                }
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
                        .background(TendrilsTheme.colors.primary.copy(alpha = 0.14f))
                        .border(1.5.dp, TendrilsTheme.colors.primary, CircleShape)
                        .clickable { tabNavigator.current = ProfileTab },
                    contentAlignment = Alignment.Center
                ) {
                    com.truthlens.ui.components.TendrilOrganism(
                        state = companionState,
                        size = 34.dp,
                        archetype = com.truthlens.ui.components.AvatarManager.selectedArchetype,
                        interactiveOnTap = false
                    )
                }
            }
        }

        // --- PINNED COMPANION HERO (Always visible, does not slide off screen) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            CompanionAvatar(
                state = companionState,
                onStateSelected = { companionState = it },
                size = 110.dp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // --- MAIN SCROLLABLE CHAT THREAD ---
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
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

                // Vision Camera / Lens Button
                IconButton(
                    onClick = { activeFlowModal = "flow_vision" },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Vision Lens",
                        tint = TendrilsPalette.Sprout500
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
                    AttachmentOption("Vision Camera / Lens", "Real-time AI scene understanding & authenticity (<450ms)", Icons.Default.Search) {
                        showAttachmentSheet = false
                        activeFlowModal = "flow_vision"
                    }
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
        "flow_vision" -> FlowVisionDialog(
            onDismiss = { activeFlowModal = null },
            onSendToChat = { onSendMessage(it) }
        )
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

private fun generateCompanionResponse(query: String): String {
    return com.truthlens.llm.LlamatikEngine.generateOnDeviceAnswer(query)
}

private fun generateFollowUps(query: String): List<String> {
    val q = query.lowercase()
    return when {
        q.contains("search engine") -> listOf("How do web crawlers work?", "How do I spot a fake online link?", "What is PageRank?")
        q.contains("link") || q.contains("fake") || q.contains("scam") || q.contains("phish") -> listOf("How does HTTPS work?", "What should I do if I clicked a scam link?", "How can I spot a deepfake image?")
        q.contains("study") || q.contains("schedule") || q.contains("learn") -> listOf("How does spaced repetition work?", "How do search engines work?", "Explain gravity and black holes")
        q.contains("space") || q.contains("gravity") || q.contains("black hole") -> listOf("What is an event horizon?", "How does solar energy work?", "How do rockets escape Earth?")
        q.contains("photosynthesis") || q.contains("chlorophyll") -> listOf("Why are leaves green?", "How do plant cells differ from animal cells?")
        else -> listOf("Explain this in simpler terms", "Give me a real-world example", "How do I spot a fake online link?")
    }
}