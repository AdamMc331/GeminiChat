package com.adammcneilly.geminichat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier

@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier,
) {
    val state = viewModel.state.collectAsState()

    ChatContent(
        state = state.value,
        onPromptChanged = viewModel::promptChanged,
        onSendClicked = viewModel::sendMessage,
        modifier = modifier,
    )
}
