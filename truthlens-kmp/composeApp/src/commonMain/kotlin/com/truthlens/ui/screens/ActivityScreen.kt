package com.truthlens.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import com.truthlens.ui.components.TendrilsCard
import com.truthlens.ui.navigation.HomeTab
import com.truthlens.ui.theme.PillShape
import com.truthlens.ui.theme.TendrilsPalette
import com.truthlens.ui.theme.TendrilsTheme

/**
 * Task completion status enum distinguishing required execution states.
 */
enum class TaskStatus(val label: String, val color: Color) {
    REQUESTED("Requested", TendrilsPalette.Slate600),
    WAITING_FOR_PERMISSION("Waiting for Permission", TendrilsPalette.Amber600),
    RUNNING("Running", TendrilsPalette.Indigo400),
    COMPLETED("Completed", TendrilsPalette.Emerald400),
    FAILED("Failed", TendrilsPalette.Coral500),
    CANCELLED("Cancelled", TendrilsPalette.Slate600)
}

data class ActivityConversation(
    val id: String,
    val title: String,
    val date: String,
    val preview: String,
    val category: String
)

data class ActivityTask(
    val id: String,
    val name: String,
    val date: String,
    val status: TaskStatus,
    val details: String,
    val permissionRequired: String? = null
)

data class ActivityLearning(
    val id: String,
    val topic: String,
    val depth: String,
    val date: String,
    val keyTakeaway: String
)

data class ActivitySavedItem(
    val id: String,
    val title: String,
    val type: String,
    val snippet: String,
    val date: String
)

/**
 * ActivityScreen — History, Task Tracking, Learning Activity, and Saved Items
 * Milestone 4 Implementation
 */
@Composable
fun ActivityScreen() {
    val tabNavigator = LocalTabNavigator.current

    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Chats", "Tasks", "Learning", "Saved")

    var showClearConfirmDialog by remember { mutableStateOf(false) }

    // In-memory local activity records
    var conversations by remember {
        mutableStateOf(
            listOf(
                ActivityConversation(
                    id = "c1",
                    title = "Photosynthesis & Plant Biology",
                    date = "Today • 10:14 AM",
                    preview = "Explained how chlorophyll captures sunlight and releases clean oxygen.",
                    category = "Biology"
                ),
                ActivityConversation(
                    id = "c2",
                    title = "Link Safety Check: news-update.cc",
                    date = "Yesterday • 4:30 PM",
                    preview = "Analyzed domain attributes and identified mismatch in publisher metadata.",
                    category = "Safety"
                )
            )
        )
    }

    var tasks by remember {
        mutableStateOf(
            listOf(
                ActivityTask(
                    id = "t1",
                    name = "Summarise Renewable Energy Document",
                    date = "Today • 11:00 AM",
                    status = TaskStatus.COMPLETED,
                    details = "Extracted 3 pages from PDF, parsed solar photovoltaic principles, and generated 3 flashcards."
                ),
                ActivityTask(
                    id = "t2",
                    name = "Inspect External Message Link",
                    date = "Yesterday • 4:32 PM",
                    status = TaskStatus.COMPLETED,
                    details = "Inspected domain registration certificates and warned of recent spoofing patterns."
                ),
                ActivityTask(
                    id = "t3",
                    name = "Set Daily Focus Study Reminder",
                    date = "Yesterday • 2:15 PM",
                    status = TaskStatus.WAITING_FOR_PERMISSION,
                    details = "Requires notification permission to post alerts at scheduled intervals.",
                    permissionRequired = "Notifications"
                ),
                ActivityTask(
                    id = "t4",
                    name = "Automated Calendar Sync",
                    date = "Nov 2 • 9:00 AM",
                    status = TaskStatus.CANCELLED,
                    details = "Cancelled by user before device calendar access was granted."
                )
            )
        )
    }

    var learningItems by remember {
        mutableStateOf(
            listOf(
                ActivityLearning(
                    id = "l1",
                    topic = "C2PA Content Authenticity",
                    depth = "Intermediate",
                    date = "Today",
                    keyTakeaway = "Learned how open cryptographic provenance reveals if an image was generated with AI."
                ),
                ActivityLearning(
                    id = "l2",
                    topic = "Photosynthesis Light Reactions",
                    depth = "Simple",
                    date = "Yesterday",
                    keyTakeaway = "Leaves absorb green wavelengths and convert CO2 into glucose."
                )
            )
        )
    }

    var savedItems by remember {
        mutableStateOf(
            listOf(
                ActivitySavedItem(
                    id = "s1",
                    title = "Red Flag Markers in Phishing Messages",
                    type = "Safety Guide",
                    snippet = "Urgent demands, suspicious sender addresses, mismatched links, and requests for passwords.",
                    date = "Saved Yesterday"
                ),
                ActivitySavedItem(
                    id = "s2",
                    title = "Clean Energy Quick Formulas",
                    type = "Study Note",
                    snippet = "P = V x I • Efficiency calculations and solar panel angle considerations.",
                    date = "Saved Nov 1"
                )
            )
        )
    }

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
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Activity",
                    style = TendrilsTheme.typography.h1,
                    color = TendrilsTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "History, tasks, learning progress & saved notes",
                    style = TendrilsTheme.typography.body2,
                    color = TendrilsTheme.colors.textSecondary
                )
            }

            IconButton(
                onClick = { showClearConfirmDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Clear activity",
                    tint = TendrilsTheme.colors.textMuted
                )
            }
        }

        // --- FILTER CHIPS ---
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { filter ->
                val isSelected = selectedFilter == filter
                TendrilsCard(
                    shape = PillShape,
                    padding = 8.dp,
                    backgroundColor = if (isSelected) TendrilsTheme.colors.primary.copy(alpha = 0.16f) else TendrilsTheme.colors.surface,
                    borderColor = if (isSelected) TendrilsTheme.colors.primary else TendrilsTheme.colors.border,
                    onClick = { selectedFilter = filter }
                ) {
                    Text(
                        text = filter,
                        style = TendrilsTheme.typography.caption,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) TendrilsTheme.colors.primary else TendrilsTheme.colors.textSecondary,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- CONTENT SCROLLER ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val showAll = selectedFilter == "All"

            // 1. RECENT CONVERSATIONS
            if (showAll || selectedFilter == "Chats") {
                SectionHeader("Recent Conversations", count = conversations.size)
                if (conversations.isEmpty()) {
                    EmptySectionCard("No conversations yet", "Start exploring questions with Tendrils on the Home screen.") {
                        tabNavigator.current = HomeTab
                    }
                } else {
                    conversations.forEach { chat ->
                        TendrilsCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = TendrilsTheme.colors.surface,
                            padding = 14.dp,
                            onClick = { tabNavigator.current = HomeTab }
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = chat.title,
                                        style = TendrilsTheme.typography.subtitle1,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TendrilsTheme.colors.textPrimary
                                    )
                                    Text(
                                        text = chat.date,
                                        style = TendrilsTheme.typography.caption,
                                        color = TendrilsTheme.colors.textMuted
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = chat.preview,
                                    style = TendrilsTheme.typography.body2,
                                    color = TendrilsTheme.colors.textSecondary
                                )
                            }
                        }
                    }
                }
            }

            // 2. RECENT TASKS (With expandable details & distinct states)
            if (showAll || selectedFilter == "Tasks") {
                SectionHeader("Recent Tasks", count = tasks.size)
                if (tasks.isEmpty()) {
                    EmptySectionCard("No tasks recorded", "Tasks you perform with Tendrils will be logged here.")
                } else {
                    tasks.forEach { task ->
                        ExpandableTaskCard(task = task)
                    }
                }
            }

            // 3. LEARNING ACTIVITY
            if (showAll || selectedFilter == "Learning") {
                SectionHeader("Learning Activity", count = learningItems.size)
                if (learningItems.isEmpty()) {
                    EmptySectionCard("No learning records", "Explore concepts in the Discover tab to build your learning log.")
                } else {
                    learningItems.forEach { item ->
                        TendrilsCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = TendrilsTheme.colors.surface,
                            padding = 14.dp
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.topic,
                                        style = TendrilsTheme.typography.subtitle1,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TendrilsTheme.colors.textPrimary
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(TendrilsTheme.colors.surfaceElevated, PillShape)
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = item.depth,
                                            style = TendrilsTheme.typography.caption,
                                            color = TendrilsTheme.colors.primary,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.keyTakeaway,
                                    style = TendrilsTheme.typography.body2,
                                    color = TendrilsTheme.colors.textSecondary
                                )
                            }
                        }
                    }
                }
            }

            // 4. SAVED ITEMS
            if (showAll || selectedFilter == "Saved") {
                SectionHeader("Saved Items", count = savedItems.size)
                if (savedItems.isEmpty()) {
                    EmptySectionCard("No saved items", "Bookmark helpful explanations and guides to find them here.")
                } else {
                    savedItems.forEach { saved ->
                        TendrilsCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = TendrilsTheme.colors.surface,
                            padding = 14.dp
                        ) {
                            Row(verticalAlignment = Alignment.Top) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(TendrilsPalette.Amber500.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = TendrilsPalette.Amber500,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = saved.title,
                                        style = TendrilsTheme.typography.subtitle1,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TendrilsTheme.colors.textPrimary
                                    )
                                    Text(
                                        text = saved.type,
                                        style = TendrilsTheme.typography.caption,
                                        color = TendrilsTheme.colors.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = saved.snippet,
                                        style = TendrilsTheme.typography.body2,
                                        color = TendrilsTheme.colors.textSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Local privacy reminder
            TendrilsCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = TendrilsTheme.colors.surfaceElevated,
                borderColor = TendrilsTheme.colors.border,
                padding = 12.dp
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = TendrilsTheme.colors.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "All activity records exist strictly on this device and can be cleared at any moment.",
                        style = TendrilsTheme.typography.caption,
                        color = TendrilsTheme.colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Confirmation dialog for clearing activity
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = {
                Text(
                    text = "Clear Activity History?",
                    style = TendrilsTheme.typography.h3,
                    color = TendrilsTheme.colors.textPrimary
                )
            },
            text = {
                Text(
                    text = "This will erase your recent conversations, task records, and saved learning notes from this device. This action cannot be undone.",
                    style = TendrilsTheme.typography.body2,
                    color = TendrilsTheme.colors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        conversations = emptyList()
                        tasks = emptyList()
                        learningItems = emptyList()
                        savedItems = emptyList()
                        showClearConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.error,
                        contentColor = Color.White
                    ),
                    shape = PillShape
                ) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Cancel", color = TendrilsTheme.colors.textMuted)
                }
            },
            backgroundColor = TendrilsTheme.colors.surface,
            shape = TendrilsTheme.shapes.medium
        )
    }
}

@Composable
private fun SectionHeader(title: String, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = TendrilsTheme.typography.h3,
            color = TendrilsTheme.colors.textPrimary
        )
        Text(
            text = "$count entries",
            style = TendrilsTheme.typography.caption,
            color = TendrilsTheme.colors.textMuted
        )
    }
}

@Composable
private fun ExpandableTaskCard(task: ActivityTask) {
    var expanded by remember { mutableStateOf(false) }

    TendrilsCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = TendrilsTheme.colors.surface,
        borderColor = TendrilsTheme.colors.border,
        padding = 14.dp,
        onClick = { expanded = !expanded }
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.name,
                    style = TendrilsTheme.typography.subtitle1,
                    fontWeight = FontWeight.SemiBold,
                    color = TendrilsTheme.colors.textPrimary
                )
                // State Badge
                Box(
                    modifier = Modifier
                        .background(task.status.color.copy(alpha = 0.15f), PillShape)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = task.status.label,
                        style = TendrilsTheme.typography.caption,
                        fontWeight = FontWeight.Bold,
                        color = task.status.color,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = task.date,
                style = TendrilsTheme.typography.caption,
                color = TendrilsTheme.colors.textMuted
            )

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Divider(color = TendrilsTheme.colors.border, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Task Details:",
                        style = TendrilsTheme.typography.caption,
                        fontWeight = FontWeight.Bold,
                        color = TendrilsTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = task.details,
                        style = TendrilsTheme.typography.body2,
                        color = TendrilsTheme.colors.textSecondary
                    )
                    if (task.permissionRequired != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .background(TendrilsPalette.Amber500.copy(alpha = 0.12f), PillShape)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Permission: ${task.permissionRequired}",
                                style = TendrilsTheme.typography.caption,
                                color = TendrilsPalette.Amber600,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptySectionCard(
    title: String,
    description: String,
    onAction: (() -> Unit)? = null
) {
    TendrilsCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = TendrilsTheme.colors.surfaceElevated,
        padding = 18.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = TendrilsTheme.typography.body1,
                fontWeight = FontWeight.SemiBold,
                color = TendrilsTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = TendrilsTheme.typography.caption,
                color = TendrilsTheme.colors.textMuted
            )
            if (onAction != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onAction,
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = TendrilsTheme.colors.primary,
                        contentColor = Color.White
                    )
                ) {
                    Text("Start Exploration", style = TendrilsTheme.typography.caption)
                }
            }
        }
    }
}
