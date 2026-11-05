package com.truthlens.ui.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.model.rememberScreenModel
import com.truthlens.llm.Role

class ChatScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { ChatScreenModel() }
        val state by screenModel.state.collectAsState()
        var inputText by remember { mutableStateOf("") }

        LaunchedEffect(Unit) {
            screenModel.initialize()
        }

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            if (state.isDownloadingModel) {
                LinearProgressIndicator(
                    progress = state.downloadProgress,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )
                Text("Downloading Model... ${(state.downloadProgress * 100).toInt()}%")
            }

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.messages) { message ->
                    val alignment = if (message.role == Role.USER) Alignment.CenterEnd else Alignment.CenterStart
                    val color = if (message.role == Role.USER) MaterialTheme.colors.primary else MaterialTheme.colors.secondary
                    
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), contentAlignment = alignment) {
                        Surface(
                            color = color,
                            shape = MaterialTheme.shapes.medium,
                            elevation = 1.dp
                        ) {
                            Text(
                                text = message.content,
                                modifier = Modifier.padding(12.dp),
                                color = MaterialTheme.colors.onPrimary
                            )
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    enabled = state.isModelLoaded && !state.isGenerating,
                    placeholder = { Text("Ask something...") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        screenModel.sendMessage(inputText)
                        inputText = ""
                    },
                    enabled = inputText.isNotBlank() && state.isModelLoaded && !state.isGenerating,
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Text("Send")
                }
            }
        }
    }
}
