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

import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.call.await
import io.getstream.chat.android.client.channel.subscribeFor
import io.getstream.chat.android.client.events.ChannelDeletedEvent
import io.getstream.chat.android.client.events.ChannelUpdatedByUserEvent
import io.getstream.chat.android.client.events.NewMessageEvent
import io.getstream.chat.android.client.events.UserStartWatchingEvent
import io.getstream.chat.android.client.events.UserStopWatchingEvent
import io.getstream.chat.android.client.models.Member
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.client.utils.observable.Disposable
import io.getstream.chat.android.client.utils.onSuccessSuspend
import io.getstream.streamdraw.data.GameMessage
import io.getstream.streamdraw.extensions.gameStatus
import io.getstream.streamdraw.extensions.groupId
import io.getstream.streamdraw.extensions.groupName
import io.getstream.streamdraw.extensions.hostName
import io.getstream.streamdraw.extensions.selectedWord
import io.getstream.streamdraw.extensions.toBase64String
import io.getstream.streamdraw.extensions.winner
import io.getstream.streamdraw.network.RandomWordsFetcher
import io.getstream.streamdraw.utils.KEY_GAME_STATUS
import io.getstream.streamdraw.utils.KEY_HOST_NAME
import io.getstream.streamdraw.utils.KEY_NAME
import io.getstream.streamdraw.utils.KEY_SELECTED_WORD
import io.getstream.streamdraw.utils.KEY_WINNER
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

class GameViewModel @AssistedInject constructor(
    private val chatClient: ChatClient,
    private val randomWordsFetcher: RandomWordsFetcher,
    @Assisted val cid: String
) : ViewModel() {

    private val channelClient = chatClient.channel(cid)

    private val _isHost: MutableState<Boolean> = mutableStateOf(false)
    val isHost: State<Boolean> = _isHost

    private val _gameStatus: MutableState<GameStatus> = mutableStateOf(GameStatus.START)
    val gameStatus: State<GameStatus> = _gameStatus

    private val _members: MutableState<List<Member>> = mutableStateOf(listOf())
    val members: State<List<Member>> = _members

    private val _winner: MutableStateFlow<String?> = MutableStateFlow(null)
    val isWinner: Flow<Boolean> = _winner.filter { currentUser != null }.mapLatest {
        it == currentUser?.name
    }

    private val _newDrawingImage: MutableState<String?> = mutableStateOf(null)
    val newDrawingImage: State<String?> = _newDrawingImage

    private val _newSingleMessage: MutableState<GameMessage?> = mutableStateOf(null)
    val newSingleMessage: State<GameMessage?> = _newSingleMessage

    private val _selectedWord = MutableStateFlow<String?>(null)
    val selectedWord: StateFlow<String?> = _selectedWord

    private val _randomWords = MutableStateFlow<List<String>?>(null)
    val randomWords: StateFlow<List<String>?> = _randomWords

    private val currentUser: User?
        get() = chatClient.getCurrentUser()

    var host: String? = null

    private val firebaseDb =
        FirebaseDatabase.getInstance().getReference(channelClient.groupId)

    private val disposables: MutableList<Disposable> = mutableListOf()

    init {
        fetchChannelInformation()
        subscribeChannelEvents()
        subscribeNewMessageEvent()
        subscribeUserWatchingEvent()
    }

    /** fetches the current channel information. */
    private fun fetchChannelInformation() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = channelClient.watch().await()
            result.onSuccessSuspend {
                host = it.hostName
                _isHost.value = host == currentUser?.name
                _selectedWord.value = it.selectedWord
                _gameStatus.value = it.gameStatus
                _members.value = it.members
                if (isHost.value) {
                    fetchRandomWords()
                } else {
                    subscribeFirebaseChannel()
                    sendJoinedMessage()
                }
            }
        }
    }

    /** send a new joined hello message. */
    private fun sendJoinedMessage() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = currentUser ?: return@launch
            chatClient.sendMessage(
                channelType = channelClient.channelType,
                channelId = channelClient.channelId,
                message = Message(
                    text = "\uD83D\uDC4B ${user.name} has joined game."
                )
            ).await()
        }
    }

    /** subscribe the firebase channel. */
    private fun subscribeFirebaseChannel() {
        firebaseDb.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    _newDrawingImage.value = snapshot.getValue(String::class.java)
                }
            }

            override fun onCancelled(p0: DatabaseError) = Unit
        })
    }

    /** subscribe channel updated events. */
    private fun subscribeChannelEvents() {
        channelClient.subscribeFor<ChannelUpdatedByUserEvent> { event ->
            val channel = event.channel
            host = channel.hostName
            _isHost.value = host == currentUser?.name
            _selectedWord.value = channel.selectedWord
            _gameStatus.value = channel.gameStatus
            _winner.value = channel.winner
        }.also { disposables.add(it) }

        channelClient.subscribeFor<ChannelDeletedEvent> {
            _gameStatus.value = GameStatus.DELETED
        }
    }

    /** subscribe user watching events. */
    private fun subscribeUserWatchingEvent() {
        channelClient.subscribeFor<UserStartWatchingEvent> { event ->
            val user = _members.value.find { it.user.name == event.user.name }
            if (user == null) {
                _members.value = _members.value + Member(user = event.user)
            }
        }

        channelClient.subscribeFor<UserStopWatchingEvent> { event ->
            _members.value = _members.value - Member(user = event.user)
            if (isHost.value) {
                sendLeftMessage(event.user)
            }
        }
    }

    /** subscribe new message events. */
    private fun subscribeNewMessageEvent() {
        channelClient.subscribeFor<NewMessageEvent> { event ->
            val name = currentUser?.name
            val sender = event.message.user.name
            val selectedWord = selectedWord.value
            val isAnswer = event.message.text.lowercase() == selectedWord?.lowercase()
            if (isHost.value && sender != name && isAnswer) {
                sendGameFinishEvent(sender)
            }
            _newSingleMessage.value = GameMessage(sender, event.message.text)
        }
    }

    /** send game finish event by host. */
    private fun sendGameFinishEvent(winner: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // send a system message to the channel.
            chatClient.sendMessage(
                channelType = channelClient.channelType,
                channelId = channelClient.channelId,
                message = Message(
                    text = "Congratulation! $winner has correct the answer. \uD83C\uDF89"
                )
            ).await()

            // update channel information with the selected word.
            val hostName = currentUser?.name ?: return@launch
            channelClient.update(
                extraData = mapOf(
                    KEY_NAME to hostName.groupName, // channel name.
                    KEY_HOST_NAME to hostName, // host name.
                    KEY_GAME_STATUS to GameStatus.FINISH.status, // game status to drawing start.,
                    KEY_WINNER to winner
                )
            ).await()
        }

        if (isHost.value) {
            firebaseDb.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        _newDrawingImage.value = snapshot.getValue(String::class.java)
                    }
                }

                override fun onCancelled(p0: DatabaseError) = Unit
            })
        }
    }

    /** fetch a random words from the network. */
    private suspend fun fetchRandomWords() {
        _randomWords.emit(randomWordsFetcher.getRandomWords())
    }

    /** set a selected word by host. */
    fun setSelectedWord(word: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _selectedWord.emit(word)

            // send a system message to the channel.
            chatClient.sendMessage(
                channelType = channelClient.channelType,
                channelId = channelClient.channelId,
                message = Message(
                    text = "Host selected a word! Guess what the host is drawing! (group id: ${channelClient.groupId})",
                )
            ).await()

            // update channel information with the selected word.
            val hostName = currentUser?.name ?: return@launch
            channelClient.update(
                extraData = mapOf(
                    KEY_NAME to hostName.groupName, // channel name.
                    KEY_HOST_NAME to hostName, // host name.
                    KEY_GAME_STATUS to GameStatus.START.status, // game status to drawing start.
                    KEY_SELECTED_WORD to word, // a selected word (answer).
                )
            ).await()
        }
    }

    /** send a message to the connected channel. */
    fun sendChatMessage(message: String) {
        val user = currentUser ?: return
        viewModelScope.launch {
            channelClient.sendMessage(
                Message(user = user, text = message)
            ).await()
        }
    }

    /** broadcast the bitmap to the connected channel. */
    fun broadcastToChannel(bitmap: Bitmap) {
        viewModelScope.launch {
            bitmap.toBase64String()?.let { stringBitmap ->
                firebaseDb.setValue(stringBitmap)
            }
        }
    }

    /** finish the winner confetti animation. */
    fun finishWinnerAnimation() {
        _winner.value = null
    }

    /** send a new joined hello message. */
    private fun sendLeftMessage(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            chatClient.sendMessage(
                channelType = channelClient.channelType,
                channelId = channelClient.channelId,
                message = Message(
                    user = user,
                    text = "${user.name} has left game."
                )
            ).await()
        }
    }

    /** restart game. */
    fun restartGame() {
        viewModelScope.launch(Dispatchers.IO) {
            fetchRandomWords()
            _selectedWord.value = null
            _gameStatus.value = GameStatus.START
        }
    }

    /** clear and dispose all connection resources. */
    fun exitChannel() {
        if (isHost.value) {
            viewModelScope.launch {
                chatClient.deleteChannel(
                    channelType = channelClient.channelType,
                    channelId = channelClient.channelId
                ).await()
            }
        }

        // disconnect user.
        chatClient.disconnect()

        // dispose all subscribers.
        disposables.forEach { it.dispose() }
    }

    override fun onCleared() {
        super.onCleared()
        exitChannel()
    }

    @AssistedFactory
    interface GameAssistedFactory {
        fun create(cid: String): GameViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: GameAssistedFactory,
            cid: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return assistedFactory.create(cid) as T
            }
        }
    }
}
