package com.adammcneilly.geminichat

import android.graphics.Bitmap

data class ChatMessage(
    val sender: Sender,
    val message: String,
    val images: List<Bitmap> = emptyList(),
) {
    enum class Sender {
        USER,
        MODEL,
    }
}