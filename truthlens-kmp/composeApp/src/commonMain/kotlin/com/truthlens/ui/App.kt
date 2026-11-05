package com.truthlens.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.truthlens.ui.components.TendrilsBottomNav
import com.truthlens.ui.model.LocalOnboardingController
import com.truthlens.ui.navigation.HomeTab
import com.truthlens.ui.screens.onboarding.OnboardingScreen
import com.truthlens.ui.theme.LocalThemeMode
import com.truthlens.ui.theme.TendrilsTheme
import com.truthlens.ui.theme.ThemeMode

/**
 * Tendrils Application Shell
 * Root container hosting adaptive theme, onboarding routing, and persistent 4-tab bottom navigation.
 * Responsive for phone, tablet, and desktop display factors.
 */
@Composable
fun App() {
    val themeModeState = remember { mutableStateOf(ThemeMode.SYSTEM) }
    val onboardingCompletedState = remember { mutableStateOf(false) }

    CompositionLocalProvider(
        LocalThemeMode provides themeModeState,
        LocalOnboardingController provides onboardingCompletedState
    ) {
        TendrilsTheme(themeMode = themeModeState.value) {
            Crossfade(targetState = onboardingCompletedState.value) { isCompleted ->
                if (!isCompleted) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(TendrilsTheme.colors.background),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .widthIn(max = 680.dp)
                        ) {
                            OnboardingScreen(onComplete = {
                                onboardingCompletedState.value = true
                            })
                        }
                    }
                } else {
                    TabNavigator(HomeTab) {
                        Scaffold(
                            backgroundColor = TendrilsTheme.colors.background,
                            bottomBar = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(TendrilsTheme.colors.surface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .widthIn(max = 840.dp)
                                    ) {
                                        TendrilsBottomNav()
                                    }
                                }
                            }
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .widthIn(max = 840.dp)
                                ) {
                                    CurrentTab()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}