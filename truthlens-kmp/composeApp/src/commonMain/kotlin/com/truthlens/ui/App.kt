package com.truthlens.ui

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
import com.truthlens.ui.navigation.HomeTab
import com.truthlens.ui.theme.LocalThemeMode
import com.truthlens.ui.theme.TendrilsTheme
import com.truthlens.ui.theme.ThemeMode

/**
 * Tendrils Application Shell
 * Root container hosting the adaptive theme and persistent 4-tab bottom navigation.
 */
@Composable
fun App() {
    val themeModeState = remember { mutableStateOf(ThemeMode.SYSTEM) }

    CompositionLocalProvider(LocalThemeMode provides themeModeState) {
        TendrilsTheme(themeMode = themeModeState.value) {
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