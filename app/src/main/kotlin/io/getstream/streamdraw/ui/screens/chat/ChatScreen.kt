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

package io.getstream.streamdraw.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import io.getstream.chat.android.compose.R
import io.getstream.chat.android.compose.state.messages.MessagesState
import io.getstream.chat.android.compose.state.messages.list.MessageItemState
import io.getstream.chat.android.compose.state.messages.list.MessageListItemState
import io.getstream.chat.android.compose.ui.components.composer.InputField
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.streamdraw.data.GameMessage
import io.getstream.streamdraw.data.toGameMessage
import io.getstream.streamdraw.ui.components.LoadingIndicator
import io.getstream.streamdraw.ui.screens.game.GameViewModel
import io.getstream.streamdraw.ui.theme.hostAccent

@Composable
fun ChatScreen(
    modifier: Modifier,
    lazyListState: LazyListState,
    currentState: MessagesState,
    onScrollToBottom: () -> Unit,
) {
    Box(modifier = modifier) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                ChatInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(6.dp)
                        .align(Alignment.Center),
                    gameViewModel = hiltViewModel()
                )
            }
        ) {
            val (isLoading, _, _, messages) = currentState
            if (isLoading) {
                LoadingIndicator(Modifier.fillMaxSize())
            } else {
                MessageList(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ChatTheme.colors.appBackground)
                        .padding(it),
                    lazyListState = lazyListState,
                    gameViewModel = hiltViewModel(),
                    onScrollToBottom = onScrollToBottom,
                    messages = messages
                )
            }
        }
    }
}

@Composable
private fun MessageList(
    modifier: Modifier,
    gameViewModel: GameViewModel,
    lazyListState: LazyListState,
    onScrollToBottom: () -> Unit,
    messages: List<MessageListItemState>,
) {
    val newMessage = gameViewModel.newSingleMessage.value
    LaunchedEffect(newMessage) {
        lazyListState.animateScrollToItem(0)
    }

    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Bottom,
        reverseLayout = true,
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        itemsIndexed(
            messages,
            key = { _, item ->
                if (item is MessageItemState) item.message.id else item.toString()
            }
        ) { index, item ->
            if (item is MessageItemState) {
                val isHostMessage = item.message.user.name == gameViewModel.host
                val gameMessage = item.message.toGameMessage()
                if (isHostMessage) {
                    HostChatMessage(gameMessage)
                } else {
                    GeneralChatMessage(gameMessage)
                }
            }

            if (index == 0 && lazyListState.isScrollInProgress) {
                onScrollToBottom()
            }
        }
    }
}

@Composable
private fun GeneralChatMessage(message: GameMessage) {
    Text(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
        text = "${message.name}: ${message.message}",
        color = ChatTheme.colors.textHighEmphasis,
        fontSize = 16.sp,
    )
}

@Composable
private fun HostChatMessage(message: GameMessage) {
    Text(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
        text = "${message.name}(host): ${message.message}",
        fontWeight = FontWeight.Bold,
        color = hostAccent,
        fontSize = 16.sp,
    )
}

@Composable
private fun ChatInput(
    modifier: Modifier = Modifier,
    gameViewModel: GameViewModel,
) {
    val onInputValue = remember { mutableStateOf("") }
    InputField(
        value = onInputValue.value,
        modifier = modifier,
        maxLines = 1,
        innerPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        onValueChange = { onInputValue.value = it },
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    innerTextField()

                    if (onInputValue.value.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.stream_compose_message_label),
                            color = ChatTheme.colors.textLowEmphasis
                        )
                    }
                }

                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple()
                        ) {
                            val message = onInputValue.value
                            if (message.isNotEmpty()) {
                                gameViewModel.sendChatMessage(message)
                                onInputValue.value = ""
                            }
                        },
                    painter = painterResource(id = R.drawable.stream_compose_ic_send),
                    tint = ChatTheme.colors.primaryAccent,
                    contentDescription = null
                )
            }
        }
    )
}
