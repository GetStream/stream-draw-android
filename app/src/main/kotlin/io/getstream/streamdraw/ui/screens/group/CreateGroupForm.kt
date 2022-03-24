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

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.getstream.streamdraw.R
import io.getstream.streamdraw.data.GameConnectionState
import io.getstream.streamdraw.data.isLoading
import io.getstream.streamdraw.extensions.toast
import io.getstream.streamdraw.ui.components.AppTextField
import io.getstream.streamdraw.ui.components.PrimaryButton
import io.getstream.streamdraw.ui.screens.main.MainScreen
import io.getstream.streamdraw.ui.screens.main.MainViewModel

@Composable
fun CreateGroupForm(
    viewModel: MainViewModel,
    connectionState: GameConnectionState
) {
    var displayName by remember { mutableStateOf("") }
    val context: Context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (connectionState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            MainScreen {
                Column(
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                ) {
                    AppTextField(
                        label = stringResource(id = R.string.display_name),
                        marginTop = 16.dp,
                        onValueChange = { displayName = it },
                        value = displayName
                    )

                    PrimaryButton(
                        onClick = {
                            if (displayName.isNotEmpty()) {
                                viewModel.createGameGroup(displayName = displayName)
                            } else {
                                context.toast(R.string.fill_all_blanks)
                            }
                        },
                        text = stringResource(id = R.string.create_group),
                        marginTop = 24.dp
                    )
                }
            }
        }
    }
}
