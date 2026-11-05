package com.truthlens.ui.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf

val LocalOnboardingController = compositionLocalOf<MutableState<Boolean>> {
    mutableStateOf(false)
}
