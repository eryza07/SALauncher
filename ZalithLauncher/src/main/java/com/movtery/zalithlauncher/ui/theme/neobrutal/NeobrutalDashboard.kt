/*
 * SA Launcher - Neobrutalism Main Dashboard
 * Accurately implements mockup WA_1792161398481.png & UI/UX PRD specifications
 * Connected seamlessly with Zalith core engine features
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalContext
import com.movtery.zalithlauncher.game.account.Account
import com.movtery.zalithlauncher.game.account.AccountsManager
import com.movtery.zalithlauncher.game.account.getAccountTypeName
import com.movtery.zalithlauncher.game.version.installed.Version
import com.movtery.zalithlauncher.game.version.installed.VersionsManager
import com.movtery.zalithlauncher.setting.AllSettings
import com.movtery.zalithlauncher.setting.unit.getOrMin
import com.movtery.zalithlauncher.ui.screens.content.elements.PlayerFace
import com.movtery.zalithlauncher.utils.platform.bytesToMB
import com.movtery.zalithlauncher.utils.platform.calculateAdaptiveHeap
import com.movtery.zalithlauncher.utils.platform.getTotalMemory

@Composable
fun NeobrutalDashboard(
    onLaunchGame: (Version?) -> Unit,
    toAccountManageScreen: () -> Unit,
    toVersionManageScreen: () -> Unit,
    toVersionSettingsScreen: () -> Unit,
    toDownloadScreen: () -> Unit,
    toDownloadModScreen: () -> Unit = toDownloadScreen,
    toSettingsScreen: () -> Unit,
    showTopBar: Boolean = false,
    modifier: Modifier = Modifier
) {
    val currentAccount by AccountsManager.currentAccountFlow.collectAsStateWithLifecycle()
    val currentVersion by VersionsManager.currentVersion.collectAsStateWithLifecycle()
    val allVersions by VersionsManager.versions.collectAsStateWithLifecycle()

    var isEntered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isEntered = true
    }

    val topBarOffset by animateDpAsState(
        targetValue = if (isEntered) 0.dp else (-20).dp,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMediumLow),
        label = "TopBarOffset"
    )
    val leftColumnOffset by animateDpAsState(
        targetValue = if (isEntered) 0.dp else (-25).dp,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMediumLow),
        label = "LeftColumnOffset"
    )
    val rightColumnOffset by animateDpAsState(
        targetValue = if (isEntered) 0.dp else 25.dp,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMediumLow),
        label = "RightColumnOffset"
    )
    val alphaAnim by animateFloatAsState(
        targetValue = if (isEntered) 1f else 0f,
        animationSpec = tween(durationMillis = 280),
        label = "DashboardAlpha"
    )

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // 1. TOP NAVIGATION BAR (As depicted in Mockup)
            if (showTopBar) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = topBarOffset)
                        .graphicsLayer { alpha = alphaAnim }
                ) {
                    NeobrutalTopBar(
                        currentAccount = currentAccount,
                        onBerandaClick = { /* Already at home */ },
                        onDownloadClick = toDownloadScreen,
                        onModsClick = toDownloadModScreen,
                        onSettingsClick = toSettingsScreen,
                        onAccountClick = toAccountManageScreen
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // 2. MAIN BODY (Split into Left Column & Right Column)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // LEFT COLUMN (User Profile, Stats, Active Version)
                Column(
                    modifier = Modifier
                        .weight(0.42f)
                        .fillMaxHeight()
                        .offset(x = leftColumnOffset)
                        .graphicsLayer { alpha = alphaAnim }
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Left 1: User Profile Box // Slot #01
                    UserProfileCard(
                        account = currentAccount,
                        onSwitchAccount = toAccountManageScreen,
                        onEditSkin = toAccountManageScreen
                    )

                    // Left 2: Activity & Playtime Card
                    PlaytimeActivityCard()

                    // Left 3: Current Active Version Box (only show if active version exists)
                    if (currentVersion != null) {
                        ActiveVersionOverviewCard(
                            version = currentVersion,
                            onDetailClick = toVersionSettingsScreen,
                            onDownloadClick = toDownloadScreen
                        )
                    }
                }

                // RIGHT COLUMN (Hero Play Action, Fast Instances, Bottom Info)
                Column(
                    modifier = Modifier
                        .weight(0.58f)
                        .fillMaxHeight()
                        .offset(x = rightColumnOffset)
                        .graphicsLayer { alpha = alphaAnim },
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Right 1: Massive Hero "MAINKAN" Card
                    HeroPlayCard(
                        version = currentVersion,
                        onPlayClick = { onLaunchGame(currentVersion) },
                        onDownloadClick = toDownloadScreen
                    )

                    // Right 2: Fast Instance Selector Card
                    FastInstanceSelectorCard(
                        currentVersion = currentVersion,
                        versionsList = allVersions,
                        onSelectVersion = { version ->
                            VersionsManager.saveCurrentVersion(version.getVersionName())
                        },
                        onManageAllClick = toVersionManageScreen,
                        onCreateInstanceClick = toDownloadScreen
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Right 3: Bottom System & Runtime Info Bar
                    BottomRuntimeInfoBar()
                }
            }
        }
    }
}

/**
 * Top Navigation Bar with retro-pill tabs and status pill
 */
@Composable
private fun NeobrutalTopBar(
    currentAccount: Account?,
    onBerandaClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onModsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAccountClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Title Brand Pill
        NeobrutalPillButton(
            text = "SA LAUNCHER",
            backgroundColor = NeobrutalTokens.AccentPrimary,
            textColor = Color.Black,
            onClick = onBerandaClick
        )

        Spacer(modifier = Modifier.width(10.dp))

        // Center Navigation Tabs
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            NeobrutalPillButton(
                text = "BERANDA",
                backgroundColor = NeobrutalTokens.AccentPrimary,
                textColor = Color.Black,
                onClick = onBerandaClick
            )

            NeobrutalPillButton(
                text = "DOWNLOAD",
                backgroundColor = Color.White,
                textColor = Color.Black,
                onClick = onDownloadClick
            )

            NeobrutalPillButton(
                text = "MODS & LOADER",
                backgroundColor = Color.White,
                textColor = Color.Black,
                onClick = onModsClick
            )

            NeobrutalPillButton(
                text = "PENGATURAN",
                backgroundColor = NeobrutalTokens.AccentPink,
                textColor = Color.Black,
                onClick = onSettingsClick
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // User Account Pill
        val accountName = currentAccount?.username
        NeobrutalPillButton(
            text = if (accountName != null) "$accountName [USER]" else "+ LOGIN AKUN",
            backgroundColor = if (accountName != null) Color.White else NeobrutalTokens.AccentPrimary,
            textColor = Color.Black,
            onClick = onAccountClick
        )
    }
}

/**
 * Left Card 1: User Profile Window Box
 */
@Composable
private fun UserProfileCard(
    account: Account?,
    onSwitchAccount: () -> Unit,
    onEditSkin: () -> Unit
) {
    val username = account?.username ?: "Belum Ada Akun"
    val uuidDisplay = account?.uniqueUUID?.take(18) ?: "Login untuk bermain"
    val authType = account?.let { getAccountTypeName(it) } ?: "BELUM LOGIN"

    val hasCape = account != null && account.hasSkinFile && account.getCapeFile().exists()

    NeobrutalWindowBox(
        title = if (account != null) "PROFIL PENGGUNA // SLOT #01" else "PROFIL PENGGUNA // BELUM LOGIN",
        headerColor = if (account != null) NeobrutalTokens.AccentSecondary else Color(0xFF334155),
        statusPill = if (account != null) "ONLINE" else "BELUM ADA AKUN",
        statusPillColor = if (account != null) NeobrutalTokens.AccentMint else NeobrutalTokens.AccentPink,
        onCloseClick = { /* minimize/collapse */ }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Skin Avatar Box with Neobrutal Border
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .background(Color(0xFFE6F3F7), RoundedCornerShape(10.dp))
                        .border(2.5.dp, Color.Black, RoundedCornerShape(10.dp))
                        .padding(3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (account != null) {
                        PlayerFace(
                            account = account,
                            avatarSize = 56.dp,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(text = "[USER]", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = username,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                        if (account != null) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "[OK]",
                                color = NeobrutalTokens.AccentOrange,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Text(
                        text = if (account != null) "UUID: $uuidDisplay" else uuidDisplay,
                        fontSize = 10.sp,
                        color = NeobrutalTokens.TextMuted,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        NeobrutalPillBadge(
                            text = if (hasCape) "Cape: ON" else "Cape: OFF",
                            backgroundColor = if (hasCape) Color(0xFFF0FDF4) else Color(0xFFF3F4F6),
                            textColor = if (hasCape) Color(0xFF166534) else Color(0xFF6B7280),
                            borderWidth = 1.5.dp
                        )
                        NeobrutalPillBadge(
                            text = authType,
                            backgroundColor = if (account != null) Color(0xFFEFF6FF) else Color(0xFFFFF1F2),
                            textColor = if (account != null) Color(0xFF1E40AF) else Color(0xFFE11D48),
                            borderWidth = 1.5.dp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row
            if (account != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NeobrutalButton(
                        onClick = onSwitchAccount,
                        modifier = Modifier.weight(1f),
                        backgroundColor = Color.White,
                        shadowOffset = 3.dp,
                        cornerRadius = 8.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 7.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "GANTI AKUN", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        }
                    }

                    NeobrutalButton(
                        onClick = onEditSkin,
                        modifier = Modifier.weight(1f),
                        backgroundColor = NeobrutalTokens.AccentPrimary,
                        shadowOffset = 3.dp,
                        cornerRadius = 8.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 7.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "EDIT SKIN", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        }
                    }
                }
            } else {
                NeobrutalButton(
                    onClick = onSwitchAccount,
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = NeobrutalTokens.AccentPrimary,
                    shadowOffset = 3.dp,
                    cornerRadius = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "LOGIN AKUN (MS / OFFLINE / ELY.BY)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Black)
                    }
                }
            }
        }
    }
}

/**
 * Left Card 2: Playtime & Activity Card
 */
@Composable
private fun PlaytimeActivityCard() {
    NeobrutalCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = NeobrutalTokens.CardWhite,
        shadowOffset = NeobrutalTokens.ShadowStandard
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AKTIVITAS & WAKTU BERMAIN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                NeobrutalPillBadge(
                    text = "STATUS: SIAP",
                    backgroundColor = NeobrutalTokens.AccentMint,
                    textColor = Color.Black,
                    borderWidth = 1.5.dp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3 Metric Stat Boxes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                StatSubBox(
                    label = "TOTAL PLAYTIME",
                    value = "0 Jam",
                    bgColor = Color.White,
                    modifier = Modifier.weight(1f)
                )
                StatSubBox(
                    label = "SESI TERAKHIR",
                    value = "0 Menit",
                    valueColor = NeobrutalTokens.AccentSecondary,
                    bgColor = Color.White,
                    modifier = Modifier.weight(1f)
                )
                StatSubBox(
                    label = "STREAK MAIN",
                    value = "0 Hari",
                    bgColor = Color(0xFFFFF9DB),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatSubBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = Color.Black,
    bgColor: Color = Color.White
) {
    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(8.dp))
            .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
            .padding(vertical = 6.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = NeobrutalTokens.TextMuted,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = valueColor,
                maxLines = 1
            )
        }
    }
}

/**
 * Left Card 3: Active Version Overview
 */
@Composable
private fun ActiveVersionOverviewCard(
    version: Version?,
    onDetailClick: () -> Unit,
    onDownloadClick: () -> Unit = onDetailClick
) {
    val hasVersion = version != null
    val targetClick = if (hasVersion) onDetailClick else onDownloadClick
    val versionName = if (hasVersion) version!!.getVersionName() else "BELUM ADA VERSI"
    val subtitle = if (hasVersion) "MobileGL v2.4 • Vulkan Pipe" else "Unduh Minecraft dari menu DOWNLOAD"

    NeobrutalCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = NeobrutalTokens.CardWhite,
        shadowOffset = NeobrutalTokens.ShadowStandard
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(if (hasVersion) NeobrutalTokens.AccentMint else Color(0xFFF3F4F6), RoundedCornerShape(8.dp))
                    .border(2.dp, Color.Black, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (hasVersion) "MC" else "[--]",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "VERSI AKTIF SAAT INI",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (hasVersion) Color(0xFF16A34A) else NeobrutalTokens.TextMuted
                )
                Text(
                    text = versionName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    fontSize = 9.sp,
                    color = NeobrutalTokens.TextMuted
                )
            }

            NeobrutalButton(
                onClick = targetClick,
                backgroundColor = if (hasVersion) NeobrutalTokens.AccentSecondary else NeobrutalTokens.AccentPrimary,
                shadowOffset = 2.5.dp,
                cornerRadius = 8.dp
            ) {
                Text(
                    text = if (hasVersion) "↗ DETAIL" else "+ UNDUH ↗",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = if (hasVersion) Color.White else Color.Black,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * Right Card 1: Massive Hero "MAINKAN" Card
 */
@Composable
private fun HeroPlayCard(
    version: Version?,
    onPlayClick: () -> Unit,
    onDownloadClick: () -> Unit = onPlayClick
) {
    val hasVersion = version != null
    val targetClick = if (hasVersion) onPlayClick else onDownloadClick
    val versionName = if (hasVersion) version!!.getVersionName().uppercase() else "BELUM ADA VERSI"
    val actionText = if (hasVersion) "▶ MAINKAN" else "▶ UNDUH MINECRAFT"
    val statusText = if (hasVersion) "READY TO LAUNCH" else "STATUS: BELUM ADA VERSI"

    NeobrutalCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = if (hasVersion) NeobrutalTokens.AccentPrimary else Color(0xFFFFD166),
        shadowOffset = NeobrutalTokens.ShadowHero,
        borderWidth = NeobrutalTokens.BorderHeavy,
        cornerRadius = 16.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Top Tag
                NeobrutalPillBadge(
                    text = statusText,
                    backgroundColor = Color.Black,
                    textColor = Color.White,
                    borderWidth = 1.5.dp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Massive Hero CTA Button Text
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { targetClick() }
                ) {
                    Text(
                        text = actionText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    NeobrutalPillBadge(
                        text = versionName,
                        backgroundColor = Color.White,
                        textColor = Color.Black,
                        borderWidth = 2.dp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "DIRECT VULKAN PIPELINE • OFFLINE/ONLINE READY",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }

            // Quick Action Box
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(Color.Black, RoundedCornerShape(14.dp))
                    .clickable { targetClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (hasVersion) "▶" else "+",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = NeobrutalTokens.AccentPrimary
                )
            }
        }
    }
}

/**
 * Right Card 2: Fast Instance Selector Card
 */
@Composable
private fun FastInstanceSelectorCard(
    currentVersion: Version?,
    versionsList: List<Version>,
    onSelectVersion: (Version) -> Unit,
    onManageAllClick: () -> Unit,
    onCreateInstanceClick: () -> Unit
) {
    NeobrutalCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = NeobrutalTokens.CardWhite,
        shadowOffset = NeobrutalTokens.ShadowStandard
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "[PROFIL] INSTANCE CEPAT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "KELOLA SEMUA (${versionsList.size}) ↗",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = NeobrutalTokens.AccentSecondary,
                    modifier = Modifier.clickable { onManageAllClick() }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (versionsList.isEmpty()) {
                // Clean empty state card (No fake instances)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF9FAFB), RoundedCornerShape(8.dp))
                        .border(1.5.dp, Color(0xFFD1D5DB), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "BELUM ADA INSTANCE / VERSI TERPASANG",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Silakan unduh Vanilla / Fabric / Forge dari menu DOWNLOAD.",
                                fontSize = 9.sp,
                                color = NeobrutalTokens.TextMuted
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        NeobrutalButton(
                            onClick = onCreateInstanceClick,
                            backgroundColor = NeobrutalTokens.AccentPrimary,
                            shadowOffset = 2.dp,
                            cornerRadius = 6.dp
                        ) {
                            Text(
                                text = "+ UNDUH VERSI ↗",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            } else {
                // Instances List (Horizontal row)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    versionsList.take(4).forEach { version ->
                        val isCurrent = version.getVersionName() == currentVersion?.getVersionName()
                        InstanceItemCard(
                            name = version.getVersionName(),
                            info = if (version.isValid()) version.getVersionSummary() else "Versi Siap",
                            isActive = isCurrent,
                            badgeText = if (isCurrent) "AKTIF" else "SIAP",
                            badgeColor = if (isCurrent) NeobrutalTokens.AccentMint else Color(0xFFF3F4F6),
                            onClick = { onSelectVersion(version) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dashed Border "Create New Instance" Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFAFAFA), RoundedCornerShape(8.dp))
                    .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                    .clickable { onCreateInstanceClick() }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+ BUAT INSTANCE ATAU PASANG DARI FILE (.ZIP / .MRPACK)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
private fun InstanceItemCard(
    name: String,
    info: String,
    isActive: Boolean,
    badgeText: String,
    badgeColor: Color,
    onClick: () -> Unit
) {
    val bgColor = if (isActive) Color(0xFFFFFBEB) else Color.White
    Box(
        modifier = Modifier
            .width(220.dp)
            .background(bgColor, RoundedCornerShape(10.dp))
            .border(2.dp, Color.Black, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Radio Indicator
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(if (isActive) NeobrutalTokens.AccentPrimary else Color.Transparent, CircleShape)
                    .border(2.dp, Color.Black, CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = info,
                    fontSize = 9.sp,
                    color = NeobrutalTokens.TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            NeobrutalPillBadge(
                text = badgeText,
                backgroundColor = badgeColor,
                textColor = Color.Black,
                borderWidth = 1.5.dp
            )
        }
    }
}

/**
 * Bottom System & Runtime Info Bar
 */
@Composable
private fun BottomRuntimeInfoBar() {
    val context = LocalContext.current
    val totalRamMb = remember(context) { runCatching { getTotalMemory(context).bytesToMB().toInt() }.getOrDefault(4096) }
    val allocatedRam = remember { AllSettings.ramAllocation.getOrMin() }
    val adaptiveHeap = remember(context, allocatedRam) {
        runCatching { calculateAdaptiveHeap(context, allocatedRam) }.getOrNull()
    }

    val displayAlloc = adaptiveHeap?.maxHeapMb ?: allocatedRam
    val isMemorySafe = adaptiveHeap == null || !adaptiveHeap.wasCapped
    val statusText = if (isMemorySafe) "MEMORY SAFE: OK" else "ADAPTIVE CAPPED"
    val badgeColor = if (isMemorySafe) NeobrutalTokens.AccentMint else NeobrutalTokens.AccentPrimary

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Runtime Indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(NeobrutalTokens.AccentMint, CircleShape)
                    .border(1.dp, Color.Black, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "RUNTIME: v2.8-STABLE  /  >_ OPENJDK 17 (ARM64)",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        // Memory Safe Indicator (PRD SH-02)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "RAM ALLOC: $displayAlloc / $totalRamMb MB",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.width(6.dp))
            NeobrutalPillBadge(
                text = statusText,
                backgroundColor = badgeColor,
                textColor = Color.Black,
                borderWidth = 1.5.dp
            )
        }
    }
}

/**
 * Pill shaped button
 */
@Composable
private fun NeobrutalPillButton(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    NeobrutalButton(
        onClick = onClick,
        backgroundColor = backgroundColor,
        shadowOffset = 2.5.dp,
        cornerRadius = NeobrutalTokens.RadiusPill
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = textColor,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
