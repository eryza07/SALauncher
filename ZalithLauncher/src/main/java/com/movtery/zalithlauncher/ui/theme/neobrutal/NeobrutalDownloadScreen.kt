/*
 * SA Launcher - Neobrutalism Download Screen
 * Implements Pages 6 & 9 of Zalith.pdf exactly
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
import com.movtery.zalithlauncher.coroutine.TaskSystem
import com.movtery.zalithlauncher.game.version.installed.VersionsManager
import com.movtery.zalithlauncher.setting.AllSettings

data class VersionCatalogItem(
    val id: String,
    val type: String,
    val date: String,
    val size: String,
    val isInstalled: Boolean = false
)

@Composable
fun NeobrutalDownloadScreen(
    onStartDownload: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val tasks by TaskSystem.tasksFlow.collectAsStateWithLifecycle()
    val installedVersions by VersionsManager.versions.collectAsStateWithLifecycle()

    var selectedFilter by remember { mutableStateOf("SEMUA") }
    var searchQuery by remember { mutableStateOf("") }

    // Page 9 Modal Dialog state
    var selectedItemForDownload by remember { mutableStateOf<VersionCatalogItem?>(null) }
    var selectedLoader by remember { mutableStateOf("Fabric Loader") }
    var autoInstallFabricApi by remember { mutableStateOf(true) }
    var createShortcut by remember { mutableStateOf(true) }

    val rawVersions = listOf(
        VersionCatalogItem("1.21.1", "RELEASE", "18 Agu 2024", "78 MB"),
        VersionCatalogItem("1.21", "RELEASE", "13 Jun 2024", "76 MB"),
        VersionCatalogItem("1.20.4", "RELEASE", "07 Des 2023", "72 MB"),
        VersionCatalogItem("1.20.1", "RELEASE", "12 Jun 2023", "70 MB"),
        VersionCatalogItem("1.19.4", "RELEASE", "14 Mar 2023", "68 MB"),
        VersionCatalogItem("1.18.2", "RELEASE", "28 Feb 2022", "64 MB"),
        VersionCatalogItem("1.16.5", "RELEASE", "15 Jan 2021", "60 MB"),
        VersionCatalogItem("1.12.2", "RELEASE", "18 Sep 2017", "50 MB"),
        VersionCatalogItem("1.8.9", "RELEASE", "09 Des 2015", "40 MB"),
        VersionCatalogItem("24w33a", "SNAPSHOT", "14 Agu 2024", "81 MB"),
        VersionCatalogItem("24w32a", "SNAPSHOT", "07 Agu 2024", "80 MB")
    )

    val installedNames = remember(installedVersions) {
        installedVersions.map { it.getVersionName() }.toSet()
    }

    val catalogItems = rawVersions.map { item ->
        val installed = installedNames.any { it.contains(item.id) }
        item.copy(isInstalled = installed)
    }.filter { item ->
        val matchesFilter = when (selectedFilter) {
            "TERPASANG" -> item.isInstalled
            "RELEASE" -> item.type == "RELEASE"
            "SNAPSHOT" -> item.type == "SNAPSHOT"
            else -> true
        }
        val matchesSearch = searchQuery.isBlank() || item.id.contains(searchQuery.trim(), ignoreCase = true)
        matchesFilter && matchesSearch
    }

    // PAGE 9: MODAL POPUP INSTALASI
    if (selectedItemForDownload != null) {
        val target = selectedItemForDownload!!
        androidx.compose.ui.window.Dialog(onDismissRequest = { selectedItemForDownload = null }) {
            Box(
                modifier = Modifier
                    .width(420.dp)
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .border(2.5.dp, Color.Black, RoundedCornerShape(10.dp))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Modal Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NeobrutalTokens.AccentPrimary, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            .border(1.dp, Color.Black, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "UNDUHAN // MINECRAFT ${target.id}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(Color.White, CircleShape)
                                .border(1.5.dp, Color.Black, CircleShape)
                                .clickable { selectedItemForDownload = null },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "X", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        }
                    }

                    // Modal Form Body
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Pilih Loader
                        Text(text = "PILIH LOADER", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Vanilla", "Fabric Loader", "Forge", "NeoForge").forEach { loader ->
                                val isSel = selectedLoader == loader
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(if (isSel) NeobrutalTokens.AccentSecondary else Color.White, RoundedCornerShape(6.dp))
                                        .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
                                        .clickable { selectedLoader = loader }
                                        .padding(vertical = 5.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = loader,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isSel) Color.White else Color.Black,
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        // Loader Version & RAM Dropdowns
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "VERSI LOADER", fontSize = 8.5.sp, fontWeight = FontWeight.Black, color = Color.Black)
                                Spacer(modifier = Modifier.height(2.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White, RoundedCornerShape(6.dp))
                                        .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 5.dp)
                                ) {
                                    Text(text = "0.15.11 (Terbaru) ▾", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "ALOKASI RAM", fontSize = 8.5.sp, fontWeight = FontWeight.Black, color = Color.Black)
                                Spacer(modifier = Modifier.height(2.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White, RoundedCornerShape(6.dp))
                                        .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 5.dp)
                                ) {
                                    Text(text = "${AllSettings.ramAllocation.getOrMin()} MB ▾", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                            }
                        }

                        // Checkboxes
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { autoInstallFabricApi = !autoInstallFabricApi }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(if (autoInstallFabricApi) NeobrutalTokens.AccentMint else Color.White, RoundedCornerShape(4.dp))
                                    .border(1.5.dp, Color.Black, RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (autoInstallFabricApi) Text("✓", fontSize = 9.sp, fontWeight = FontWeight.Black)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pasang Fabric API secara otomatis (Rekomendasi)", fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { createShortcut = !createShortcut }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(if (createShortcut) NeobrutalTokens.AccentMint else Color.White, RoundedCornerShape(4.dp))
                                    .border(1.5.dp, Color.Black, RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (createShortcut) Text("✓", fontSize = 9.sp, fontWeight = FontWeight.Black)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Buat shortcut di Beranda", fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Modal Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            NeobrutalButton(
                                onClick = { selectedItemForDownload = null },
                                modifier = Modifier.weight(1f),
                                backgroundColor = Color.White,
                                shadowOffset = 2.dp,
                                cornerRadius = 6.dp
                            ) {
                                Text("BATAL", fontSize = 9.5.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(vertical = 6.dp))
                            }

                            NeobrutalButton(
                                onClick = {
                                    onStartDownload(target.id)
                                    selectedItemForDownload = null
                                },
                                modifier = Modifier.weight(1.6f),
                                backgroundColor = NeobrutalTokens.AccentPrimary,
                                shadowOffset = 2.dp,
                                cornerRadius = 6.dp
                            ) {
                                Text("▶ MULAI MENGUNDUH (${target.size})", fontSize = 9.5.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(vertical = 6.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // MAIN VIEW (PAGE 6 OF ZALITH.PDF)
    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // COLUMN 1: VERSION CATALOG (Left Column)
        NeobrutalWindowBox(
            title = "[KATALOG] MINECRAFT VERSIONS",
            headerColor = NeobrutalTokens.AccentPrimary,
            headerTextColor = Color.Black,
            statusPill = "${catalogItems.size} TERSEDIA",
            statusPillColor = Color.White,
            modifier = Modifier
                .weight(1.3f)
                .fillMaxHeight()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Filter chips & search bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("SEMUA", "TERPASANG", "RELEASE", "SNAPSHOT").forEach { filter ->
                        val isSel = selectedFilter == filter
                        Box(
                            modifier = Modifier
                                .background(if (isSel) NeobrutalTokens.AccentOrange else Color.White, RoundedCornerShape(4.dp))
                                .border(1.5.dp, Color.Black, RoundedCornerShape(4.dp))
                                .clickable { selectedFilter = filter }
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = filter,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Cari versi...", fontSize = 8.sp, color = NeobrutalTokens.TextMuted) },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        textStyle = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black),
                        modifier = Modifier
                            .weight(1f)
                            .height(28.dp)
                            .border(1.5.dp, Color.Black, RoundedCornerShape(4.dp))
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Catalog list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(catalogItems) { item ->
                        VersionCatalogRowItem(
                            item = item,
                            onDownloadClick = { selectedItemForDownload = item }
                        )
                    }
                }
            }
        }

        // COLUMN 2: ANTREAN INSTALASI & STORAGE (Right Column)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // ANTREAN INSTALASI Box
            NeobrutalWindowBox(
                title = "[QUEUE] ANTREAN INSTALASI",
                headerColor = NeobrutalTokens.AccentSecondary,
                headerTextColor = Color.White,
                statusPill = if (tasks.isNotEmpty()) "MENGUNDUH" else "IDLE",
                statusPillColor = if (tasks.isNotEmpty()) NeobrutalTokens.AccentPrimary else Color(0xFFF3F4F6),
                modifier = Modifier.weight(1.3f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Mengunduh: Minecraft 1.21.1",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )

                    // Neobrutal Progress Bar
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "PROGRESS: 68%", fontSize = 8.5.sp, fontWeight = FontWeight.Black, color = Color.Black)
                            Text(text = "52.4 / 78 MB", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = NeobrutalTokens.TextMuted)
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(14.dp)
                                .background(Color(0xFFE5E7EB), RoundedCornerShape(4.dp))
                                .border(1.5.dp, Color.Black, RoundedCornerShape(4.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(0.68f)
                                    .background(NeobrutalTokens.AccentPrimary, RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp))
                                    .border(1.dp, Color.Black, RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp))
                            )
                        }
                    }

                    // Checklist steps
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF9FAFB), RoundedCornerShape(6.dp))
                            .border(1.dp, Color.Black, RoundedCornerShape(6.dp))
                            .padding(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        StepCheckItem(text = "Metadata Versi", isDone = true)
                        StepCheckItem(text = "Libraries & Dependencies", isDone = true)
                        StepCheckItem(text = "Minecraft Client Jar (68%)", isRunning = true)
                        StepCheckItem(text = "Asset Index & Sounds")
                        StepCheckItem(text = "Verifikasi Hash SHA-1")
                    }

                    Spacer(modifier = Modifier.weight(1f, fill = false))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        NeobrutalButton(
                            onClick = { /* Pause */ },
                            modifier = Modifier.weight(1f),
                            backgroundColor = Color.White,
                            shadowOffset = 2.dp,
                            cornerRadius = 6.dp
                        ) {
                            Text("JEDA", fontSize = 8.5.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(vertical = 5.dp))
                        }

                        NeobrutalButton(
                            onClick = { /* Cancel */ },
                            modifier = Modifier.weight(1f),
                            backgroundColor = Color(0xFFFFF1F2),
                            shadowOffset = 2.dp,
                            cornerRadius = 6.dp
                        ) {
                            Text("BATALKAN", fontSize = 8.5.sp, fontWeight = FontWeight.Black, color = Color(0xFFE11D48), modifier = Modifier.padding(vertical = 5.dp))
                        }
                    }
                }
            }

            // STORAGE PERANGKAT Box
            NeobrutalWindowBox(
                title = "[DISK] STORAGE PERANGKAT",
                headerColor = Color(0xFFFEF3C7),
                headerTextColor = Color.Black,
                modifier = Modifier.weight(0.7f)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Storage Tersedia: 18.4 GB",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Ruang penyimpanan aman dan cukup untuk instalasi.",
                        fontSize = 8.5.sp,
                        color = NeobrutalTokens.TextMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .background(Color(0xFFE5E7EB), RoundedCornerShape(4.dp))
                            .border(1.5.dp, Color.Black, RoundedCornerShape(4.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.35f)
                                .background(NeobrutalTokens.AccentMint, RoundedCornerShape(2.dp))
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VersionCatalogRowItem(
    item: VersionCatalogItem,
    onDownloadClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(6.dp))
            .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
            .padding(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Minecraft ${item.id}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    NeobrutalPillBadge(
                        text = item.type,
                        backgroundColor = if (item.type == "RELEASE") Color(0xFFEFF6FF) else Color(0xFFFEF3C7),
                        textColor = if (item.type == "RELEASE") Color(0xFF1E40AF) else Color(0xFFB45309),
                        borderWidth = 1.dp
                    )
                }
                Text(
                    text = "${item.date} • ${item.size}",
                    fontSize = 8.5.sp,
                    color = NeobrutalTokens.TextMuted
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            if (item.isInstalled) {
                NeobrutalPillBadge(
                    text = "TERPASANG",
                    backgroundColor = NeobrutalTokens.AccentMint,
                    textColor = Color(0xFF166534),
                    borderWidth = 1.dp
                )
            } else {
                NeobrutalButton(
                    onClick = onDownloadClick,
                    backgroundColor = NeobrutalTokens.AccentPrimary,
                    shadowOffset = 1.5.dp,
                    cornerRadius = 4.dp
                ) {
                    Text(
                        text = "UNDUH & PASANG",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StepCheckItem(
    text: String,
    isDone: Boolean = false,
    isRunning: Boolean = false
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val symbol = when {
            isDone -> "✓"
            isRunning -> "▶"
            else -> " "
        }
        val symbolColor = when {
            isDone -> Color(0xFF166534)
            isRunning -> NeobrutalTokens.AccentOrange
            else -> Color.Black
        }

        Box(
            modifier = Modifier
                .size(14.dp)
                .background(if (isDone) Color(0xFFDCFCE7) else Color.White, RoundedCornerShape(3.dp))
                .border(1.dp, Color.Black, RoundedCornerShape(3.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = symbol, fontSize = 8.sp, fontWeight = FontWeight.Black, color = symbolColor)
        }

        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 8.5.sp,
            fontWeight = if (isRunning || isDone) FontWeight.Bold else FontWeight.Medium,
            color = if (isRunning) Color.Black else NeobrutalTokens.TextMuted
        )
    }
}
