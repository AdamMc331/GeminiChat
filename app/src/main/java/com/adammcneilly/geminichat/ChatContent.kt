package com.adammcneilly.geminichat

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adammcneilly.geminichat.ui.theme.GeminiChatTheme

@Composable
fun ChatContent(
    state: ChatViewState,
    onPromptChanged: (String) -> Unit,
    onSendClicked: () -> Unit,
    onImageClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F),
        ) {
            items(state.history) { message ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    val alignment = when (message.sender) {
                        ChatMessage.Sender.USER -> Alignment.CenterEnd
                        ChatMessage.Sender.MODEL -> Alignment.CenterStart
                    }

                    Column(
                        modifier = Modifier
                            .align(alignment),
                    ) {

                        message.images.forEach { bitmap ->
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = null,
                            )
                        }

                        Text(
                            text = message.message,
                        )
                    }
                }
            }

            if (state.modelIsProcessing) {
                item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .wrapContentSize(),
                    )
                }
            }
        }

        OutlinedTextField(
            value = state.prompt,
            onValueChange = onPromptChanged,
            shape = CircleShape,
            placeholder = {
                Text(
                    text = "What can I help you with?",
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    modifier = Modifier
                        .clickable(
                            onClick = onSendClicked,
                        ),
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Photo,
                    contentDescription = "Upload Image",
                    modifier = Modifier
                        .clickable(
                            onClick = onImageClicked,
                        )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
    }
}

@Preview(
    name = "Day Mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Night Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun EmptyChatContentPreview() {
    val emptyState = ChatViewState(
        history = emptyList(),
        modelIsProcessing = false,
        prompt = "",
    )

    GeminiChatTheme {
        Surface {
            ChatContent(
                state = emptyState,
                onPromptChanged = {},
                onSendClicked = {},
                onImageClicked = {},
                modifier = Modifier
                    .fillMaxSize(),
            )
        }
    }
}

@Preview(
    name = "Day Mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Night Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun FilledChatContentPreview() {
    val filledState = ChatViewState(
        history = listOf(
            ChatMessage(
                sender = ChatMessage.Sender.USER,
                message = "Hello!",
            ),
            ChatMessage(
                sender = ChatMessage.Sender.MODEL,
                message = "Hi! What can I do for you?",
            ),
        ),
        modelIsProcessing = false,
        prompt = "",
    )

    GeminiChatTheme {
        Surface {
            ChatContent(
                state = filledState,
                onPromptChanged = {},
                onSendClicked = {},
                onImageClicked = {},
                modifier = Modifier
                    .fillMaxSize(),
            )
        }
    }
}