package com.adammcneilly.geminichat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(
    generativeModel: GenerativeModel,
) : ViewModel() {

    private val chat = generativeModel.startChat()

    private val _state = MutableStateFlow(
        ChatViewState(
            history = emptyList(),
            modelIsProcessing = false,
            prompt = "",
        )
    )

    val state = _state.asStateFlow()

    fun promptChanged(newPrompt: String) {
        _state.update { currentState ->
            currentState.copy(
                prompt = newPrompt,
            )
        }
    }

    fun sendMessage() {
        sendMessage(state.value.prompt)
    }

    private fun sendMessage(message: String) {
        viewModelScope.launch {
            // Set processing and add user's messages
            _state.update { currentState ->
                currentState.copy(
                    history = currentState.history + ChatMessage(
                        sender = ChatMessage.Sender.USER,
                        message = message,
                    ),
                    modelIsProcessing = true,
                    prompt = "",
                )
            }

            val response = chat.sendMessage(message)
            // Clear processing and add model's message.
            _state.update { currentState ->
                currentState.copy(
                    history = currentState.history + ChatMessage(
                        sender = ChatMessage.Sender.MODEL,
                        message = response.text.orEmpty(),
                    ),
                    modelIsProcessing = false,
                )
            }
        }
    }
}
