package com.adammcneilly.geminichat

data class ChatMessage(
    val sender: Sender,
    val message: String,
) {
    enum class Sender {
        USER,
        MODEL,
    }
}