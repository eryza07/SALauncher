/*
 * SA Launcher - Universal Neobrutalism Top Bar & Sub-Header
 * Implements the universal navigation and sub-headers from Zalith.pdf (Pages 1 to 9)
 */

package com.movtery.zalithlauncher.ui.theme.neobrutal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.movtery.zalithlauncher.ui.resolveAndroidString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.game.account.Account
import com.movtery.zalithlauncher.ui.screens.NestedNavKey
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.screens.TitledNavKey
import com.movtery.zalithlauncher.ui.screens.content.elements.PlayerFace

/**
 * Universal Neobrutalism Top Navigation Bar
 * Shared identically across all screens (Pages 1-9 in Zalith.pdf)
 */
@Composable
fun UniversalNeobrutalTopBar(
    currentKey: TitledNavKey?,
    currentAccount: Account?,
    taskRunning: Boolean,
    isTasksExpanded: Boolean,
    onBerandaClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onModsClick: () -> Unit,
    onFileManagerClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAccountClick: () -> Unit,
    onChangeTasksExpanded: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBeranda = currentKey == null || currentKey is NormalNavKey.LauncherMain
    val isDownload = currentKey is NestedNavKey.Download
    val isMods = currentKey is NestedNavKey.VersionSettings || currentKey is NormalNavKey.Mods
    val isFileManager = currentKey is NormalNavKey.FileSelector || currentKey is NormalNavKey.FileManager
    val isSettings = currentKey is NestedNavKey.Settings

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(38.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. App Title / Brand Pill: ZALITH-OE ★
        NeobrutalPillTab(
            text = "ZALITH-OE ★",
            isActive = isBeranda,
            activeColor = NeobrutalTokens.AccentPrimary,
            onClick = onBerandaClick
        )

        Spacer(modifier = Modifier.width(10.dp))

        // 2. Center Navigation Tabs
        Row(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NeobrutalPillTab(
                text = "BERANDA",
                isActive = isBeranda,
                activeColor = NeobrutalTokens.AccentPrimary,
                onClick = onBerandaClick
            )

            NeobrutalPillTab(
                text = "DOWNLOAD",
                isActive = isDownload,
                activeColor = NeobrutalTokens.AccentPrimary,
                onClick = onDownloadClick
            )

            NeobrutalPillTab(
                text = "MODS & LOADER",
                isActive = isMods,
                activeColor = NeobrutalTokens.AccentPrimary,
                onClick = onModsClick
            )

            NeobrutalPillTab(
                text = "FILE MANAGER",
                isActive = isFileManager,
                activeColor = NeobrutalTokens.AccentPrimary,
                onClick = onFileManagerClick
            )

            NeobrutalPillTab(
                text = "PENGATURAN",
                isActive = isSettings,
                activeColor = NeobrutalTokens.AccentPink,
                onClick = onSettingsClick
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // 3. Optional Background Task Indicator
        if (taskRunning) {
            NeobrutalButton(
                onClick = onChangeTasksExpanded,
                backgroundColor = NeobrutalTokens.AccentMint,
                shadowOffset = 2.dp,
                cornerRadius = NeobrutalTokens.RadiusPill,
                modifier = Modifier.padding(end = 6.dp)
            ) {
                Text(
                    text = "TASKS ↻",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // 4. User Profile Pill Button
        val accountName = currentAccount?.username
        NeobrutalButton(
            onClick = onAccountClick,
            backgroundColor = if (accountName != null) Color.White else NeobrutalTokens.AccentPrimary,
            shadowOffset = 2.dp,
            cornerRadius = NeobrutalTokens.RadiusPill
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color(0xFFE6F3F7), RoundedCornerShape(4.dp))
                        .border(1.5.dp, Color.Black, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (currentAccount != null) {
                        PlayerFace(
                            account = currentAccount,
                            avatarSize = 18.dp,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Text(
                            text = "MC",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = accountName ?: "+ LOGIN AKUN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (accountName != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "[USER]",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeobrutalTokens.TextMuted
                    )
                }
            }
        }
    }
}

/**
 * Sub-Header for sub-screens (Pages 2-8 in Zalith.pdf)
 * Features [← KEMBALI], Bold Title, Subtitle, and Screen Action Buttons
 */
@Composable
fun NeobrutalSubHeader(
    currentKey: TitledNavKey?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onResetSettings: (() -> Unit)? = null,
    onSaveSettings: (() -> Unit)? = null,
    onRefreshDownload: (() -> Unit)? = null,
    onImportMrpack: (() -> Unit)? = null,
    onCreateInstance: (() -> Unit)? = null,
    onAddAccount: (() -> Unit)? = null,
    onImportJar: (() -> Unit)? = null,
    onExploreMods: (() -> Unit)? = null,
    onImportFile: (() -> Unit)? = null,
    onNewFolder: (() -> Unit)? = null
) {
    val (titleText, subtitleText) = when (currentKey) {
        is NestedNavKey.Settings -> "PENGATURAN" to "Sesuaikan mesin, alokasi memori, dan kontrol game Anda"
        is NestedNavKey.Download -> "DOWNLOAD & INSTALASI" to "Unduh versi Vanilla, Fabric, Forge, dan Quilt"
        is NormalNavKey.VersionsManager -> "PROFIL INSTANCE" to "Kelola versi Minecraft dan modpack terpasang"
        is NormalNavKey.AccountManager -> "PROFIL AKUN" to "Kelola akun Minecraft (Microsoft & Offline)"
        is NestedNavKey.VersionSettings, is NormalNavKey.Mods -> "MODS & LOADER" to "Kelola Fabric/Forge loader, pasang mod, dan atur resource pack"
        is NormalNavKey.FileSelector, is NormalNavKey.FileManager -> "FILE MANAGER" to "Akses langsung ke direktori file dan dokumen game"
        is NormalNavKey.Multiplayer -> "MULTIPLAYER & LAN" to "Terracotta P2P Online Multiplayer"
        is NormalNavKey.License -> "LISENSI & HUKUM" to "Lisensi Open Source dan Informasi Hak Cipta"
        is NestedNavKey.VersionExport -> "EKSPOR PROFIL" to "Ekspor instance menjadi paket modpack .mrpack / .zip"
        else -> {
            val resTitle = currentKey?.title?.let { resolveAndroidString(it).text } ?: "ZALITH LAUNCHER"
            resTitle.uppercase() to "Navigasi sistem Zalith-OE"
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // [← KEMBALI] Button
        NeobrutalButton(
            onClick = onBack,
            backgroundColor = Color.White,
            shadowOffset = 2.dp,
            cornerRadius = 8.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "← KEMBALI",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Title & Subtitle
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = titleText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitleText,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = NeobrutalTokens.TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Right-hand Screen Actions
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (currentKey) {
                is NestedNavKey.Settings -> {
                    if (onResetSettings != null) {
                        NeobrutalButton(
                            onClick = onResetSettings,
                            backgroundColor = Color.White,
                            shadowOffset = 2.dp,
                            cornerRadius = 6.dp
                        ) {
                            Text(
                                text = "RESET",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    if (onSaveSettings != null) {
                        NeobrutalButton(
                            onClick = onSaveSettings,
                            backgroundColor = NeobrutalTokens.AccentPrimary,
                            shadowOffset = 2.dp,
                            cornerRadius = 6.dp
                        ) {
                            Text(
                                text = "SIMPAN PERUBAHAN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                is NestedNavKey.Download -> {
                    NeobrutalPillBadge(
                        text = "SUMBER RESMI • ONLINE",
                        backgroundColor = Color.White,
                        textColor = Color.Black,
                        borderWidth = 1.5.dp
                    )
                    if (onRefreshDownload != null) {
                        NeobrutalButton(
                            onClick = onRefreshDownload,
                            backgroundColor = NeobrutalTokens.AccentMint,
                            shadowOffset = 2.dp,
                            cornerRadius = 6.dp
                        ) {
                            Text(
                                text = "SEGARKAN LIST",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                is NormalNavKey.VersionsManager -> {
                    if (onImportMrpack != null) {
                        NeobrutalButton(
                            onClick = onImportMrpack,
                            backgroundColor = Color.White,
                            shadowOffset = 2.dp,
                            cornerRadius = 6.dp
                        ) {
                            Text(
                                text = "IMPOR .MRPACK",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    if (onCreateInstance != null) {
                        NeobrutalButton(
                            onClick = onCreateInstance,
                            backgroundColor = NeobrutalTokens.AccentPrimary,
                            shadowOffset = 2.dp,
                            cornerRadius = 6.dp
                        ) {
                            Text(
                                text = "+ BUAT INSTANCE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                is NormalNavKey.AccountManager -> {
                    if (onAddAccount != null) {
                        NeobrutalButton(
                            onClick = onAddAccount,
                            backgroundColor = NeobrutalTokens.AccentPrimary,
                            shadowOffset = 2.dp,
                            cornerRadius = 6.dp
                        ) {
                            Text(
                                text = "+ TAMBAH AKUN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                is NestedNavKey.VersionSettings, is NormalNavKey.Mods -> {
                    if (onImportJar != null) {
                        NeobrutalButton(
                            onClick = onImportJar,
                            backgroundColor = Color.White,
                            shadowOffset = 2.dp,
                            cornerRadius = 6.dp
                        ) {
                            Text(
                                text = "IMPOR FILE .JAR",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    if (onExploreMods != null) {
                        NeobrutalButton(
                            onClick = onExploreMods,
                            backgroundColor = NeobrutalTokens.AccentPrimary,
                            shadowOffset = 2.dp,
                            cornerRadius = 6.dp
                        ) {
                            Text(
                                text = "+ JELAJAHI MOD",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                is NormalNavKey.FileSelector, is NormalNavKey.FileManager -> {
                    if (onImportFile != null) {
                        NeobrutalButton(
                            onClick = onImportFile,
                            backgroundColor = Color.White,
                            shadowOffset = 2.dp,
                            cornerRadius = 6.dp
                        ) {
                            Text(
                                text = "IMPOR FILE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    if (onNewFolder != null) {
                        NeobrutalButton(
                            onClick = onNewFolder,
                            backgroundColor = NeobrutalTokens.AccentPrimary,
                            shadowOffset = 2.dp,
                            cornerRadius = 6.dp
                        ) {
                            Text(
                                text = "+ FOLDER BARU",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun NeobrutalPillTab(
    text: String,
    isActive: Boolean,
    activeColor: Color = NeobrutalTokens.AccentPrimary,
    onClick: () -> Unit
) {
    NeobrutalButton(
        onClick = onClick,
        backgroundColor = if (isActive) activeColor else Color.White,
        shadowOffset = if (isActive) 2.dp else 1.5.dp,
        cornerRadius = NeobrutalTokens.RadiusPill
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 5.dp)
        )
    }
}
