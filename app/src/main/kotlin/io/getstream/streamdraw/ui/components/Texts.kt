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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.getstream.streamdraw.ui.theme.PrimaryColor

@Composable
fun TitleText(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    text: String,
    marginTop: Dp = 0.dp,
    marginBottom: Dp = 0.dp,
    marginLeft: Dp = 0.dp,
    marginRight: Dp = 0.dp
) {
    Text(
        text = text,
        modifier = modifier
            .clickable { onClick?.invoke() }
            .padding(top = marginTop, bottom = marginBottom, start = marginLeft, end = marginRight),
        fontSize = 32.sp,
        color = PrimaryColor,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
}

@Composable
fun SubtitleText(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    text: String,
    marginTop: Dp = 0.dp,
    marginBottom: Dp = 0.dp,
    marginLeft: Dp = 0.dp,
    marginRight: Dp = 0.dp
) {
    Text(
        text = text,
        modifier = modifier
            .clickable { onClick?.invoke() }
            .padding(top = marginTop, bottom = marginBottom, start = marginLeft, end = marginRight),
        fontSize = 18.sp,
        color = PrimaryColor,
        fontWeight = FontWeight.Normal,
        textAlign = TextAlign.Center
    )
}

@Composable
fun NormalText(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    text: String,
    size: TextUnit,
    align: TextAlign = TextAlign.Left,
    color: Color = PrimaryColor,
    weight: FontWeight = FontWeight.Normal,
    marginTop: Dp = 0.dp,
    marginBottom: Dp = 0.dp,
    marginLeft: Dp = 0.dp,
    marginRight: Dp = 0.dp
) {
    Text(
        text = text,
        modifier = modifier
            .wrapContentWidth()
            .wrapContentHeight()
            .clickable { onClick?.invoke() }
            .padding(top = marginTop, bottom = marginBottom, start = marginLeft, end = marginRight),
        fontSize = size,
        color = color,
        fontWeight = weight,
        textAlign = align
    )
}
