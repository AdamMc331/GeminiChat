package com.adammcneilly.geminichat

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * @param[textModel] This [GenerativeModel] is used to create a multi-turn chat that allows us to
 * ask specific questions, and it maintains context of the chat history.
 * @param[imageModel] Currently the gemini-pro-vision model does not support multi turn chat, so we
 * supply a second [GenerativeModel] that is used to run image based requests.
 */
class ChatViewModel(
    textModel: GenerativeModel,
    private val imageModel: GenerativeModel,
) : ViewModel() {

    private val chat = textModel.startChat()

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

    fun sendImage(
        image: Bitmap
    ) {
        viewModelScope.launch {
            val prompt = "Describe this image"

            val content = content(
                role = "user",
            ) {
                image(image)
                text(prompt)
            }

            _state.update { currentState ->
                currentState.copy(
                    history = currentState.history + ChatMessage(
                        sender = ChatMessage.Sender.USER,
                        message = prompt,
                        images = listOf(image),
                    ),
                    modelIsProcessing = true,
                )
            }

            val response = imageModel.generateContent(content)
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

            var chatMessage = ChatMessage(
                sender = ChatMessage.Sender.MODEL,
                message = "",
            )

            Log.d("ChatViewModel", "Starting message stream!")
            chat.sendMessageStream(message).onCompletion {
                _state.update { currentState ->
                    currentState.copy(
                        modelIsProcessing = false,
                    )
                }
            }.collect { response ->
                Log.d("ChatViewModel", "Message stream chunk: ${response.text}")
                chatMessage = chatMessage.copy(
                    message = chatMessage.message + response.text.orEmpty(),
                )

                _state.update { currentState ->
                    val historyWithoutLastModelMessage =  currentState.history
                        .dropLastWhile { it.sender == ChatMessage.Sender.MODEL }
                    val newHistory = historyWithoutLastModelMessage + chatMessage

                    currentState.copy(
                        history = newHistory,
                    )
                }
            }
//            val response = chat.sendMessage(message)
//            // Clear processing and add model's message.
//            _state.update { currentState ->
//                currentState.copy(
//                    history = currentState.history + ChatMessage(
//                        sender = ChatMessage.Sender.MODEL,
//                        message = response.text.orEmpty(),
//                    ),
//                    modelIsProcessing = false,
//                )
//            }
        }
    }
}
