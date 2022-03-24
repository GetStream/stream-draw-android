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

package io.getstream.streamdraw.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.getstream.streamdraw.R
import io.getstream.streamdraw.ui.components.PrimaryButton
import io.getstream.streamdraw.ui.components.SecondaryButton
import io.getstream.streamdraw.ui.screens.main.MainScreen
import io.getstream.streamdraw.ui.screens.main.NAV_CREATE_GROUP
import io.getstream.streamdraw.ui.screens.main.NAV_JOIN_GROUP

@Composable
fun HomeScreen(navController: NavController) {
    MainScreen {
        PrimaryButton(
            onClick = { navController.navigate(NAV_CREATE_GROUP) },
            text = stringResource(id = R.string.create_group),
            marginTop = 12.dp,
            marginBottom = 12.dp,
            marginLeft = 24.dp,
            marginRight = 24.dp
        )
        SecondaryButton(
            onClick = { navController.navigate(NAV_JOIN_GROUP) },
            text = stringResource(id = R.string.join_group),
            marginTop = 12.dp,
            marginBottom = 12.dp,
            marginLeft = 24.dp,
            marginRight = 24.dp
        )
    }
}
