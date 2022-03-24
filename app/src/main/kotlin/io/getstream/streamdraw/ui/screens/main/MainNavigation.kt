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

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.getstream.streamdraw.ui.screens.group.CreateGroup
import io.getstream.streamdraw.ui.screens.group.JoinGroup
import io.getstream.streamdraw.ui.screens.home.HomeScreen

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = NAV_SPLASH) {
        composable(NAV_SPLASH) { HomeScreen(navController) }
        composable(NAV_CREATE_GROUP) { CreateGroup(hiltViewModel()) }
        composable(NAV_JOIN_GROUP) { JoinGroup(hiltViewModel()) }
    }
}

const val NAV_SPLASH = "splash"
const val NAV_CREATE_GROUP = "create_group"
const val NAV_JOIN_GROUP = "join_group"
