package com.truthlens.ui

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.truthlens.ui.screens.HomeScreen
import com.truthlens.ui.theme.TruthLensTheme

@Composable
fun App() {
    TruthLensTheme {
        Navigator(HomeScreen()) { navigator ->
            SlideTransition(navigator)
        }
    }
}