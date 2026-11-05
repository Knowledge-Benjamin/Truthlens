package com.truthlens.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
                    OnboardingScreen(onComplete = {
                        onboardingCompletedState.value = true
                    })
                } else {
                    TabNavigator(HomeTab) {
                        Scaffold(
                            backgroundColor = TendrilsTheme.colors.background,
                            bottomBar = {
                                TendrilsBottomNav()
                            }
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
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