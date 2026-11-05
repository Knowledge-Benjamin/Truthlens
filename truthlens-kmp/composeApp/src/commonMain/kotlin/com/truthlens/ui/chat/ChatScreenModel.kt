package com.truthlens.ui.chat

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.truthlens.llm.ChatFormatter
import com.truthlens.llm.LlamatikEngine
import com.truthlens.llm.Message
import com.truthlens.llm.ModelDownloader
import com.truthlens.llm.Role
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatState(
    val messages: List<Message> = emptyList(),
    val isDownloadingModel: Boolean = false,
    val isModelLoaded: Boolean = false,
    val downloadProgress: Float = 0f,
    val isGenerating: Boolean = false
)

class ChatScreenModel : ScreenModel {
    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    private val downloader = ModelDownloader()
    
    // As requested: default model LiquidAI/LFM2.5-230M-GGUF
    private val modelUrl = "https://huggingface.co/LiquidAI/LFM2.5-230M-GGUF/resolve/main/LFM2.5-230M-GGUF.gguf"
    private val modelFileName = "LFM2.5-230M-GGUF.gguf"

    fun initialize() {
        if (_state.value.isModelLoaded || _state.value.isDownloadingModel) return

        _state.update { it.copy(isDownloadingModel = true) }
        
        screenModelScope.launch(Dispatchers.IO) {
            try {
                val path = downloader.downloadModel(modelUrl, modelFileName) { progress ->
                    _state.update { it.copy(downloadProgress = progress) }
                }
                LlamatikEngine.initModel(path)
                _state.update { 
                    it.copy(
                        isDownloadingModel = false, 
                        isModelLoaded = true,
                        messages = listOf(Message(Role.SYSTEM, "You are a helpful offline assistant."))
                    ) 
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update { it.copy(isDownloadingModel = false) }
            }
        }
    }

    fun sendMessage(content: String) {
        if (content.isBlank() || _state.value.isGenerating) return

        val userMessage = Message(Role.USER, content)
        _state.update { it.copy(messages = it.messages + userMessage, isGenerating = true) }

        screenModelScope.launch(Dispatchers.IO) {
            val formattedPrompt = ChatFormatter.format(_state.value.messages)
            
            // Add an empty assistant message to stream into
            _state.update { it.copy(messages = it.messages + Message(Role.ASSISTANT, "")) }

            try {
                LlamatikEngine.generateStreaming(formattedPrompt).collect { chunk ->
                    _state.update { currentState ->
                        val messages = currentState.messages.toMutableList()
                        val lastIndex = messages.lastIndex
                        val lastMessage = messages[lastIndex]
                        messages[lastIndex] = lastMessage.copy(content = lastMessage.content + chunk)
                        currentState.copy(messages = messages)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _state.update { it.copy(isGenerating = false) }
            }
        }
    }
}
