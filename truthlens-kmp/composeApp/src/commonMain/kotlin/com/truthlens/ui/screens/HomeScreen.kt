package com.truthlens.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import com.truthlens.llm.ConversationMemory
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
import com.truthlens.voice.TendrilsStt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen() {
    val tabNavigator = LocalTabNavigator.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var companionState by remember { mutableStateOf(CompanionState.IDLE) }
    var inputText by remember { mutableStateOf("") }
    var showAttachmentSheet by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) }
    var activeFlowModal by remember { mutableStateOf<String?>(null) }

    val initialWelcomeMessage = remember {
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
    }

    var messages by remember { mutableStateOf(listOf(initialWelcomeMessage)) }

    val suggestions = listOf(
        "Explain something",
        "Help me learn",
        "Check a link",
        "Read a document",
        "Continue"
    )

    val onSendMessage: (String) -> Unit = { query ->
        if (query.isNotBlank() && companionState != CompanionState.THINKING) {
            val userMsg = ChatMessage(
                id = "u_${messages.size}",
                role = ChatMessage.Role.USER,
                content = query
            )
            messages = messages + userMsg
            inputText = ""

            coroutineScope.launch {
                try {
                    try { listState.scrollToItem(messages.size - 1) } catch (_: Throwable) {}
                    companionState = CompanionState.THINKING
                    delay(160)

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

                    // Real streaming from Llamatik engine with early 5-word TTS start & ConversationMemory
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

                    if (accumulated.isBlank()) {
                        com.truthlens.voice.TendrilsTts.speakChunk(finalContent, flush = true)
                    } else if (ttsChunkBuffer.any { it.isLetterOrDigit() }) {
                        com.truthlens.voice.TendrilsTts.speakChunk(ttsChunkBuffer, flush = isFirstTtsChunk)
                    }

                    // Keep avatar speaking as long as TTS is actively playing audio
                    var speechWaitCount = 0
                    while (com.truthlens.voice.TendrilsTts.isSpeaking() && speechWaitCount < 350) {
                        delay(100)
                        speechWaitCount++
                    }

                    delay(200)
                    companionState = CompanionState.COMPLETED
                    delay(800)
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
        // --- TOP SECTION: GREETING, MEMORY/HISTORY & PROFILE ACCESS ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
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
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Memory & History Button
                Box(
                    modifier = Modifier
                        .clip(PillShape)
                        .background(TendrilsTheme.colors.surfaceElevated)
                        .border(1.dp, TendrilsTheme.colors.border, PillShape)
                        .clickable { showHistoryDialog = true }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "History and Memory",
                            tint = TendrilsTheme.colors.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Memory (${ConversationMemory.turns.size})",
                            style = TendrilsTheme.typography.caption,
                            fontWeight = FontWeight.SemiBold,
                            color = TendrilsTheme.colors.textPrimary
                        )
                    }
                }

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

            if (companionState == CompanionState.THINKING) {
                item {
                    ThinkingIndicator()
                }
            }

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
            borderColor = if (companionState == CompanionState.LISTENING) TendrilsPalette.Indigo400 else TendrilsTheme.colors.border,
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
                            text = if (companionState == CompanionState.LISTENING) "Listening... Speak now" else "Ask Tendrils anything...",
                            style = TendrilsTheme.typography.body1,
                            color = if (companionState == CompanionState.LISTENING) TendrilsPalette.Indigo400 else TendrilsTheme.colors.textMuted
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

                // Real Talk / Microphone Recording & Transcription Button
                val isRecording = companionState == CompanionState.LISTENING
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (isRecording) TendrilsPalette.Indigo400.copy(alpha = 0.18f)
                            else Color.Transparent
                        )
                        .clickable {
                            if (isRecording) {
                                TendrilsStt.stopListening()
                                companionState = CompanionState.IDLE
                            } else {
                                companionState = CompanionState.LISTENING
                                TendrilsStt.startListening(
                                    languageCode = "en-US",
                                    onPartialResult = { partial ->
                                        inputText = partial
                                    },
                                    onFinalResult = { transcript ->
                                        companionState = CompanionState.IDLE
                                        if (transcript.isNotBlank()) {
                                            inputText = transcript
                                            onSendMessage(transcript)
                                        }
                                    },
                                    onError = { _ ->
                                        companionState = CompanionState.IDLE
                                    }
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    RealMicrophoneIcon(
                        isRecording = isRecording,
                        tint = if (isRecording) TendrilsPalette.Indigo400 else TendrilsTheme.colors.primary
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

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

    // Conversation History & Companion Memory Dialog
    if (showHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showHistoryDialog = false },
            title = {
                Text(
                    text = "History & Companion Memory",
                    style = TendrilsTheme.typography.h3,
                    color = TendrilsTheme.colors.textPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Active Memory Context Card
                    TendrilsCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = TendrilsTheme.colors.primary.copy(alpha = 0.12f),
                        borderColor = TendrilsTheme.colors.primary,
                        padding = 12.dp
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Active Memory Topic",
                                style = TendrilsTheme.typography.caption,
                                fontWeight = FontWeight.Bold,
                                color = TendrilsTheme.colors.primary
                            )
                            Text(
                                text = ConversationMemory.activeSubjectTitle,
                                style = TendrilsTheme.typography.body1,
                                fontWeight = FontWeight.SemiBold,
                                color = TendrilsTheme.colors.textPrimary
                            )
                            Text(
                                text = "${ConversationMemory.turns.size} turns remembered in context • Step ${ConversationMemory.continuationStep}",
                                style = TendrilsTheme.typography.caption,
                                color = TendrilsTheme.colors.textSecondary
                            )
                            if (ConversationMemory.turns.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            showHistoryDialog = false
                                            onSendMessage("Continue")
                                        },
                                        shape = PillShape,
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = TendrilsTheme.colors.primary,
                                            contentColor = Color.White
                                        ),
                                        modifier = Modifier.weight(1f).height(34.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("Continue Topic", fontSize = 11.sp)
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            showHistoryDialog = false
                                            onSendMessage("Give me a real-world example")
                                        },
                                        shape = PillShape,
                                        modifier = Modifier.weight(1f).height(34.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("Real Example", fontSize = 11.sp, color = TendrilsTheme.colors.primary)
                                    }
                                }
                            }
                        }
                    }

                    // Recent Memory Turns
                    if (ConversationMemory.turns.isNotEmpty()) {
                        Text(
                            text = "Recent Remembered Prompts",
                            style = TendrilsTheme.typography.caption,
                            fontWeight = FontWeight.Bold,
                            color = TendrilsTheme.colors.textSecondary
                        )
                        ConversationMemory.turns.takeLast(3).reversed().forEach { turn ->
                            TendrilsCard(
                                modifier = Modifier.fillMaxWidth(),
                                backgroundColor = TendrilsTheme.colors.surfaceElevated,
                                padding = 8.dp,
                                onClick = {
                                    showHistoryDialog = false
                                    onSendMessage("Continue explaining ${turn.subjectTitle}")
                                }
                            ) {
                                Column {
                                    Text(
                                        text = turn.subjectTitle,
                                        style = TendrilsTheme.typography.caption,
                                        fontWeight = FontWeight.Bold,
                                        color = TendrilsTheme.colors.primary
                                    )
                                    Text(
                                        text = "Q: ${turn.userQuery}",
                                        style = TendrilsTheme.typography.caption,
                                        color = TendrilsTheme.colors.textPrimary
                                    )
                                }
                            }
                        }
                    }

                    // Saved Chat Sessions
                    if (ConversationMemory.savedSessions.isNotEmpty()) {
                        Text(
                            text = "Saved Chat Sessions",
                            style = TendrilsTheme.typography.caption,
                            fontWeight = FontWeight.Bold,
                            color = TendrilsTheme.colors.textSecondary
                        )
                        ConversationMemory.savedSessions.take(3).forEach { session ->
                            TendrilsCard(
                                modifier = Modifier.fillMaxWidth(),
                                backgroundColor = TendrilsTheme.colors.surfaceElevated,
                                padding = 8.dp,
                                onClick = {
                                    messages = session.messages
                                    ConversationMemory.activeSubjectTitle = session.summaryTopic
                                    showHistoryDialog = false
                                }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = session.title,
                                            style = TendrilsTheme.typography.body2,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TendrilsTheme.colors.textPrimary
                                        )
                                        Text(
                                            text = "${session.summaryTopic} • ${session.turnCount} messages",
                                            style = TendrilsTheme.typography.caption,
                                            color = TendrilsTheme.colors.textMuted
                                        )
                                    }
                                    Text(
                                        text = "Restore",
                                        style = TendrilsTheme.typography.caption,
                                        fontWeight = FontWeight.Bold,
                                        color = TendrilsTheme.colors.primary
                                    )
                                }
                            }
                        }
                    }
                }
            },
            buttons = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = {
                            ConversationMemory.archiveSession(messages)
                            ConversationMemory.clearActiveMemory()
                            messages = listOf(initialWelcomeMessage)
                            showHistoryDialog = false
                        }
                    ) {
                        Text("Save & New Chat", color = TendrilsTheme.colors.primary, fontWeight = FontWeight.Bold)
                    }
                    TextButton(onClick = { showHistoryDialog = false }) {
                        Text("Close", color = TendrilsTheme.colors.textMuted)
                    }
                }
            },
            backgroundColor = TendrilsTheme.colors.surface,
            shape = TendrilsTheme.shapes.medium
        )
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

/**
 * Custom vector-drawn Real Microphone & Talk Wave Icon.
 * Displays a crisp capsule microphone with cradle, stand, and live voice waves when recording.
 */
@Composable
private fun RealMicrophoneIcon(
    isRecording: Boolean,
    tint: Color
) {
    Canvas(modifier = Modifier.size(22.dp)) {
        val w = size.width
        val h = size.height
        val cx = w / 2f

        // 1. Microphone Capsule (Rounded vertical pill at top center)
        val capW = w * 0.30f
        val capH = h * 0.48f
        drawRoundRect(
            color = tint,
            topLeft = Offset(cx - capW / 2f, h * 0.08f),
            size = Size(capW, capH),
            cornerRadius = CornerRadius(capW / 2f, capW / 2f)
        )

        // 2. U-Shaped Microphone Cradle
        val cradlePath = Path().apply {
            moveTo(cx - w * 0.26f, h * 0.36f)
            quadraticBezierTo(cx - w * 0.26f, h * 0.72f, cx, h * 0.72f)
            quadraticBezierTo(cx + w * 0.26f, h * 0.72f, cx + w * 0.26f, h * 0.36f)
        }
        drawPath(
            path = cradlePath,
            color = tint,
            style = Stroke(width = 2.2f, cap = StrokeCap.Round)
        )

        // 3. Vertical Stem & Base Stand
        drawLine(
            color = tint,
            start = Offset(cx, h * 0.72f),
            end = Offset(cx, h * 0.90f),
            strokeWidth = 2.2f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = Offset(cx - w * 0.18f, h * 0.90f),
            end = Offset(cx + w * 0.18f, h * 0.90f),
            strokeWidth = 2.2f,
            cap = StrokeCap.Round
        )

        // 4. Live Talk Sound Waves on left and right when actively listening
        if (isRecording) {
            drawLine(
                color = tint,
                start = Offset(w * 0.08f, h * 0.24f),
                end = Offset(w * 0.08f, h * 0.48f),
                strokeWidth = 2.0f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = tint,
                start = Offset(w * 0.92f, h * 0.24f),
                end = Offset(w * 0.92f, h * 0.48f),
                strokeWidth = 2.0f,
                cap = StrokeCap.Round
            )
        }
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
        q.contains("search engine") || q.contains("crawler") -> listOf("Continue", "How do web crawlers work?", "Give me a real-world example")
        q.contains("link") || q.contains("fake") || q.contains("scam") || q.contains("phish") -> listOf("Continue", "How does HTTPS work?", "What should I do if I clicked a scam link?")
        q.contains("study") || q.contains("schedule") || q.contains("learn") -> listOf("Continue", "How does spaced repetition work?", "Give me a real-world example")
        q.contains("space") || q.contains("gravity") || q.contains("black hole") -> listOf("Continue", "What is an event horizon?", "Explain this in simpler terms")
        q.contains("photosynthesis") || q.contains("chlorophyll") -> listOf("Continue", "Explain this in simpler terms", "Give me a real-world example")
        else -> listOf("Continue", "Explain this in simpler terms", "Give me a real-world example")
    }
}