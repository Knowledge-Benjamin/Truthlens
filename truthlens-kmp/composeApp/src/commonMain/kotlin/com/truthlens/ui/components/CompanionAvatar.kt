package com.truthlens.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.truthlens.ui.model.CompanionState
import com.truthlens.ui.theme.PillShape
import com.truthlens.ui.theme.TendrilsPalette
import com.truthlens.ui.theme.TendrilsTheme

/**
 * Main AI Companion Visual & State Indicator
 * Section 5: "This is the most important screen. It should be the application's visual centre."
 * Provides 8 distinct visual treatments:
 * Idle, Listening, Thinking, Speaking, Performing a task, Waiting for permission, Completed, Error.
 * Does NOT continuously animate when idle.
 */
@Composable
fun CompanionAvatar(
    state: CompanionState,
    onStateSelected: ((CompanionState) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showStateMenu by remember { mutableStateOf(false) }

    // Subtle scale animation only active during dynamic states (Listening, Thinking, Speaking, Task)
    val isDynamicState = state == CompanionState.LISTENING ||
            state == CompanionState.THINKING ||
            state == CompanionState.SPEAKING ||
            state == CompanionState.PERFORMING_TASK

    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by if (isDynamicState) {
        infiniteTransition.animateFloat(
            initialValue = 1.0f,
            targetValue = 1.06f,
            animationSpec = infiniteRepeatable(
                animation = tween(1100, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    } else {
        remember { mutableStateOf(1.0f) }
    }

    val animatedPrimaryColor by animateColorAsState(
        targetValue = state.primaryColor,
        animationSpec = tween(400)
    )

    val animatedAuraColor by animateColorAsState(
        targetValue = state.auraColor,
        animationSpec = tween(400)
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- MULTI-LAYER ORGANIC ORB ---
        Box(
            modifier = Modifier
                .size(136.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(animatedAuraColor)
                .border(2.dp, animatedPrimaryColor.copy(alpha = 0.5f), CircleShape)
                .clickable { showStateMenu = true },
            contentAlignment = Alignment.Center
        ) {
            // Inner Core Circle
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(TendrilsTheme.colors.surface)
                    .border(1.5.dp, animatedPrimaryColor.copy(alpha = 0.8f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getIconForState(state),
                    contentDescription = state.label,
                    tint = animatedPrimaryColor,
                    modifier = Modifier.size(44.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- STATE BADGE (Clickable to inspect other states) ---
        Box(
            modifier = Modifier
                .clip(PillShape)
                .background(TendrilsTheme.colors.surface)
                .border(1.dp, animatedPrimaryColor.copy(alpha = 0.35f), PillShape)
                .clickable { showStateMenu = true }
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
                        .background(animatedPrimaryColor)
                )
                Text(
                    text = "Tendrils • ${state.label}",
                    style = TendrilsTheme.typography.caption,
                    fontWeight = FontWeight.SemiBold,
                    color = TendrilsTheme.colors.textPrimary
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Change state preview",
                    tint = TendrilsTheme.colors.textMuted,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Dropdown Menu to test all 8 states easily
            DropdownMenu(
                expanded = showStateMenu,
                onDismissRequest = { showStateMenu = false }
            ) {
                CompanionState.values().forEach { st ->
                    DropdownMenuItem(
                        onClick = {
                            onStateSelected?.invoke(st)
                            showStateMenu = false
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(st.primaryColor)
                            )
                            Text(
                                text = st.label,
                                style = TendrilsTheme.typography.body2,
                                fontWeight = if (st == state) FontWeight.Bold else FontWeight.Normal,
                                color = if (st == state) TendrilsTheme.colors.primary else TendrilsTheme.colors.textPrimary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // State Description Text
        Text(
            text = state.description,
            style = TendrilsTheme.typography.caption,
            color = TendrilsTheme.colors.textSecondary,
            textAlign = TextAlign.Center
        )
    }
}

private fun getIconForState(state: CompanionState): ImageVector {
    return when (state) {
        CompanionState.IDLE -> Icons.Default.Favorite
        CompanionState.LISTENING -> Icons.Default.Share // mic wave representation
        CompanionState.THINKING -> Icons.Default.Star
        CompanionState.SPEAKING -> Icons.Default.Notifications
        CompanionState.PERFORMING_TASK -> Icons.Default.Refresh
        CompanionState.WAITING_FOR_PERMISSION -> Icons.Default.Lock
        CompanionState.COMPLETED -> Icons.Default.Check
        CompanionState.ERROR -> Icons.Default.Warning
    }
}
