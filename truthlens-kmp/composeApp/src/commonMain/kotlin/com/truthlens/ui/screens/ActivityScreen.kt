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
 * Categorical Classification for Activity Items
 * Grouped into Recents, Today, Risky, Blocked, and Saved & Learning
 */
enum class ActivityGroup(val title: String, val badgeColor: Color, val icon: ImageVector) {
    BLOCKED("Blocked Threats", TendrilsPalette.Coral500, Icons.Default.Close),
    RISKY("Risky & Flagged", TendrilsPalette.Amber600, Icons.Default.Warning),
    TODAY("Today's Activity", TendrilsPalette.Emerald400, Icons.Default.DateRange),
    RECENTS("Recent History", TendrilsPalette.Indigo400, Icons.Default.Refresh),
    SAVED("Saved & Learning", TendrilsPalette.Teal400, Icons.Default.Star)
}

data class TendrilsActivityItem(
    val id: String,
    val title: String,
    val timestamp: String,
    val group: ActivityGroup,
    val tag: String,
    val summary: String,
    val details: String? = null,
    val actionLabel: String? = null,
    val severityLevel: String? = null
)

/**
 * ActivityScreen — Structured History & Protection Logs
 * Groupings: Recents, Today, Risky, Blocked, Saved & Learning
 */
@Composable
fun ActivityScreen() {
    val tabNavigator = LocalTabNavigator.current

    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Blocked", "Risky", "Today", "Recents", "Saved")

    var showClearConfirmDialog by remember { mutableStateOf(false) }

    // Seeded realistic activity data covering security intelligence & study companion
    var activityItems by remember {
        mutableStateOf(
            listOf(
                // --- BLOCKED ---
                TendrilsActivityItem(
                    id = "b1",
                    title = "Phishing Link Prevented",
                    timestamp = "Today • 1:45 PM",
                    group = ActivityGroup.BLOCKED,
                    tag = "Phishing Shield",
                    summary = "Blocked connection to spoofed domain: `bank-login-secure.cc`",
                    details = "Domain age is 2 days. SSL certificate issued by unknown authority. Spoof target: Mobile Banking.",
                    severityLevel = "Blocked",
                    actionLabel = "Inspect Threat Details"
                ),
                TendrilsActivityItem(
                    id = "b2",
                    title = "Unauthorized Background Telemetry Blocked",
                    timestamp = "Today • 11:20 AM",
                    group = ActivityGroup.BLOCKED,
                    tag = "Privacy Sandbox",
                    summary = "Prevented background third-party tracking beacon from accessing device identifiers.",
                    details = "Blocked destination IP 198.51.100.44 without affecting host app performance.",
                    severityLevel = "Prevented"
                ),

                // --- RISKY ---
                TendrilsActivityItem(
                    id = "r1",
                    title = "Synthetic Media Detected (87% Deepfake)",
                    timestamp = "Today • 12:10 PM",
                    group = ActivityGroup.RISKY,
                    tag = "Deepfake Scan",
                    summary = "Scan flagged face-swapped artifacts in shared media `interview_clip.mp4`",
                    details = "TFLite Artifact Scanner identified irregular frequency harmonics around facial boundaries. C2PA provenance signature is absent.",
                    severityLevel = "High Risk",
                    actionLabel = "View Artifact Heatmap"
                ),
                TendrilsActivityItem(
                    id = "r2",
                    title = "Suspicious SMS Link Warning",
                    timestamp = "Yesterday • 4:15 PM",
                    group = ActivityGroup.RISKY,
                    tag = "Link Shield",
                    summary = "Warning issued for SMS message claiming uncollected package with urgent countdown.",
                    details = "Sender number unverified. Link redirects through three intermediate shorteners.",
                    severityLevel = "Suspicious"
                ),

                // --- TODAY ---
                TendrilsActivityItem(
                    id = "t1",
                    title = "Background Media Scanner Verified 4 Photos",
                    timestamp = "Today • 10:30 AM",
                    group = ActivityGroup.TODAY,
                    tag = "Media Scanner",
                    summary = "4 newly captured camera photos scanned locally on-device. All verified authentic.",
                    details = "Camera hardware metadata matches native Android Keystore attestation. No synthetic artifacts detected."
                ),
                TendrilsActivityItem(
                    id = "t2",
                    title = "Photosynthesis & Plant Biology Chat",
                    timestamp = "Today • 9:15 AM",
                    group = ActivityGroup.TODAY,
                    tag = "Study Chat",
                    summary = "Explored how chlorophyll converts sunlight into glucose and releases oxygen.",
                    actionLabel = "Open Chat"
                ),

                // --- RECENTS ---
                TendrilsActivityItem(
                    id = "rc1",
                    title = "OCR Study Card Generation: Renewable Energy",
                    timestamp = "Yesterday • 3:30 PM",
                    group = ActivityGroup.RECENTS,
                    tag = "OCR Reader",
                    summary = "Extracted 3 pages from textbook photo and created 5 interactive flashcards.",
                    details = "Processed entirely offline via ML Kit Latin Text Recognition engine."
                ),
                TendrilsActivityItem(
                    id = "rc2",
                    title = "Set Daily Focus Study Reminder",
                    timestamp = "Nov 3 • 2:15 PM",
                    group = ActivityGroup.RECENTS,
                    tag = "Routine Task",
                    summary = "Scheduled gentle 25-minute study intervals with natural break reminders."
                ),

                // --- SAVED ---
                TendrilsActivityItem(
                    id = "s1",
                    title = "Red Flag Markers in Phishing Messages",
                    timestamp = "Saved Yesterday",
                    group = ActivityGroup.SAVED,
                    tag = "Safety Guide",
                    summary = "Urgent emotional tone, mismatched sender domains, and credential requests."
                ),
                TendrilsActivityItem(
                    id = "s2",
                    title = "Clean Energy Quick Formulas",
                    timestamp = "Saved Nov 1",
                    group = ActivityGroup.SAVED,
                    tag = "Study Note",
                    summary = "P = V x I • Photovoltaic efficiency factors and tilt angle optimization."
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
                    text = "Organized by Recents, Today, Risky & Blocked threats",
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

        // --- FILTER CHIPS WITH BADGES ---
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { filter ->
                val isSelected = selectedFilter == filter
                val count = when (filter) {
                    "All" -> activityItems.size
                    "Blocked" -> activityItems.count { it.group == ActivityGroup.BLOCKED }
                    "Risky" -> activityItems.count { it.group == ActivityGroup.RISKY }
                    "Today" -> activityItems.count { it.group == ActivityGroup.TODAY }
                    "Recents" -> activityItems.count { it.group == ActivityGroup.RECENTS }
                    "Saved" -> activityItems.count { it.group == ActivityGroup.SAVED }
                    else -> 0
                }

                TendrilsCard(
                    shape = PillShape,
                    padding = 8.dp,
                    backgroundColor = if (isSelected) TendrilsTheme.colors.primary.copy(alpha = 0.16f) else TendrilsTheme.colors.surface,
                    borderColor = if (isSelected) TendrilsTheme.colors.primary else TendrilsTheme.colors.border,
                    onClick = { selectedFilter = filter }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = filter,
                            style = TendrilsTheme.typography.caption,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) TendrilsTheme.colors.primary else TendrilsTheme.colors.textSecondary
                        )
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isSelected) TendrilsTheme.colors.primary else TendrilsTheme.colors.surfaceElevated,
                                    PillShape
                                )
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "$count",
                                style = TendrilsTheme.typography.caption,
                                fontSize = 10.sp,
                                color = if (isSelected) Color.White else TendrilsTheme.colors.textMuted
                            )
                        }
                    }
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

            // 1. BLOCKED SECTION
            if (showAll || selectedFilter == "Blocked") {
                val blocked = activityItems.filter { it.group == ActivityGroup.BLOCKED }
                if (blocked.isNotEmpty() || selectedFilter == "Blocked") {
                    GroupHeader(ActivityGroup.BLOCKED, blocked.size)
                    if (blocked.isEmpty()) {
                        EmptyCard("No blocked threats", "No malicious links or telemetry have been blocked.")
                    } else {
                        blocked.forEach { item ->
                            ActivityItemCard(
                                item = item,
                                onActionClick = { /* Inspect action */ }
                            )
                        }
                    }
                }
            }

            // 2. RISKY SECTION
            if (showAll || selectedFilter == "Risky") {
                val risky = activityItems.filter { it.group == ActivityGroup.RISKY }
                if (risky.isNotEmpty() || selectedFilter == "Risky") {
                    GroupHeader(ActivityGroup.RISKY, risky.size)
                    if (risky.isEmpty()) {
                        EmptyCard("No risky items", "All scanned content is verified and safe.")
                    } else {
                        risky.forEach { item ->
                            ActivityItemCard(
                                item = item,
                                onActionClick = { /* View heatmap */ }
                            )
                        }
                    }
                }
            }

            // 3. TODAY'S ACTIVITY
            if (showAll || selectedFilter == "Today") {
                val today = activityItems.filter { it.group == ActivityGroup.TODAY }
                if (today.isNotEmpty() || selectedFilter == "Today") {
                    GroupHeader(ActivityGroup.TODAY, today.size)
                    if (today.isEmpty()) {
                        EmptyCard("No activity today", "Start a study conversation or media scan to see today's activity.")
                    } else {
                        today.forEach { item ->
                            ActivityItemCard(
                                item = item,
                                onActionClick = {
                                    if (item.actionLabel == "Open Chat") {
                                        tabNavigator.current = HomeTab
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // 4. RECENTS SECTION
            if (showAll || selectedFilter == "Recents") {
                val recents = activityItems.filter { it.group == ActivityGroup.RECENTS }
                if (recents.isNotEmpty() || selectedFilter == "Recents") {
                    GroupHeader(ActivityGroup.RECENTS, recents.size)
                    if (recents.isEmpty()) {
                        EmptyCard("No recent history", "Completed tasks and past study logs will show here.")
                    } else {
                        recents.forEach { item ->
                            ActivityItemCard(item = item)
                        }
                    }
                }
            }

            // 5. SAVED & LEARNING
            if (showAll || selectedFilter == "Saved") {
                val saved = activityItems.filter { it.group == ActivityGroup.SAVED }
                if (saved.isNotEmpty() || selectedFilter == "Saved") {
                    GroupHeader(ActivityGroup.SAVED, saved.size)
                    if (saved.isEmpty()) {
                        EmptyCard("No saved notes", "Bookmark helpful answers and safety tips to see them here.")
                    } else {
                        saved.forEach { item ->
                            ActivityItemCard(item = item)
                        }
                    }
                }
            }

            // Privacy Assurance Card
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
                        text = "All protection logs and activity records exist strictly on this device and can be cleared at any time.",
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
                    text = "This will erase all protection logs, recent chats, risky alerts, and saved study notes from this device.",
                    style = TendrilsTheme.typography.body2,
                    color = TendrilsTheme.colors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        activityItems = emptyList()
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
private fun GroupHeader(group: ActivityGroup, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(group.badgeColor.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = group.icon,
                    contentDescription = null,
                    tint = group.badgeColor,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                text = group.title,
                style = TendrilsTheme.typography.h3,
                color = TendrilsTheme.colors.textPrimary
            )
        }
        Text(
            text = "$count entries",
            style = TendrilsTheme.typography.caption,
            color = TendrilsTheme.colors.textMuted
        )
    }
}

@Composable
private fun ActivityItemCard(
    item: TendrilsActivityItem,
    onActionClick: (() -> Unit)? = null
) {
    var isExpanded by remember { mutableStateOf(false) }

    TendrilsCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = TendrilsTheme.colors.surface,
        borderColor = if (item.group == ActivityGroup.BLOCKED) {
            TendrilsPalette.Coral500.copy(alpha = 0.35f)
        } else if (item.group == ActivityGroup.RISKY) {
            TendrilsPalette.Amber600.copy(alpha = 0.35f)
        } else TendrilsTheme.colors.border,
        padding = 14.dp,
        onClick = { if (item.details != null) isExpanded = !isExpanded }
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tag & Severity Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(item.group.badgeColor.copy(alpha = 0.15f), PillShape)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.tag,
                            style = TendrilsTheme.typography.caption,
                            fontWeight = FontWeight.Bold,
                            color = item.group.badgeColor,
                            fontSize = 11.sp
                        )
                    }

                    if (item.severityLevel != null) {
                        Box(
                            modifier = Modifier
                                .background(item.group.badgeColor, PillShape)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = item.severityLevel,
                                style = TendrilsTheme.typography.caption,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Text(
                    text = item.timestamp,
                    style = TendrilsTheme.typography.caption,
                    color = TendrilsTheme.colors.textMuted
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.title,
                style = TendrilsTheme.typography.subtitle1,
                fontWeight = FontWeight.SemiBold,
                color = TendrilsTheme.colors.textPrimary
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = item.summary,
                style = TendrilsTheme.typography.body2,
                color = TendrilsTheme.colors.textSecondary
            )

            // Expandable details if present
            if (item.details != null && isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = TendrilsTheme.colors.border, thickness = 1.dp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Technical Details:",
                    style = TendrilsTheme.typography.caption,
                    fontWeight = FontWeight.Bold,
                    color = TendrilsTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.details,
                    style = TendrilsTheme.typography.body2,
                    color = TendrilsTheme.colors.textSecondary
                )
            }

            // Action button if present
            if (item.actionLabel != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { onActionClick?.invoke() }) {
                        Text(
                            text = item.actionLabel,
                            style = TendrilsTheme.typography.caption,
                            fontWeight = FontWeight.Bold,
                            color = item.group.badgeColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyCard(title: String, subtitle: String) {
    TendrilsCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = TendrilsTheme.colors.surfaceElevated,
        padding = 16.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = TendrilsTheme.colors.textMuted,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = TendrilsTheme.typography.subtitle1,
                fontWeight = FontWeight.SemiBold,
                color = TendrilsTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = TendrilsTheme.typography.caption,
                color = TendrilsTheme.colors.textMuted
            )
        }
    }
}
