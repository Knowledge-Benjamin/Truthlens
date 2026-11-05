package com.truthlens.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.truthlens.ui.components.Footer
import com.truthlens.ui.components.GlassCard
import com.truthlens.ui.components.Navbar
import com.truthlens.ui.theme.Brand500
import com.truthlens.ui.theme.Brand600
import com.truthlens.ui.theme.Slate400
import kotlinx.coroutines.delay

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var isVisible by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            delay(100)
            isVisible = true
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            Navbar(
                onNavigateToHome = { },
                onNavigateToDetect = { /* navigator.push(DetectorScreen()) */ },
                onNavigateToLearn = { /* navigator.push(LearnScreen()) */ },
                onNavigateToQuiz = { /* navigator.push(QuizScreen()) */ }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(64.dp))

                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                        initialOffsetY = { 30 },
                        animationSpec = tween(600)
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Detect Deepfakes with AI",
                            color = Color.White,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Protect yourself from manipulated media. Upload images, videos, or audio to check for AI generation.",
                            color = Slate400,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )

                        Spacer(modifier = Modifier.height(48.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { navigator.push(com.truthlens.ui.chat.ChatScreen()) },
                                colors = ButtonDefaults.buttonColors(backgroundColor = Brand600),
                                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
                            ) {
                                Text("Offline LLM", color = Color.White, fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Button(
                                onClick = { /* navigator.push(DetectorScreen()) */ },
                                colors = ButtonDefaults.buttonColors(backgroundColor = Brand500),
                                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
                            ) {
                                Text("Scan Media", color = Color.White, fontSize = 16.sp)
                            }
                            Button(
                                onClick = { /* navigator.push(LearnScreen()) */ },
                                colors = ButtonDefaults.buttonColors(backgroundColor = Brand600),
                                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
                            ) {
                                Text("Learn More", color = Color.White, fontSize = 16.sp)
                            }
                            Button(
                                onClick = { navigator.push(com.truthlens.ui.chat.ChatScreen()) },
                                colors = ButtonDefaults.buttonColors(backgroundColor = Brand600),
                                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
                            ) {
                                Text("Offline LLM", color = Color.White, fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(64.dp))

                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(animationSpec = tween(800, delayMillis = 200)) + slideInVertically(
                        initialOffsetY = { 30 },
                        animationSpec = tween(800, delayMillis = 200)
                    )
                ) {
                    GlassCard(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Column {
                            Text("How it works", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("1. Upload media", color = Slate400)
                            Text("2. AI models analyze for artifacts", color = Slate400)
                            Text("3. Get a confidence score", color = Slate400)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                Footer()
            }
        }
    }
}