/*
 * SA Launcher - Neobrutalism Accounts Screen
 * Implements Page 5 of Zalith.pdf exactly
 */

package com.movtery.zalithlauncher.ui.theme.neobrutal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.movtery.zalithlauncher.game.account.Account
import com.movtery.zalithlauncher.game.account.localLogin
import com.movtery.zalithlauncher.game.account.AccountsManager
import com.movtery.zalithlauncher.game.account.getAccountTypeName
import com.movtery.zalithlauncher.ui.components.SkinPreview3D
import com.movtery.zalithlauncher.ui.screens.content.elements.PlayerFace
import kotlinx.coroutines.launch

@Composable
fun NeobrutalAccountsScreen(
    onMicrosoftLogin: () -> Unit = {},
    onUploadSkin: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val currentAccount by AccountsManager.currentAccountFlow.collectAsStateWithLifecycle()
    val allAccounts by AccountsManager.accountsFlow.collectAsStateWithLifecycle()

    var showOfflineDialog by remember { mutableStateOf(false) }
    var offlineUsernameInput by remember { mutableStateOf("") }
    var showSwitchAccountDialog by remember { mutableStateOf(false) }

    var isSlimModel by remember { mutableStateOf(false) }
    var isCapeEnabled by remember { mutableStateOf(false) }

    val hasCape = currentAccount != null && currentAccount!!.hasSkinFile && currentAccount!!.getCapeFile().exists() && isCapeEnabled

    // Dialog for Offline Account Login
    if (showOfflineDialog) {
        NeobrutalDialogBox(
            title = "TAMBAH AKUN OFFLINE",
            onDismiss = { showOfflineDialog = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Masukkan nama player (karakter A-Z, 0-9, garis bawah):",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                TextField(
                    value = offlineUsernameInput,
                    onValueChange = { offlineUsernameInput = it },
                    placeholder = { Text("Nama pengguna...", fontSize = 11.sp, color = NeobrutalTokens.TextMuted) },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, Color.Black, RoundedCornerShape(6.dp))
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NeobrutalButton(
                        onClick = { showOfflineDialog = false },
                        modifier = Modifier.weight(1f),
                        backgroundColor = Color.White,
                        shadowOffset = 2.dp,
                        cornerRadius = 6.dp
                    ) {
                        Text("BATAL", fontSize = 10.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(vertical = 6.dp))
                    }

                    NeobrutalButton(
                        onClick = {
                            if (offlineUsernameInput.isNotBlank()) {
                                coroutineScope.launch {
                                    localLogin(offlineUsernameInput.trim(), null)
                                    showOfflineDialog = false
                                    offlineUsernameInput = ""
                                }
                            }
                        },
                        modifier = Modifier.weight(1.2f),
                        backgroundColor = NeobrutalTokens.AccentPrimary,
                        shadowOffset = 2.dp,
                        cornerRadius = 6.dp
                    ) {
                        Text("SIMPAN AKUN", fontSize = 10.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(vertical = 6.dp))
                    }
                }
            }
        }
    }

    // Switch Account Selection Dialog
    if (showSwitchAccountDialog) {
        NeobrutalDialogBox(
            title = "PILIH AKUN LAIN",
            onDismiss = { showSwitchAccountDialog = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allAccounts.forEach { acc ->
                    val isCur = acc.uniqueUUID == currentAccount?.uniqueUUID
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isCur) Color(0xFFFFFBEB) else Color.White, RoundedCornerShape(6.dp))
                            .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
                            .clickable {
                                AccountsManager.setCurrentAccount(acc)
                                showSwitchAccountDialog = false
                            }
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = acc.username, fontSize = 11.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
                            if (isCur) {
                                NeobrutalPillBadge(text = "AKTIF", backgroundColor = NeobrutalTokens.AccentMint, textColor = Color(0xFF166534), borderWidth = 1.dp)
                            }
                        }
                    }
                }
            }
        }
    }

    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // COLUMN 1: PROFIL PENGGUNA (Page 5 Left Column)
        NeobrutalWindowBox(
            title = "[USER] PROFIL PENGGUNA",
            headerColor = NeobrutalTokens.AccentSecondary,
            headerTextColor = Color.White,
            statusPill = if (currentAccount != null) "TERDAFTAR" else "BELUM LOGIN",
            statusPillColor = if (currentAccount != null) NeobrutalTokens.AccentMint else NeobrutalTokens.AccentPink,
            modifier = Modifier
                .weight(1.1f)
                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // User Avatar and info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .background(Color(0xFFE6F3F7), RoundedCornerShape(10.dp))
                            .border(2.5.dp, Color.Black, RoundedCornerShape(10.dp))
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val cur = currentAccount
                        if (cur != null) {
                            PlayerFace(account = cur, avatarSize = 60.dp, modifier = Modifier.fillMaxSize())
                        } else {
                            Text(text = "[USER]", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentAccount?.username ?: "Belum Login",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            NeobrutalPillBadge(
                                text = if (currentAccount != null) "TERVERIFIKASI" else "OFFLINE",
                                backgroundColor = if (currentAccount != null) NeobrutalTokens.AccentMint else Color(0xFFF3F4F6),
                                textColor = if (currentAccount != null) Color(0xFF166534) else Color(0xFF6B7280),
                                borderWidth = 1.dp
                            )
                        }

                        Text(
                            text = "Tipe: ${currentAccount?.let { getAccountTypeName(it) } ?: "Belum Ada Akun"}",
                            fontSize = 9.sp,
                            color = NeobrutalTokens.TextMuted,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "UUID: ${currentAccount?.uniqueUUID?.take(18) ?: "Tidak ada"}",
                            fontSize = 8.5.sp,
                            color = NeobrutalTokens.TextMuted
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        NeobrutalPillBadge(
                            text = if (hasCape) "Cape: ON" else "Cape: OFF",
                            backgroundColor = if (hasCape) Color(0xFFF0FDF4) else Color(0xFFF3F4F6),
                            textColor = if (hasCape) Color(0xFF166534) else Color(0xFF6B7280),
                            borderWidth = 1.dp
                        )
                    }
                }

                // Action Buttons for current account
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    NeobrutalButton(
                        onClick = { /* Refresh/Sync account */ },
                        modifier = Modifier.weight(1f),
                        backgroundColor = Color.White,
                        shadowOffset = 2.dp,
                        cornerRadius = 6.dp
                    ) {
                        Text("SINKRONKAN", fontSize = 8.5.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(vertical = 5.dp))
                    }

                    NeobrutalButton(
                        onClick = { showSwitchAccountDialog = true },
                        modifier = Modifier.weight(1f),
                        backgroundColor = Color.White,
                        shadowOffset = 2.dp,
                        cornerRadius = 6.dp
                    ) {
                        Text("GANTI AKUN", fontSize = 8.5.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(vertical = 5.dp))
                    }

                    NeobrutalButton(
                        onClick = {
                            currentAccount?.let { AccountsManager.deleteAccount(it) }
                        },
                        modifier = Modifier.weight(1f),
                        backgroundColor = Color(0xFFFFF1F2),
                        shadowOffset = 2.dp,
                        cornerRadius = 6.dp
                    ) {
                        Text("KELUAR", fontSize = 8.5.sp, fontWeight = FontWeight.Black, color = Color(0xFFE11D48), modifier = Modifier.padding(vertical = 5.dp))
                    }
                }

                Spacer(modifier = Modifier.weight(1f, fill = false))

                // Bottom Sub-box: TAMBAH AKUN BARU
                NeobrutalWindowBox(
                    title = "[+] TAMBAH AKUN BARU",
                    headerColor = Color(0xFFFEF3C7),
                    headerTextColor = Color.Black,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        NeobrutalButton(
                            onClick = onMicrosoftLogin,
                            modifier = Modifier.weight(1f),
                            backgroundColor = Color(0xFFEFF6FF),
                            shadowOffset = 2.dp,
                            cornerRadius = 6.dp
                        ) {
                            Text(
                                text = "+ MICROSOFT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF1E40AF),
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }

                        NeobrutalButton(
                            onClick = { showOfflineDialog = true },
                            modifier = Modifier.weight(1f),
                            backgroundColor = NeobrutalTokens.AccentPrimary,
                            shadowOffset = 2.dp,
                            cornerRadius = 6.dp
                        ) {
                            Text(
                                text = "+ OFFLINE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // COLUMN 2: SKIN & CAPE // KUSTOMISASI (Page 5 Right Column)
        NeobrutalWindowBox(
            title = "[CUSTOM] SKIN & CAPE // KUSTOMISASI",
            headerColor = NeobrutalTokens.AccentMint,
            headerTextColor = Color.Black,
            statusPill = "PREVIEW 3D",
            statusPillColor = Color.White,
            modifier = Modifier
                .weight(1.4f)
                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Skin Preview 3D Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .background(Color(0xFFF9FAFB), RoundedCornerShape(8.dp))
                        .border(2.dp, Color.Black, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    val curAccount = currentAccount
                    val skinFile = remember(curAccount) {
                        curAccount?.getSkinFile()?.takeIf { it.exists() }
                    }
                    val capeFile = remember(curAccount) {
                        curAccount?.getCapeFile()?.takeIf { it.exists() }
                    }
                    if (curAccount != null) {
                        SkinPreview3D(
                            skinFile = skinFile,
                            capeFile = capeFile,
                            modelType = curAccount.skinModelType,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            text = "Skin Model 64×64\nLogin untuk melihat preview skin",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeobrutalTokens.TextMuted
                        )
                    }
                }

                // Buttons under preview
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    NeobrutalButton(
                        onClick = onUploadSkin,
                        modifier = Modifier.weight(1.3f),
                        backgroundColor = Color.White,
                        shadowOffset = 2.dp,
                        cornerRadius = 6.dp
                    ) {
                        Text(
                            text = "UNGGAH DARI STORAGE",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(vertical = 5.dp)
                        )
                    }

                    NeobrutalButton(
                        onClick = { /* Reset skin */ },
                        modifier = Modifier.weight(1f),
                        backgroundColor = Color.White,
                        shadowOffset = 2.dp,
                        cornerRadius = 6.dp
                    ) {
                        Text(
                            text = "RESET DEFAULT",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(vertical = 5.dp)
                        )
                    }
                }

                // Options: Model Type & Cape Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Arm Thickness Radio
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Tipe Model:", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))

                        RadioChoicePill(
                            label = "Classic 4px",
                            isSelected = !isSlimModel,
                            onSelect = { isSlimModel = false }
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        RadioChoicePill(
                            label = "Slim 3px",
                            isSelected = isSlimModel,
                            onSelect = { isSlimModel = true }
                        )
                    }

                    // Cape Toggle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { isCapeEnabled = !isCapeEnabled }
                    ) {
                        Text(text = "Cape Aktif:", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .background(if (isCapeEnabled) NeobrutalTokens.AccentMint else Color(0xFFF3F4F6), RoundedCornerShape(4.dp))
                                .border(1.5.dp, Color.Black, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isCapeEnabled) "✓ ON" else "X OFF",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f, fill = false))

                // Bottom Primary Action: TERAPKAN SKIN KE GAME
                NeobrutalButton(
                    onClick = { /* Apply skin */ },
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = NeobrutalTokens.AccentPrimary,
                    shadowOffset = 2.dp,
                    cornerRadius = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 7.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "▶ TERAPKAN SKIN KE GAME",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RadioChoicePill(
    label: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(if (isSelected) NeobrutalTokens.AccentOrange else Color.White, RoundedCornerShape(4.dp))
            .border(1.5.dp, Color.Black, RoundedCornerShape(4.dp))
            .clickable { onSelect() }
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            fontSize = 8.sp,
            fontWeight = FontWeight.Black,
            color = Color.Black
        )
    }
}

@Composable
private fun NeobrutalDialogBox(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(360.dp)
                .background(Color.White, RoundedCornerShape(10.dp))
                .border(2.5.dp, Color.Black, RoundedCornerShape(10.dp))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NeobrutalTokens.AccentPrimary, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .border(1.dp, Color.Black, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Black)
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(Color.White, CircleShape)
                            .border(1.5.dp, Color.Black, CircleShape)
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "X", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.Black)
                    }
                }
                content()
            }
        }
    }
}
