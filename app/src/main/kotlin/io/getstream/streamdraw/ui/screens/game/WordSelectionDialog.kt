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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.getstream.streamdraw.R
import io.getstream.streamdraw.ui.components.SubtitleText
import io.getstream.streamdraw.ui.components.TitleText
import io.getstream.streamdraw.ui.theme.PrimaryColor
import timber.log.Timber

@Composable
fun WordSelectionDialog(
    wordSelected: (selection: String) -> Unit,
    words: List<String>
) {
    Dialog(onDismissRequest = { }) {
        WordSelectionDialogView(words = words, wordSelected)
    }
}

@Composable
private fun WordSelectionDialogView(
    words: List<String>,
    wordSelected: (selection: String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            TitleText(text = stringResource(id = R.string.select_a_word))
            Column(modifier = Modifier.padding(top = 18.dp)) {
                words.forEach {
                    SubtitleText(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        text = it,
                        onClick = { wordSelected.invoke(it) }
                    )
                    Divider(color = PrimaryColor, thickness = 1.dp)
                }
            }
        }
    }
}

@Preview
@Composable
private fun DefaultPreview() {
    WordSelectionDialogView(listOf("Pigeon", "Hammer", "Landslide")) { selection ->
        Timber.d(selection)
    }
}
