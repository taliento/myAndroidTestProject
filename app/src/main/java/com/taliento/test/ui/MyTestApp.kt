/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.taliento.test.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.taliento.test.MyTestProjectState
import com.taliento.test.ui.mytest.MyTestScreen
import com.taliento.test.ui.ocr.OCRScreen

@Composable
fun MyTestApp(appState: MyTestProjectState) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = { appState.navigate("main") },
                    icon = { Icon(imageVector = Icons.Rounded.Home, contentDescription = null) },
                    label = { Text("Home") },
                    alwaysShowLabel = true,

                    )
                NavigationBarItem(
                    selected = false,
                    onClick = { appState.navigate("ocr") },
                    icon = { Icon(imageVector = Icons.Rounded.Search, contentDescription = null) },
                    label = { Text("OCR") },
                    alwaysShowLabel = true,

                    )
            }
        }) { padding ->
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Horizontal,
                        ),
                    ),
            ) {
                NavHost(navController = appState.navController, startDestination = "main") {
                    composable("main") { MyTestScreen(modifier = Modifier.padding(16.dp)) }
                    composable("ocr") { OCRScreen() }
                    // TODO: Add more destinations
                }
            }

        }
        
        
    }

    
}
