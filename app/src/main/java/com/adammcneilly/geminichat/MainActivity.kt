package com.adammcneilly.geminichat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.google.ai.client.generativeai.GenerativeModel
import com.adammcneilly.geminichat.ui.theme.GeminiChatTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GeminiChatTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val textModel = GenerativeModel(
                        modelName = "gemini-pro",
                        apiKey = BuildConfig.apiKey
                    )

                    val imageModel = GenerativeModel(
                        modelName = "gemini-pro-vision",
                        apiKey = BuildConfig.apiKey
                    )

                    val viewModel = ChatViewModel(textModel, imageModel)

                    ChatScreen(
                        viewModel = viewModel,
                        modifier = Modifier
                            .fillMaxSize(),
                    )
                }
            }
        }
    }
}
