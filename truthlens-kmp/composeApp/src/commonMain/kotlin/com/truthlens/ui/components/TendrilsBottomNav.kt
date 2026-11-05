package com.truthlens.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import com.truthlens.ui.navigation.ActivityTab
import com.truthlens.ui.navigation.DiscoverTab
import com.truthlens.ui.navigation.HomeTab
import com.truthlens.ui.navigation.ProfileTab
import com.truthlens.ui.theme.PillShape
import com.truthlens.ui.theme.TendrilsTheme

/**
 * Polished bottom navigation bar for Tendrils.
 * Features 4 primary destinations with organic pill indicator, comfortable touch targets,
 * and adaptive light/dark theming.
 */
@Composable
fun TendrilsBottomNav(
    modifier: Modifier = Modifier
) {
    val tabNavigator = LocalTabNavigator.current
    val currentTab = tabNavigator.current

    Surface(
        color = TendrilsTheme.colors.surface,
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TendrilsTheme.colors.border),
        elevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TendrilsNavItem(
                tab = HomeTab,
                icon = Icons.Default.Home,
                label = "Home",
                isSelected = currentTab == HomeTab,
                onClick = { tabNavigator.current = HomeTab }
            )
            TendrilsNavItem(
                tab = DiscoverTab,
                icon = Icons.Default.Search,
                label = "Discover",
                isSelected = currentTab == DiscoverTab,
                onClick = { tabNavigator.current = DiscoverTab }
            )
            TendrilsNavItem(
                tab = ActivityTab,
                icon = Icons.Default.DateRange,
                label = "Activity",
                isSelected = currentTab == ActivityTab,
                onClick = { tabNavigator.current = ActivityTab }
            )
            TendrilsNavItem(
                tab = ProfileTab,
                icon = Icons.Default.Person,
                label = "Profile",
                isSelected = currentTab == ProfileTab,
                onClick = { tabNavigator.current = ProfileTab }
            )
        }
    }
}

@Composable
private fun TendrilsNavItem(
    tab: Tab,
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val activeBackgroundColor = if (TendrilsTheme.colors.isDark) {
        TendrilsTheme.colors.primary.copy(alpha = 0.18f)
    } else {
        TendrilsTheme.colors.primary.copy(alpha = 0.12f)
    }

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) activeBackgroundColor else Color.Transparent,
        animationSpec = tween(durationMillis = 200)
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) TendrilsTheme.colors.primary else TendrilsTheme.colors.textMuted,
        animationSpec = tween(durationMillis = 200)
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .clip(PillShape)
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            if (isSelected) {
                Text(
                    text = label,
                    color = contentColor,
                    fontSize = 13.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun Surface(
    color: Color,
    modifier: Modifier = Modifier,
    border: androidx.compose.foundation.BorderStroke? = null,
    elevation: androidx.compose.ui.unit.Dp = 0.dp,
    content: @Composable () -> Unit
) {
    androidx.compose.material.Surface(
        color = color,
        modifier = modifier,
        border = border,
        elevation = elevation,
        content = content
    )
}
