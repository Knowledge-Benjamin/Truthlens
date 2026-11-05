package com.truthlens.ui.screens.profile

import androidx.compose.animation.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.truthlens.ui.components.TendrilsCard
import com.truthlens.ui.theme.PillShape
import com.truthlens.ui.theme.TendrilsPalette
import com.truthlens.ui.theme.TendrilsTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class LinkedCompanion(
    val id: String,
    val name: String,
    val role: String,
    val pairingCode: String,
    val statusText: String,
    val sharedNotesCount: Int,
    val avatarInitial: String,
    val isParent: Boolean = false
)

/**
 * My Friends & Family — Private Code-Linked Companion Circle
 * No random directory discovery. Connections require exchanging unique codes.
 */
@Composable
fun FriendsAndFamilyDialog(
    onDismiss: () -> Unit
) {
    var myUniqueCode by remember { mutableStateOf("TEN-8492-LILY") }
    var inputCode by remember { mutableStateOf("") }
    var isConnecting by remember { mutableStateOf(false) }
    var connectionFeedback by remember { mutableStateOf<String?>(null) }
    var waveFeedback by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    var linkedFriends by remember {
        mutableStateOf(
            listOf(
                LinkedCompanion(
                    id = "f1",
                    name = "Mama Sarah",
                    role = "Parent / Guardian",
                    pairingCode = "TEN-2291-MAMA",
                    statusText = "Safe Protection Active • Last active 20m ago",
                    sharedNotesCount = 2,
                    avatarInitial = "M",
                    isParent = true
                ),
                LinkedCompanion(
                    id = "f2",
                    name = "Brian Kato",
                    role = "Study Buddy • Senior 3",
                    pairingCode = "TEN-5143-BRIO",
                    statusText = "Studying Biology & Solar formulas",
                    sharedNotesCount = 4,
                    avatarInitial = "B",
                    isParent = false
                ),
                LinkedCompanion(
                    id = "f3",
                    name = "Amina",
                    role = "Sister • Primary 6",
                    pairingCode = "TEN-9904-MINA",
                    statusText = "Learning Luganda & Nature science",
                    sharedNotesCount = 1,
                    avatarInitial = "A",
                    isParent = false
                )
            )
        )
    }

    SettingsSubScreenDialog(
        title = "My Friends & Family",
        icon = Icons.Default.Person,
        onDismiss = onDismiss
    ) {
        // Privacy Banner
        TendrilsCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = TendrilsPalette.Emerald400.copy(alpha = 0.12f),
            borderColor = TendrilsPalette.Emerald400.copy(alpha = 0.4f),
            padding = 12.dp
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = TendrilsPalette.Emerald400,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Strictly Private & Code-Linked",
                        style = TendrilsTheme.typography.subtitle1,
                        fontWeight = FontWeight.Bold,
                        color = TendrilsTheme.colors.textPrimary
                    )
                    Text(
                        text = "No public search or random stranger matching. You only link with trusted people by exchanging a unique 6-character code.",
                        style = TendrilsTheme.typography.caption,
                        color = TendrilsTheme.colors.textSecondary
                    )
                }
            }
        }

        // My Personal Code Card
        Text(
            text = "Your Companion Share Code",
            style = TendrilsTheme.typography.subtitle1,
            fontWeight = FontWeight.SemiBold,
            color = TendrilsTheme.colors.textPrimary
        )

        TendrilsCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = TendrilsTheme.colors.surfaceElevated,
            borderColor = TendrilsTheme.colors.primary.copy(alpha = 0.5f),
            padding = 14.dp
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Give this code to a friend or parent to link companions:",
                    style = TendrilsTheme.typography.caption,
                    color = TendrilsTheme.colors.textMuted
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(PillShape)
                        .background(TendrilsTheme.colors.primary.copy(alpha = 0.15f))
                        .border(1.5.dp, TendrilsTheme.colors.primary, PillShape)
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = myUniqueCode,
                        style = TendrilsTheme.typography.h2,
                        letterSpacing = 2.sp,
                        color = TendrilsTheme.colors.primary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            connectionFeedback = "Code copied to clipboard: $myUniqueCode"
                        },
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = PillShape,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = TendrilsTheme.colors.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Code", style = TendrilsTheme.typography.caption, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            myUniqueCode = "TEN-${(1000..9999).random()}-${listOf("ROOT", "SPROUT", "LEAF", "BUD", "BLOOM").random()}"
                            connectionFeedback = "Generated fresh private code: $myUniqueCode"
                        },
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = PillShape,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = TendrilsTheme.colors.surface,
                            contentColor = TendrilsTheme.colors.textPrimary
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("New Code", style = TendrilsTheme.typography.caption)
                    }
                }
            }
        }

        Divider(color = TendrilsTheme.colors.border)

        // Connect with a Friend's Code
        Text(
            text = "Link a Friend or Family Member",
            style = TendrilsTheme.typography.subtitle1,
            fontWeight = FontWeight.SemiBold,
            color = TendrilsTheme.colors.textPrimary
        )

        OutlinedTextField(
            value = inputCode,
            onValueChange = { inputCode = it.uppercase() },
            label = { Text("Enter their unique code (e.g. TEN-5143-BRIO)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = TendrilsTheme.colors.textPrimary,
                focusedBorderColor = TendrilsTheme.colors.primary,
                unfocusedBorderColor = TendrilsTheme.colors.border
            )
        )

        Button(
            onClick = {
                if (inputCode.isNotBlank()) {
                    scope.launch {
                        isConnecting = true
                        delay(600)
                        val newFriend = LinkedCompanion(
                            id = "f_${linkedFriends.size + 1}",
                            name = "Companion Friend (${inputCode.take(8)})",
                            role = "Study Friend",
                            pairingCode = inputCode,
                            statusText = "Connected securely • Online",
                            sharedNotesCount = 0,
                            avatarInitial = "F"
                        )
                        linkedFriends = linkedFriends + newFriend
                        connectionFeedback = "Successfully linked with ${newFriend.name}!"
                        inputCode = ""
                        isConnecting = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(44.dp),
            shape = PillShape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = TendrilsTheme.colors.primary,
                contentColor = Color.White
            )
        ) {
            Text(if (isConnecting) "Linking Companions..." else "Link Companion Securely")
        }

        if (connectionFeedback != null) {
            Text(
                text = connectionFeedback!!,
                style = TendrilsTheme.typography.caption,
                color = TendrilsTheme.colors.primary
            )
        }

        Divider(color = TendrilsTheme.colors.border)

        // Connected Friends & Family List
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Connected Circle (${linkedFriends.size})",
                style = TendrilsTheme.typography.subtitle1,
                fontWeight = FontWeight.SemiBold,
                color = TendrilsTheme.colors.textPrimary
            )
            Text(
                text = "Private Sync",
                style = TendrilsTheme.typography.caption,
                color = TendrilsPalette.Emerald400
            )
        }

        if (waveFeedback != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TendrilsPalette.Amber500.copy(alpha = 0.15f), PillShape)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = waveFeedback!!,
                    style = TendrilsTheme.typography.caption,
                    fontWeight = FontWeight.SemiBold,
                    color = TendrilsPalette.Amber600
                )
            }
        }

        linkedFriends.forEach { friend ->
            TendrilsCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = TendrilsTheme.colors.surfaceElevated,
                borderColor = if (friend.isParent) TendrilsPalette.Indigo400.copy(alpha = 0.4f) else TendrilsTheme.colors.border,
                padding = 12.dp
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(TendrilsTheme.colors.primary.copy(alpha = 0.2f))
                                .border(1.5.dp, TendrilsTheme.colors.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = friend.avatarInitial,
                                style = TendrilsTheme.typography.subtitle1,
                                fontWeight = FontWeight.Bold,
                                color = TendrilsTheme.colors.primary
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = friend.name,
                                    style = TendrilsTheme.typography.body1,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TendrilsTheme.colors.textPrimary
                                )
                                Box(
                                    modifier = Modifier
                                        .background(TendrilsTheme.colors.surface, PillShape)
                                        .padding(horizontal = 6.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = friend.role,
                                        style = TendrilsTheme.typography.caption,
                                        fontSize = 10.sp,
                                        color = TendrilsTheme.colors.primary
                                    )
                                }
                            }
                            Text(
                                text = friend.statusText,
                                style = TendrilsTheme.typography.caption,
                                color = TendrilsTheme.colors.textMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                waveFeedback = "Sent a Sprout Wave 🌱 to ${friend.name}'s companion!"
                                scope.launch {
                                    delay(2500)
                                    waveFeedback = null
                                }
                            },
                            modifier = Modifier.weight(1f).height(36.dp),
                            shape = PillShape,
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = TendrilsTheme.colors.surface,
                                contentColor = TendrilsTheme.colors.primary
                            )
                        ) {
                            Icon(imageVector = Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Wave Sprout 🌱", style = TendrilsTheme.typography.caption, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                waveFeedback = "Shared current study summary with ${friend.name}!"
                                scope.launch {
                                    delay(2500)
                                    waveFeedback = null
                                }
                            },
                            modifier = Modifier.weight(1f).height(36.dp),
                            shape = PillShape,
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = TendrilsTheme.colors.surface,
                                contentColor = TendrilsTheme.colors.textSecondary
                            )
                        ) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share Note", style = TendrilsTheme.typography.caption)
                        }
                    }
                }
            }
        }
    }
}
