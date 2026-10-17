/*
 * SA Launcher - Neobrutalism Instances Screen
 * Implements Page 4 of Zalith.pdf exactly
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.movtery.zalithlauncher.setting.unit.getOrMin
import com.movtery.zalithlauncher.game.version.installed.Version
import com.movtery.zalithlauncher.game.version.installed.VersionsManager
import com.movtery.zalithlauncher.setting.AllSettings
import java.io.File

@Composable
fun NeobrutalInstancesScreen(
    onCreateInstance: () -> Unit = {},
    onImportMrpack: () -> Unit = {},
    onExportInstance: (Version) -> Unit = {},
    onDuplicateInstance: (Version) -> Unit = {},
    onDeleteInstance: (Version) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val allVersions by VersionsManager.versions.collectAsStateWithLifecycle()
    val activeVersion by VersionsManager.currentVersion.collectAsStateWithLifecycle()

    var selectedVersion by remember(allVersions, activeVersion) {
        mutableStateOf(activeVersion ?: allVersions.firstOrNull())
    }

    var instanceName by remember(selectedVersion) {
        mutableStateOf(selectedVersion?.getVersionName() ?: "")
    }

    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // COLUMN 1: DAFTAR INSTANCE (Page 4 Left Column)
        NeobrutalWindowBox(
            title = "[PROFIL] DAFTAR INSTANCE",
            headerColor = NeobrutalTokens.AccentSecondary,
            headerTextColor = Color.White,
            statusPill = "${allVersions.size} TOTAL",
            statusPillColor = NeobrutalTokens.AccentMint,
            modifier = Modifier
                .weight(1.1f)
                .fillMaxHeight()
        ) {
            if (allVersions.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "BELUM ADA INSTANCE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Unduh atau buat instance Minecraft baru.",
                        fontSize = 9.sp,
                        color = NeobrutalTokens.TextMuted
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    NeobrutalButton(
                        onClick = onCreateInstance,
                        backgroundColor = NeobrutalTokens.AccentPrimary,
                        shadowOffset = 2.dp,
                        cornerRadius = 6.dp
                    ) {
                        Text(
                            text = "+ BUAT INSTANCE BARU",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(allVersions) { version ->
                        val isActive = activeVersion?.getVersionName() == version.getVersionName()
                        val isSelected = selectedVersion?.getVersionName() == version.getVersionName()

                        InstanceCardItem(
                            version = version,
                            isActive = isActive,
                            isSelected = isSelected,
                            onSelectForDetail = { selectedVersion = version },
                            onSetActive = {
                                VersionsManager.saveCurrentVersion(version.getVersionName())
                                selectedVersion = version
                            }
                        )
                    }
                }
            }
        }

        // COLUMN 2: DETAIL // INSTANCE (Page 4 Right Column)
        NeobrutalWindowBox(
            title = "[DETAIL] INSTANCE: ${selectedVersion?.getVersionName() ?: "PILIH INSTANCE"}",
            headerColor = NeobrutalTokens.AccentMint,
            headerTextColor = Color.Black,
            statusPill = if (activeVersion?.getVersionName() == selectedVersion?.getVersionName()) "AKTIF" else "SIAP",
            statusPillColor = if (activeVersion?.getVersionName() == selectedVersion?.getVersionName()) NeobrutalTokens.AccentPrimary else Color.White,
            modifier = Modifier
                .weight(1.4f)
                .fillMaxHeight()
        ) {
            if (selectedVersion == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Pilih instance di sebelah kiri untuk melihat detail.", fontSize = 10.sp, color = NeobrutalTokens.TextMuted)
                }
            } else {
                val currentSel = selectedVersion!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Top Stat Sub-boxes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatBoxItem(label = "MODS", value = "24 MODS", modifier = Modifier.weight(1f))
                        StatBoxItem(label = "STORAGE", value = "4.2 GB", modifier = Modifier.weight(1f))
                        StatBoxItem(label = "PLAYTIME", value = "0 JAM", modifier = Modifier.weight(1f))
                    }

                    // Instance Name Field
                    Column {
                        Text(text = "NAMA INSTANCE", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        Spacer(modifier = Modifier.height(3.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(6.dp))
                                .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = instanceName,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }
                    }

                    // Configuration Options
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ConfigDropdownItem(
                            label = "LOADER",
                            value = "Fabric Loader 0.15.11 ▾",
                            modifier = Modifier.weight(1f)
                        )
                        ConfigDropdownItem(
                            label = "RENDERER",
                            value = "${AllSettings.rendererV2.state.name} ▾",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ConfigDropdownItem(
                            label = "JAVA RUNTIME",
                            value = "OpenJDK 21 (ARM64) ▾",
                            modifier = Modifier.weight(1f)
                        )
                        ConfigDropdownItem(
                            label = "ALOKASI RAM",
                            value = "${AllSettings.ramAllocation.getOrMin()} MB ▾",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f, fill = false))

                    // Action Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        NeobrutalButton(
                            onClick = { onDuplicateInstance(currentSel) },
                            modifier = Modifier.weight(1f),
                            backgroundColor = Color.White,
                            shadowOffset = 2.dp,
                            cornerRadius = 6.dp
                        ) {
                            Text(
                                text = "DUPLIKAT",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }

                        NeobrutalButton(
                            onClick = { onExportInstance(currentSel) },
                            modifier = Modifier.weight(1f),
                            backgroundColor = Color.White,
                            shadowOffset = 2.dp,
                            cornerRadius = 6.dp
                        ) {
                            Text(
                                text = "EKSPOR",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }

                        NeobrutalButton(
                            onClick = { onDeleteInstance(currentSel) },
                            modifier = Modifier.weight(1f),
                            backgroundColor = Color(0xFFFFF1F2),
                            shadowOffset = 2.dp,
                            cornerRadius = 6.dp
                        ) {
                            Text(
                                text = "HAPUS",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFE11D48),
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }

                        NeobrutalButton(
                            onClick = {
                                VersionsManager.saveCurrentVersion(currentSel.getVersionName())
                            },
                            modifier = Modifier.weight(1.3f),
                            backgroundColor = NeobrutalTokens.AccentPrimary,
                            shadowOffset = 2.dp,
                            cornerRadius = 6.dp
                        ) {
                            Text(
                                text = "SIMPAN PROFIL",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InstanceCardItem(
    version: Version,
    isActive: Boolean,
    isSelected: Boolean,
    onSelectForDetail: () -> Unit,
    onSetActive: () -> Unit
) {
    val bgColor = if (isSelected) Color(0xFFFFFBEB) else Color.White
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(8.dp))
            .border(if (isSelected) 2.dp else 1.5.dp, Color.Black, RoundedCornerShape(8.dp))
            .clickable { onSelectForDetail() }
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
                    .background(Color.White, CircleShape)
                    .border(2.dp, Color.Black, CircleShape)
                    .clickable { onSetActive() },
                contentAlignment = Alignment.Center
            ) {
                if (isActive) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color.Black, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = version.getVersionName(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Fabric 0.15.11 • Java 21",
                    fontSize = 8.5.sp,
                    color = NeobrutalTokens.TextMuted,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            if (isActive) {
                NeobrutalPillBadge(
                    text = "AKTIF",
                    backgroundColor = NeobrutalTokens.AccentMint,
                    textColor = Color(0xFF166534),
                    borderWidth = 1.dp
                )
            } else {
                NeobrutalButton(
                    onClick = onSetActive,
                    backgroundColor = Color.White,
                    shadowOffset = 1.dp,
                    cornerRadius = 4.dp
                ) {
                    Text(
                        text = "PILIH",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatBoxItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(6.dp))
            .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
            .padding(vertical = 5.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, fontSize = 7.5.sp, fontWeight = FontWeight.Bold, color = NeobrutalTokens.TextMuted)
            Text(text = value, fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.Black)
        }
    }
}

@Composable
private fun ConfigDropdownItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = label, fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color.Black)
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(6.dp))
                .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
                .padding(horizontal = 6.dp, vertical = 5.dp)
        ) {
            Text(
                text = value,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
