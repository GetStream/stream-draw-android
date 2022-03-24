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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import io.getstream.chat.android.compose.viewmodel.messages.MessageListViewModel
import io.getstream.sketchbook.SketchbookController

@Composable
fun GameScreen(
    gameViewModel: GameViewModel,
    listViewModel: MessageListViewModel,
    sketchbookController: SketchbookController,
    exitGame: () -> Unit
) {
    LaunchedEffect(Unit) {
        sketchbookController.setPaintStrokeWidth(23f)
        sketchbookController.setPaintColor(Color.Black)
    }

    GameDrawing(
        isHost = gameViewModel.isHost.value,
        gameViewModel = gameViewModel,
        listViewModel = listViewModel,
        sketchbookController = sketchbookController,
        exitGame = exitGame,
    )
}
