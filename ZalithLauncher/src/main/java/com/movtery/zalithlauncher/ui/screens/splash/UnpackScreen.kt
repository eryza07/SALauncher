/*
 * Zalith Launcher 2 - Neobrutalism UI
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

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.nonInteractiveScrollbar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.components.InstallableItem
import com.movtery.zalithlauncher.ui.base.BaseScreen
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.theme.neobrutal.NeobrutalButton
import com.movtery.zalithlauncher.ui.theme.neobrutal.NeobrutalPillBadge
import com.movtery.zalithlauncher.ui.theme.neobrutal.NeobrutalTokens
import com.movtery.zalithlauncher.ui.theme.neobrutal.NeobrutalWindowBox
import com.movtery.zalithlauncher.utils.animation.getAnimateTween
import com.movtery.zalithlauncher.utils.animation.swapAnimateDpAsState
import com.movtery.zalithlauncher.viewmodel.SplashBackStackViewModel

@Composable
fun UnpackScreen(
    items: List<InstallableItem>,
    screenViewModel: SplashBackStackViewModel,
    onAgreeClick: () -> Unit = {}
) {
    BaseScreen(
        screenKey = NormalNavKey.UnpackDeps,
        currentKey = screenViewModel.splashScreen.currentKey
    ) { isVisible ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            UnpackTaskList(
                isVisible = isVisible,
                items = items,
                modifier = Modifier
                    .weight(6.5f)
                    .fillMaxHeight()
            )

            ActionMenu(
                isVisible = isVisible,
                modifier = Modifier
                    .weight(3.5f)
                    .fillMaxHeight(),
                onAgreeClick = onAgreeClick
            )
        }
    }
}

@Composable
private fun ActionMenu(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    onAgreeClick: () -> Unit = {}
) {
    var installing by remember { mutableStateOf(false) }

    val xOffset by swapAnimateDpAsState(
        targetValue = 40.dp,
        swapIn = isVisible,
        isHorizontal = true
    )

    NeobrutalWindowBox(
        title = "[SETUP] RUNTIME JAVA",
        modifier = modifier.offset { IntOffset(x = xOffset.roundToPx(), y = 0) },
        headerColor = NeobrutalTokens.AccentOrange,
        headerTextColor = Color.Black
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                NeobrutalPillBadge(
                    text = "LANGKAH AWAL",
                    backgroundColor = Color.Black,
                    textColor = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (installing) {
                        stringResource(R.string.splash_screen_installing)
                    } else {
                        stringResource(R.string.splash_screen_unpack_desc)
                    },
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFFBEB), RoundedCornerShape(8.dp))
                        .border(1.5.dp, Color.Black, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Text(
                            text = "KOMPONEN DISEDIAKAN:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• OpenJDK 8, 17, 21 (ARM64)\n• JNA & Android Native Bridge\n• Terracotta Engine",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeobrutalTokens.TextMuted,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            NeobrutalButton(
                enabled = !installing,
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = if (installing) Color(0xFFE5E7EB) else NeobrutalTokens.AccentPrimary,
                shadowOffset = if (installing) 0.dp else 4.dp,
                onClick = {
                    installing = true
                    onAgreeClick()
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (installing) "SEDANG MEMASANG RUNTIME..." else "▶ PASANG RUNTIME SEKARANG",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun UnpackTaskList(
    isVisible: Boolean,
    items: List<InstallableItem>,
    modifier: Modifier = Modifier,
) {
    val yOffset by swapAnimateDpAsState(
        targetValue = (-40).dp,
        swapIn = isVisible
    )

    NeobrutalWindowBox(
        title = "[DEP] DAFTAR DEPENDENCIES & RUNTIME",
        modifier = modifier.offset { IntOffset(x = 0, y = yOffset.roundToPx()) },
        headerColor = NeobrutalTokens.AccentSecondary,
        headerTextColor = Color.White,
        statusPill = "${items.size} ITEMS",
        statusPillColor = NeobrutalTokens.AccentMint
    ) {
        val scrollState = rememberLazyListState()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nonInteractiveScrollbar(
                    state = scrollState.scrollIndicatorState!!,
                    orientation = Orientation.Vertical,
                ),
            contentPadding = PaddingValues(vertical = 4.dp),
            state = scrollState,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items) { item ->
                TaskItem(item = item)
            }
        }
    }
}

@Composable
private fun TaskItem(
    item: InstallableItem,
    modifier: Modifier = Modifier
) {
    val state by item.state.collectAsStateWithLifecycle()
    val message by item.task.taskMessage.collectAsStateWithLifecycle()

    val cardBg = when (state) {
        InstallableItem.State.FINISHED -> Color(0xFFF0FDF4)
        InstallableItem.State.RUNNING -> Color(0xFFFFFBEB)
        else -> Color.White
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(cardBg, RoundedCornerShape(8.dp))
            .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .animateContentSize(animationSpec = getAnimateTween())
            ) {
                Text(
                    text = item.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
                item.summary?.let {
                    Text(
                        text = it,
                        fontSize = 10.sp,
                        color = NeobrutalTokens.TextMuted
                    )
                }
                if (state == InstallableItem.State.RUNNING) {
                    message?.let { taskMessage ->
                        Text(
                            text = "> $taskMessage",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeobrutalTokens.AccentSecondary,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            when (state) {
                InstallableItem.State.NOT_STARTED -> {
                    NeobrutalPillBadge(
                        text = "BELUM MULAI",
                        backgroundColor = Color(0xFFF3F4F6),
                        textColor = Color.Black,
                        borderWidth = 1.5.dp
                    )
                }
                InstallableItem.State.PENDING -> {
                    NeobrutalPillBadge(
                        text = "MENUNGGU",
                        backgroundColor = Color(0xFFFFF9DB),
                        textColor = Color.Black,
                        borderWidth = 1.5.dp
                    )
                }
                InstallableItem.State.RUNNING -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        NeobrutalPillBadge(
                            text = "MEMASANG",
                            backgroundColor = NeobrutalTokens.AccentPrimary,
                            textColor = Color.Black,
                            borderWidth = 1.5.dp
                        )
                    }
                }
                InstallableItem.State.FINISHED -> {
                    NeobrutalPillBadge(
                        text = "[OK] SELESAI",
                        backgroundColor = NeobrutalTokens.AccentMint,
                        textColor = Color.Black,
                        borderWidth = 1.5.dp
                    )
                }
                else -> {
                    NeobrutalPillBadge(
                        text = state.name,
                        backgroundColor = Color.White,
                        textColor = Color.Black,
                        borderWidth = 1.5.dp
                    )
                }
            }
        }
    }
}