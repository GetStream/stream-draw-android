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

package io.getstream.streamdraw.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.call.await
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.ConnectionData
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.client.utils.Result
import io.getstream.streamdraw.data.GameConnectionState
import io.getstream.streamdraw.extensions.groupName
import io.getstream.streamdraw.extensions.image
import io.getstream.streamdraw.extensions.toChannelId
import io.getstream.streamdraw.persistence.AppPreferences
import io.getstream.streamdraw.ui.screens.game.GameStatus
import io.getstream.streamdraw.utils.CHANNEL_MESSAGING
import io.getstream.streamdraw.utils.KEY_GAME_STATUS
import io.getstream.streamdraw.utils.KEY_HOST_NAME
import io.getstream.streamdraw.utils.KEY_NAME
import io.getstream.streamdraw.utils.generateGroupId
import io.getstream.streamdraw.utils.generateUserId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val prefs: AppPreferences,
    private val chatClient: ChatClient
) : ViewModel() {

    private val userId: String
        get() = prefs.userId ?: generateUserId().also { prefs.userId = it }

    private val _gameConnectionState =
        MutableStateFlow<GameConnectionState>(GameConnectionState.None)
    val gameConnectionState: StateFlow<GameConnectionState> = _gameConnectionState

    val connectedChannel: Flow<Channel?> =
        _gameConnectionState.filterIsInstance<GameConnectionState.Success>()
            .mapNotNull { it.channel }

    /** Connect a user to the Stream server. */
    private suspend fun connectUser(displayName: String): Result<ConnectionData> {
        val currentUser = chatClient.getCurrentUser()
        if (currentUser != null) {
            chatClient.disconnect()
        }
        val user = User(
            id = userId,
            name = displayName,
            image = displayName.image
        )
        val token = chatClient.devToken(userId)
        return chatClient
            .connectUser(user, token)
            .await()
    }

    /** create a new channel. */
    private suspend fun createChannel(
        groupId: String,
        displayName: String,
    ): Result<Channel> {
        return chatClient.createChannel(
            channelType = CHANNEL_MESSAGING,
            channelId = groupId,
            memberIds = listOf(userId),
            extraData = mapOf(
                KEY_NAME to displayName.groupName,
                KEY_HOST_NAME to displayName,
                KEY_GAME_STATUS to GameStatus.START.status
            )
        ).await()
    }

    /** create a new game group with the [displayName] as a host. */
    fun createGameGroup(displayName: String) {
        viewModelScope.launch {
            val connection = connectUser(displayName)
            if (connection.isSuccess) {
                _gameConnectionState.emit(GameConnectionState.Loading)
                val groupId = generateGroupId()
                val result = createChannel(groupId, displayName)
                if (result.isSuccess) {
                    _gameConnectionState.emit(GameConnectionState.Success(result.data()))
                } else {
                    _gameConnectionState.emit(GameConnectionState.Failure(result.error()))
                }
            }
        }
    }

    /** join the game group with the [displayName] and specific [groupId]. */
    fun joinGameGroup(displayName: String, groupId: String) {
        viewModelScope.launch {
            val connection = connectUser(displayName)
            if (connection.isSuccess) {
                _gameConnectionState.emit(GameConnectionState.Loading)
                val channelClient = chatClient.channel(groupId.toChannelId())
                val result = channelClient.addMembers(listOf(userId)).await()
                if (result.isSuccess) {
                    _gameConnectionState.emit(GameConnectionState.Success(result.data()))
                } else {
                    _gameConnectionState.emit(GameConnectionState.Failure(result.error()))
                }
            }
        }
    }
}
