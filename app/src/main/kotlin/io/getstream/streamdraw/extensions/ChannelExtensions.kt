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

package io.getstream.streamdraw.extensions

import io.getstream.chat.android.client.channel.ChannelClient
import io.getstream.chat.android.client.models.Channel
import io.getstream.streamdraw.ui.screens.game.GameStatus
import io.getstream.streamdraw.utils.KEY_GAME_STATUS
import io.getstream.streamdraw.utils.KEY_HOST_NAME
import io.getstream.streamdraw.utils.KEY_SELECTED_WORD
import io.getstream.streamdraw.utils.KEY_WINNER

inline val Channel.groupId: String
    get() = cid.split(":")[1]

inline val ChannelClient.groupId: String
    get() = cid.split(":")[1]

inline val Channel.selectedWord: String?
    get() = extraData[KEY_SELECTED_WORD]?.toString()

inline val Channel.hostName: String?
    get() = extraData[KEY_HOST_NAME]?.toString()

inline val Channel.gameStatus: GameStatus
    get() {
        val statusName = extraData[KEY_GAME_STATUS]?.toString() ?: GameStatus.START.name
        return GameStatus.getGameStatusByName(statusName)
    }

inline val Channel.winner: String?
    get() = extraData[KEY_WINNER]?.toString()
