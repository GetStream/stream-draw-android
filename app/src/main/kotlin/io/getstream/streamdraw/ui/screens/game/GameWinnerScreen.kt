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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.streamdraw.R
import io.getstream.streamdraw.states.AnimationState
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.compose.OnParticleSystemUpdateListener
import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartySystem
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Spread
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

@Composable
fun GameWinnerScreen(
    gameViewModel: GameViewModel
) {
    val isWinner = gameViewModel.isWinner.collectAsState(initial = false)
    val keyboardController = LocalSoftwareKeyboardController.current
    if (isWinner.value) {
        Box(modifier = Modifier.fillMaxSize()) {

            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = parties(),
                updateListener = object : OnParticleSystemUpdateListener {
                    override fun onParticleSystemEnded(system: PartySystem, activeSystems: Int) {
                        gameViewModel.finishWinnerAnimation()
                    }
                }
            )

            var animationState by remember { mutableStateOf(AnimationState.NONE) }
            val springValue: Float by animateFloatAsState(
                if (animationState == AnimationState.NONE) 0f else 1f,
                spring(dampingRatio = 0.3f, stiffness = Spring.StiffnessMediumLow)
            )
            LaunchedEffect(Unit) {
                animationState = AnimationState.START
                keyboardController?.hide()
            }
            Text(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                    .align(Alignment.Center)
                    .scale(springValue),
                text = stringResource(id = R.string.game_winner),
                color = ChatTheme.colors.textHighEmphasis,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                maxLines = 2,
            )
        }
    }
}

private fun parties(): List<Party> {
    val parade = Party(
        speed = 10f,
        maxSpeed = 30f,
        damping = 0.9f,
        angle = Angle.RIGHT - 45,
        spread = Spread.SMALL,
        colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
        emitter = Emitter(duration = 3, TimeUnit.SECONDS).perSecond(30),
        position = Position.Relative(0.0, 0.5)
    )

    val explode = Party(
        speed = 0f,
        maxSpeed = 30f,
        damping = 0.9f,
        spread = 360,
        colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
        emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
        position = Position.Relative(0.5, 0.3)
    )

    return listOf(
        parade,
        parade.copy(
            angle = parade.angle - 90, // flip angle from right to left
            position = Position.Relative(1.0, 0.5)
        ),
        explode
    )
}
