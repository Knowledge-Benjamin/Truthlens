package com.truthlens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.truthlens.ui.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.truthlens.voice.TendrilsTts.init(this)
        com.truthlens.voice.TendrilsStt.init(this)
        setContent {
            App()
        }
    }
}