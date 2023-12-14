package com.adammcneilly.geminichat

import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null) {
            // TODO: Use this URI to get a bitmap and then send it to the ViewModel.
            Log.d("PhotoPicker", "Selected URI: $uri")
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            viewModel.sendImage(bitmap)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    val state = viewModel.state.collectAsState()

    ChatContent(
        state = state.value,
        onPromptChanged = viewModel::promptChanged,
        onSendClicked = viewModel::sendMessage,
        onImageClicked = {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        modifier = modifier,
    )
}
