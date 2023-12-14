package com.adammcneilly.geminichat

data class ChatViewState(
    val history: List<ChatMessage>,
    val modelIsProcessing: Boolean,
    val prompt: String,
)
