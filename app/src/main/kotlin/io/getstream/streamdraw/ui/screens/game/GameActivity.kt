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

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.viewmodel.messages.MessageListViewModel
import io.getstream.chat.android.compose.viewmodel.messages.MessagesViewModelFactory
import io.getstream.sketchbook.rememberSketchbookController
import javax.inject.Inject

@AndroidEntryPoint
class GameActivity : ComponentActivity() {

    @set:Inject
    internal lateinit var gameViewModelFactory: GameViewModel.GameAssistedFactory
    private val cid by lazy { intent.getStringExtra(EXTRA_CID)!! }
    private val gameViewModel: GameViewModel by viewModels {
        GameViewModel.provideFactory(gameViewModelFactory, cid)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ChatTheme {
                // create a messages view model.
                val listViewModel = MessagesViewModelFactory(
                    context = LocalContext.current,
                    channelId = cid,
                    enforceUniqueReactions = false
                ).let {
                    viewModel(MessageListViewModel::class.java, factory = it)
                }

                GameScreen(
                    gameViewModel = gameViewModel,
                    listViewModel = listViewModel,
                    exitGame = { finish() },
                    sketchbookController = rememberSketchbookController()
                )
            }
        }
    }

    companion object {
        private const val EXTRA_CID: String = "EXTRA_CID"

        fun getIntent(context: Context, cid: String): Intent {
            return Intent(context, GameActivity::class.java).apply {
                putExtra(EXTRA_CID, cid)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }
    }
}
