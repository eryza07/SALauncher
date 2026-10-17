/*
 * Zalith Launcher 2
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

package com.movtery.zalithlauncher.ui.screens.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.movtery.zalithlauncher.BuildKeys
import com.movtery.zalithlauncher.components.InstallableItem
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.screens.rememberTransitionSpec
import com.movtery.zalithlauncher.ui.theme.neobrutal.NeobrutalDottedCanvas
import com.movtery.zalithlauncher.ui.theme.neobrutal.NeobrutalPillBadge
import com.movtery.zalithlauncher.ui.theme.neobrutal.NeobrutalTokens
import com.movtery.zalithlauncher.viewmodel.SplashBackStackViewModel

/**
 * @param startAllTask 开启全部的解压任务
 * @param unpackItems 解压任务列表
 */
@Composable
fun SplashScreen(
    startAllTask: () -> Unit,
    unpackItems: List<InstallableItem>,
    screenViewModel: SplashBackStackViewModel
) {
    NeobrutalDottedCanvas {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .background(Color.White)
                    .border(width = 2.dp, color = Color.Black),
                contentColor = Color.Black
            )

            NavigationUI(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                startAllTask = startAllTask,
                unpackItems = unpackItems,
                screenViewModel = screenViewModel
            )
        }
    }
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    contentColor: Color,
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(NeobrutalTokens.AccentPrimary, RoundedCornerShape(2.dp))
                    .border(1.5.dp, Color.Black, RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${BuildKeys.LAUNCHER_NAME.uppercase()} // INITIAL SETUP",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black,
                letterSpacing = 1.sp
            )
        }

        NeobrutalPillBadge(
            text = "RUNTIME SETUP",
            backgroundColor = NeobrutalTokens.AccentMint,
            textColor = Color.Black,
            borderWidth = 1.5.dp
        )
    }
}

@Composable
private fun NavigationUI(
    modifier: Modifier = Modifier,
    startAllTask: () -> Unit,
    unpackItems: List<InstallableItem>,
    screenViewModel: SplashBackStackViewModel
) {
    val backStack = screenViewModel.splashScreen.backStack

    val currentKey = backStack.lastOrNull()
    LaunchedEffect(currentKey) {
        screenViewModel.splashScreen.currentKey = currentKey
    }

    if (backStack.isNotEmpty()) {
        NavDisplay(
            backStack = backStack,
            modifier = modifier,
            transitionSpec = rememberTransitionSpec(),
            popTransitionSpec = rememberTransitionSpec(),
            entryProvider = entryProvider {
                entry<NormalNavKey.UnpackDeps> {
                    UnpackScreen(unpackItems, screenViewModel) {
                        startAllTask()
                    }
                }
            }
        )
    } else {
        Box(modifier)
    }
}