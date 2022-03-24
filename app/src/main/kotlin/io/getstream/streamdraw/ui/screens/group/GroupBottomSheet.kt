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

import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp

@Composable
fun GroupBottomSheet(
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    expand: Boolean,
    sheetContent: @Composable () -> Unit,
    mainContent: @Composable () -> Unit
) {
    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = { sheetContent() },
        sheetPeekHeight = 0.dp
    ) {
        mainContent()
    }
    if (expand && bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
        LaunchedEffect(bottomSheetScaffoldState.bottomSheetState) {
            bottomSheetScaffoldState.bottomSheetState.expand()
        }
    }
}
