/*
 * Copyright 2022 Stream.IO, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.getstream.streamdraw.ui.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.ui.util.rememberMessageListState
import io.getstream.chat.android.compose.viewmodel.messages.MessageListViewModel
import io.getstream.sketchbook.ColorPickerPaletteIcon
import io.getstream.sketchbook.PaintColorPalette
import io.getstream.sketchbook.SketchbookController
import io.getstream.streamdraw.R
import io.getstream.streamdraw.extensions.toBitmap
import io.getstream.streamdraw.ui.components.SketchbookControlMenu
import io.getstream.streamdraw.ui.components.SketchbookScreen
import io.getstream.streamdraw.ui.components.TitleText
import io.getstream.streamdraw.ui.screens.chat.ChatScreen

@Composable
fun GameDrawing(
    isHost: Boolean,
    gameViewModel: GameViewModel,
    listViewModel: MessageListViewModel,
    sketchbookController: SketchbookController,
    exitGame: () -> Unit
) {
    val gameStatus = gameViewModel.gameStatus.value
    val newDrawingImage = gameViewModel.newDrawingImage.value

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChatTheme.colors.appBackground)
    ) {
        if (isHost && gameStatus == GameStatus.START) {
            GameDrawingHost(viewModel = gameViewModel, sketchbookController = sketchbookController)
        } else if (gameStatus != GameStatus.DELETED) {
            if (isHost) {
                GameRestartButton(gameViewModel = gameViewModel)
            }
            GameDrawingGuest(
                drawingImage = newDrawingImage,
                gameViewModel = gameViewModel,
                listViewModel = listViewModel
            )
        } else {
            GameDeletedScreen(exitGame = exitGame)
        }
    }

    GameExitDialog(gameViewModel = gameViewModel)
}

@Composable
private fun GameDrawingHost(
    viewModel: GameViewModel,
    sketchbookController: SketchbookController
) {
    val randomWords by viewModel.randomWords.collectAsState()
    val selectedWord by viewModel.selectedWord.collectAsState()
    if (randomWords != null && selectedWord == null) {
        WordSelectionDialog(
            words = randomWords!!,
            wordSelected = { selection -> viewModel.setSelectedWord(selection) }
        )
    } else {
        Column {
            if (selectedWord != null) {
                TitleText(
                    text = selectedWord!!,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                )
            }

            SketchbookScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f, fill = false),
                controller = sketchbookController,
                onEventListener = { bitmap ->
                    viewModel.broadcastToChannel(bitmap)
                }
            )

            PaintColorPalette(
                modifier = Modifier.padding(6.dp),
                controller = sketchbookController,
                initialSelectedIndex = 3,
                onColorSelected = { _, _ -> sketchbookController.setPaintShader(null) },
                header = {
                    ColorPickerPaletteIcon(
                        modifier = Modifier
                            .size(60.dp)
                            .padding(6.dp),
                        controller = sketchbookController,
                        bitmap = ImageBitmap.imageResource(R.drawable.palette)
                    )
                }
            )

            SketchbookControlMenu(controller = sketchbookController)

            NewGameMessage(viewModel = hiltViewModel())
        }
    }
}

@Composable
private fun GameDrawingGuest(
    drawingImage: String?,
    gameViewModel: GameViewModel,
    listViewModel: MessageListViewModel,
    lazyListState: LazyListState = rememberMessageListState(parentMessageId = listViewModel.currentMessagesState.parentMessageId)
) {
    Column(modifier = Modifier.fillMaxSize()) {

        DrawingScreen(
            modifier = Modifier
                .fillMaxSize()
                .weight(0.9f),
            bitmap = drawingImage?.toBitmap()
        )

        PlayerScreen(gameViewModel.members.value)

        ChatScreen(
            modifier = Modifier
                .fillMaxSize()
                .weight(0.85f),
            lazyListState = lazyListState,
            currentState = listViewModel.currentMessagesState,
            onScrollToBottom = { listViewModel.clearNewMessageState() }
        )

        GameWinnerScreen(gameViewModel = gameViewModel)
    }
}
