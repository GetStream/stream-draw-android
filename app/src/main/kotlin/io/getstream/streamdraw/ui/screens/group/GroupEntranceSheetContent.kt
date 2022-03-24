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

package io.getstream.streamdraw.ui.screens.group

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.getstream.chat.android.client.models.Channel
import io.getstream.streamdraw.R
import io.getstream.streamdraw.extensions.groupId
import io.getstream.streamdraw.extensions.setClipboard
import io.getstream.streamdraw.ui.components.NormalText
import io.getstream.streamdraw.ui.components.PrimaryButton
import io.getstream.streamdraw.ui.components.SubtitleText
import io.getstream.streamdraw.ui.components.TitleText
import io.getstream.streamdraw.ui.screens.game.GameActivity
import io.getstream.streamdraw.ui.theme.LightColor
import io.getstream.streamdraw.ui.theme.PrimaryColor

@Composable
fun GroupEntranceSheetContent(channel: Channel?) {
    val context = LocalContext.current
    Box(
        Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        if (channel != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TitleText(
                    text = stringResource(id = R.string.group_entrance),
                    marginTop = 12.dp,
                    marginBottom = 12.dp
                )
                SubtitleText(
                    text = stringResource(id = R.string.group_created_desc),
                    marginTop = 8.dp,
                    marginBottom = 18.dp
                )
                Box(
                    modifier = Modifier
                        .background(PrimaryColor, shape = RoundedCornerShape(8.dp))
                        .wrapContentSize()
                        .padding(12.dp)
                ) {
                    NormalText(
                        text = channel.groupId,
                        size = 32.sp,
                        weight = FontWeight.Bold,
                        align = TextAlign.Center,
                        color = LightColor,
                        onClick = { context.setClipboard(channel.groupId) }
                    )
                }
                NormalText(
                    text = stringResource(id = R.string.invite_people),
                    size = 16.sp,
                    weight = FontWeight.Normal,
                    align = TextAlign.Center,
                    marginTop = 16.dp,
                    onClick = { context.setClipboard(channel.groupId) }
                )
                PrimaryButton(
                    text = stringResource(id = R.string.continue_to_game),
                    marginTop = 24.dp,
                    marginBottom = 24.dp,
                    onClick = {
                        context.startActivity(GameActivity.getIntent(context, channel.cid))
                    }
                )
            }
        } else {
            SubtitleText(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(id = R.string.group_created_failed),
            )
        }
    }
}
