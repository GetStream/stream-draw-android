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

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import io.getstream.streamdraw.R

@Composable
fun MainScreen(
    content: @Composable () -> Unit
) {

    ConstraintLayout(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxSize()
    ) {
        val (logo, image, contents) = createRefs()

        Row(
            modifier = Modifier.constrainAs(logo) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(image.top)
            }
        ) {
            Image(
                modifier = Modifier
                    .width(140.dp)
                    .height(65.dp),
                bitmap = ImageBitmap.imageResource(R.drawable.stream_logo),
                contentDescription = null
            )

            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = stringResource(R.string.app_name),
                color = MaterialTheme.colors.onPrimary,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier
                .constrainAs(image) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(logo.bottom)
                    bottom.linkTo(contents.top)
                }
                .fillMaxWidth()
                .height(320.dp)
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                bitmap = ImageBitmap.imageResource(R.drawable.sketchbook_bg),
                contentDescription = null
            )

            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 30.dp),
                bitmap = ImageBitmap.imageResource(R.drawable.drawing_content),
                contentDescription = null
            )
        }

        Column(
            modifier = Modifier
                .constrainAs(contents) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(image.bottom)
                    bottom.linkTo(parent.bottom)
                }
        ) {
            content()
        }
    }
}
