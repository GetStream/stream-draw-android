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

package io.getstream.streamdraw.ui.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.sketchbook.SketchbookController
import io.getstream.streamdraw.R
import io.getstream.streamdraw.ui.screens.game.GameChatDialog
import io.getstream.streamdraw.ui.theme.hostAccent

@Composable
fun SketchbookControlMenu(
    controller: SketchbookController,
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .clickable {
                    controller.setEraseMode(false)
                    controller.setRainbowShader()
                    Toast
                        .makeText(context, "Rainbow Shader", Toast.LENGTH_SHORT)
                        .show()
                }
                .size(40.dp),
            tint = ChatTheme.colors.textHighEmphasis,
            imageVector = ImageVector.vectorResource(R.drawable.ic_brush),
            contentDescription = null
        )

        Box {
            val expanded = remember { mutableStateOf(false) }
            val widths = listOf(10f, 20f, 30f, 40f, 50f)
            Icon(
                modifier = Modifier
                    .clickable { expanded.value = true }
                    .size(40.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_line_weight),
                tint = ChatTheme.colors.textHighEmphasis,
                contentDescription = null
            )
            DropdownMenu(
                expanded = expanded.value,
                modifier = Modifier.background(ChatTheme.colors.appBackground),
                onDismissRequest = { expanded.value = false }
            ) {
                widths.forEach { width ->
                    DropdownMenuItem(
                        onClick = {
                            controller.setPaintStrokeWidth(width)
                            expanded.value = false
                        }
                    ) {
                        Text(text = width.toString(), color = ChatTheme.colors.textHighEmphasis)
                    }
                }
            }
        }

        Box {
            val expanded = remember { mutableStateOf(false) }
            Icon(
                modifier = Modifier
                    .clickable { expanded.value = true }
                    .size(40.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_line_style),
                tint = ChatTheme.colors.textHighEmphasis,
                contentDescription = null
            )

            DropdownMenu(
                expanded = expanded.value,
                modifier = Modifier.background(ChatTheme.colors.appBackground),
                onDismissRequest = { expanded.value = false }
            ) {

                DropdownMenuItem(
                    onClick = {
                        controller.setPathEffect(PathEffect.cornerPathEffect(60f))
                        expanded.value = false
                    }
                ) {
                    Text(text = "Normal", color = ChatTheme.colors.textHighEmphasis)
                }

                DropdownMenuItem(
                    onClick = {
                        controller.setPathEffect(
                            PathEffect.dashPathEffect(
                                floatArrayOf(20f, 40f),
                                40f
                            )
                        )
                        expanded.value = false
                    }
                ) {
                    Text(text = "Dash Effect", color = ChatTheme.colors.textHighEmphasis)
                }
            }
        }

        Image(
            modifier = Modifier
                .clickable {
                    controller.toggleEraseMode()
                    Toast
                        .makeText(context, "Erase Mode", Toast.LENGTH_SHORT)
                        .show()
                }
                .size(40.dp),
            bitmap = ImageBitmap.imageResource(R.drawable.eraser),
            contentDescription = null
        )

        val expand = remember { mutableStateOf(false) }
        Icon(
            modifier = Modifier
                .clickable {
                    expand.value = true
                }
                .size(40.dp),
            imageVector = ImageVector.vectorResource(R.drawable.ic_bubble),
            tint = hostAccent,
            contentDescription = null
        )

        GameChatDialog(listViewModel = hiltViewModel(), expanded = expand)
    }
}
