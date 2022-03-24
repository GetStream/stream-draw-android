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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.ui.util.rememberMessageListState
import io.getstream.chat.android.compose.viewmodel.messages.MessageListViewModel
import io.getstream.streamdraw.R
import io.getstream.streamdraw.ui.screens.chat.ChatScreen

@Composable
fun GameChatDialog(
    expanded: MutableState<Boolean>,
    listViewModel: MessageListViewModel
) {
    if (expanded.value) {
        Dialog(onDismissRequest = { expanded.value = false }) {
            GameChatDialogContent(
                expanded,
                listViewModel
            )
        }
    }
}

@Composable
private fun GameChatDialogContent(
    expanded: MutableState<Boolean>,
    listViewModel: MessageListViewModel,
    lazyListState: LazyListState = rememberMessageListState(parentMessageId = listViewModel.currentMessagesState.parentMessageId)
) {
    Column(
        modifier = Modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(8.dp))
            .background(ChatTheme.colors.appBackground)
    ) {

        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp),
            text = stringResource(id = R.string.group_chat),
            color = ChatTheme.colors.textHighEmphasis,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp
        )

        ChatScreen(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clickable { expanded.value = false },
            lazyListState = lazyListState,
            currentState = listViewModel.currentMessagesState,
            onScrollToBottom = { listViewModel.clearNewMessageState() }
        )
    }
}
