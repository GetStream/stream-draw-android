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

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.getstream.streamdraw.states.AnimationState
import io.getstream.streamdraw.ui.theme.hostAccent

@Composable
fun NewGameMessage(
    viewModel: GameViewModel
) {
    val newMessage = viewModel.newSingleMessage.value
    if (newMessage != null) {
        var animationState by remember { mutableStateOf(AnimationState.NONE) }
        val springValue: Float by animateFloatAsState(
            if (animationState == AnimationState.NONE) 0f else 1f,
            spring(dampingRatio = 0.3f, stiffness = Spring.StiffnessMediumLow)
        )
        LaunchedEffect(newMessage) {
            animationState = AnimationState.START
        }
        Text(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                .height(40.dp)
                .offset(x = (-200).dp + (200 * springValue).dp),
            text = "${newMessage.name}: ${newMessage.message}",
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            color = hostAccent,
            maxLines = 2,
        )
    }
}
