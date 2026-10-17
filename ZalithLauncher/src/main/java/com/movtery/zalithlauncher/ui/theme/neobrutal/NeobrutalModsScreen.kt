/*
 * SA Launcher - Neobrutalism Mods Screen
 * Implements Pages 3 & 8 of Zalith.pdf exactly
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movtery.zalithlauncher.game.version.installed.Version
import com.movtery.zalithlauncher.game.version.installed.VersionsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

data class ModDisplayItem(
    val file: File,
    val name: String,
    val versionStr: String,
    val isEnabled: Boolean,
    val badge: String = "TERBARU",
    val badgeBg: Color = NeobrutalTokens.AccentMint,
    val badgeText: Color = Color(0xFF166534)
)

@Composable
fun NeobrutalModsScreen(
    currentVersion: Version? = VersionsManager.currentVersion.value,
    onInstallModFile: () -> Unit = {},
    onExploreModsOnline: () -> Unit = {},
    onChangeLoader: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedCategory by remember { mutableStateOf("MODS") }
    val categories = listOf("MODS", "LOADER", "RESOURCE PACK", "SHADERS", "MODPACK", "DUNIA", "FAVORIT")

    val modsList = remember { mutableStateListOf<ModDisplayItem>() }
    var isLoadingMods by remember { mutableStateOf(false) }
    var currentPage by remember { mutableIntStateOf(1) }
    val pageSize = 7

    fun reloadMods() {
        if (currentVersion == null) {
            modsList.clear()
            return
        }
        coroutineScope.launch {
            isLoadingMods = true
            val loaded = withContext(Dispatchers.IO) {
                val modsDir = File(currentVersion.getGameDir(), "mods")
                if (!modsDir.exists() || !modsDir.isDirectory) {
                    emptyList()
                } else {
                    val files = modsDir.listFiles()?.filter {
                        it.isFile && (it.name.endsWith(".jar") || it.name.endsWith(".jar.disabled"))
                    } ?: emptyArray()

                    files.map { file ->
                        val isEnabled = !file.name.endsWith(".disabled")
                        val cleanName = file.name.removeSuffix(".disabled").removeSuffix(".jar")
                        ModDisplayItem(
                            file = file,
                            name = cleanName,
                            versionStr = "v1.0",
                            isEnabled = isEnabled,
                            badge = if (isEnabled) "TERBARU" else "OFF",
                            badgeBg = if (isEnabled) NeobrutalTokens.AccentMint else Color(0xFFF3F4F6),
                            badgeText = if (isEnabled) Color(0xFF166534) else Color(0xFF6B7280)
                        )
                    }
                }
            }
            modsList.clear()
            modsList.addAll(loaded)
            isLoadingMods = false
        }
    }

    LaunchedEffect(currentVersion) {
        reloadMods()
    }

    // Default mock mods for preview if no local mod files yet
    val displayMods = if (modsList.isNotEmpty()) {
        modsList
    } else {
        listOf(
            ModDisplayItem(File("sodium.jar"), "Sodium", "v0.5.8", true, "TERBARU", NeobrutalTokens.AccentMint, Color(0xFF166534)),
            ModDisplayItem(File("iris.jar"), "Iris Shaders", "v1.7.0", true, "TERBARU", NeobrutalTokens.AccentMint, Color(0xFF166534)),
            ModDisplayItem(File("lithium.jar"), "Lithium", "v0.12.1", true, "PERBARUI", NeobrutalTokens.AccentOrange, Color(0xFF9A3412)),
            ModDisplayItem(File("ferritecore.jar"), "FerriteCore", "v6.0.1", true, "TERBARU", NeobrutalTokens.AccentMint, Color(0xFF166534)),
            ModDisplayItem(File("modmenu.jar"), "Mod Menu", "v9.0.0", false, "OFF", Color(0xFFF3F4F6), Color(0xFF6B7280)),
            ModDisplayItem(File("entityculling.jar"), "Entity Culling", "v1.6.2", true, "TERBARU", NeobrutalTokens.AccentMint, Color(0xFF166534)),
            ModDisplayItem(File("clothconfig.jar"), "Cloth Config", "v13.0", true, "TERBARU", NeobrutalTokens.AccentMint, Color(0xFF166534))
        )
    }

    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (selectedCategory == "FAVORIT") {
            // PAGE 8 VIEW: FAVORIT & KOLEKSI
            NeobrutalWindowBox(
                title = "[KOLEKSI] MODS & MODPACK FAVORIT",
                headerColor = NeobrutalTokens.AccentOrange,
                headerTextColor = Color.Black,
                statusPill = "CURATED PACKS",
                statusPillColor = Color(0xFFFFFBEB),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        ModpackCollectionCard(
                            title = "PERFORMANCE BOOST PACK",
                            badge = "REKOMENDASI",
                            description = "Paket esensial FPS tinggi untuk HP menengah & low-end. Optimasi chunk & entity rendering.",
                            modChips = listOf("Sodium", "Lithium", "FerriteCore", "Entity Culling", "Krypton", "ModernFix"),
                            buttonText = "▶ PASANG SEMUA (6 MOD)",
                            onInstall = onExploreModsOnline
                        )
                    }
                    item {
                        ModpackCollectionCard(
                            title = "SHADERS & VISUAL PACK",
                            badge = "GRAFIS HD",
                            description = "Dukungan shaderpack realistis dengan Iris dan Sodium untuk grafis Minecraft maksimal.",
                            modChips = listOf("Iris Shaders", "Continuity", "Sodium Extra", "Reese's Sodium Options"),
                            buttonText = "▶ PASANG SEMUA (4 MOD)",
                            onInstall = onExploreModsOnline
                        )
                    }
                    item {
                        ModpackCollectionCard(
                            title = "QUALITY OF LIFE PACK",
                            badge = "PENGALAMAN",
                            description = "HUD informatif, mod menu intuitif, dan resep crafting tanpa mengubah vanilla feeling.",
                            modChips = listOf("AppleSkin", "Mod Menu", "Roughly Enough Items", "Jade"),
                            buttonText = "▶ PASANG SEMUA (4 MOD)",
                            onInstall = onExploreModsOnline
                        )
                    }
                }
            }
        } else {
            // PAGE 3 VIEW: MODS TABLE (Wide Column)
            NeobrutalWindowBox(
                title = "[LIST] DAFTAR MOD TERPASANG",
                headerColor = NeobrutalTokens.AccentMint,
                headerTextColor = Color.Black,
                statusPill = "${displayMods.count { it.isEnabled }} AKTIF",
                statusPillColor = Color.White,
                modifier = Modifier
                    .weight(1.5f)
                    .fillMaxHeight()
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Table Header Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE5E7EB), RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .border(1.dp, Color.Black, RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "STATUS", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.Black, modifier = Modifier.width(55.dp))
                        Text(text = "NAMA MOD", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.Black, modifier = Modifier.weight(1f))
                        Text(text = "VERSI", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.Black, modifier = Modifier.width(60.dp))
                        Text(text = "AKSI", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.Black, modifier = Modifier.width(65.dp))
                    }

                    // Table Items
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(displayMods) { mod ->
                            ModTableRowItem(
                                item = mod,
                                onToggle = {
                                    if (mod.file.exists()) {
                                        if (mod.isEnabled) {
                                            val disabledFile = File(mod.file.parentFile, mod.file.name + ".disabled")
                                            mod.file.renameTo(disabledFile)
                                        } else {
                                            val enabledFile = File(mod.file.parentFile, mod.file.name.removeSuffix(".disabled"))
                                            mod.file.renameTo(enabledFile)
                                        }
                                        reloadMods()
                                    }
                                },
                                onDelete = {
                                    if (mod.file.exists()) {
                                        mod.file.delete()
                                        reloadMods()
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Pagination / Summary Footer
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF9FAFB), RoundedCornerShape(6.dp))
                            .border(1.dp, Color.Black, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Menampilkan 1-${displayMods.size} dari ${displayMods.size} mod",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeobrutalTokens.TextMuted
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            NeobrutalButton(
                                onClick = { if (currentPage > 1) currentPage-- },
                                backgroundColor = Color.White,
                                shadowOffset = 1.dp,
                                cornerRadius = 4.dp
                            ) {
                                Text("‹ PREV", fontSize = 8.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                            }
                            NeobrutalButton(
                                onClick = { currentPage++ },
                                backgroundColor = Color.White,
                                shadowOffset = 1.dp,
                                cornerRadius = 4.dp
                            ) {
                                Text("NEXT ›", fontSize = 8.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                            }
                        }
                    }
                }
            }

            // PAGE 3 CENTER COLUMN: LOADER INSTANCE & REKOMENDASI KOMPATIBEL
            Column(
                modifier = Modifier
                    .weight(0.95f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Card 1: Loader Instance
                NeobrutalWindowBox(
                    title = "[LOADER] INSTANCE",
                    headerColor = NeobrutalTokens.AccentSecondary,
                    headerTextColor = Color.White,
                    statusPill = "ACTIVE",
                    statusPillColor = NeobrutalTokens.AccentMint,
                    modifier = Modifier.weight(1.1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Fabric Loader 0.15.11",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                        Text(
                            text = "Minecraft ${currentVersion?.getVersionName() ?: "1.20.4"}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeobrutalTokens.TextMuted
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFEFF6FF), RoundedCornerShape(6.dp))
                                .border(1.dp, Color(0xFF3B82F6), RoundedCornerShape(6.dp))
                                .padding(5.dp)
                        ) {
                            Text(
                                text = "Kompatibilitas: Sangat Baik • 24 Mod Aktif",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E40AF)
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f, fill = false))

                        NeobrutalButton(
                            onClick = onChangeLoader,
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = Color.White,
                            shadowOffset = 2.dp,
                            cornerRadius = 6.dp
                        ) {
                            Text(
                                text = "GANTI LOADER",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(vertical = 5.dp)
                            )
                        }

                        NeobrutalButton(
                            onClick = onExploreModsOnline,
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = NeobrutalTokens.AccentPrimary,
                            shadowOffset = 2.dp,
                            cornerRadius = 6.dp
                        ) {
                            Text(
                                text = "INSTALL FABRIC API",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(vertical = 5.dp)
                            )
                        }
                    }
                }

                // Card 2: Rekomendasi Kompatibel
                NeobrutalWindowBox(
                    title = "[REKOMENDASI] KOMPATIBEL",
                    headerColor = Color(0xFFFEF3C7),
                    headerTextColor = Color.Black,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        RecommendationModItem(name = "Indium", desc = "Render Sodium add-on", onInstall = onExploreModsOnline)
                        RecommendationModItem(name = "Entity Culling", desc = "Optimasi entity & mob", onInstall = onExploreModsOnline)
                        RecommendationModItem(name = "ImmediatelyFast", desc = "Fast UI rendering", onInstall = onExploreModsOnline)
                    }
                }
            }
        }

        // RIGHT COLUMN: CATEGORY MENU (Page 3 & 8 Right Column)
        Column(
            modifier = Modifier
                .width(125.dp)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            categories.forEach { category ->
                val isSelected = selectedCategory == category
                NeobrutalButton(
                    onClick = { selectedCategory = category },
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = if (isSelected) NeobrutalTokens.AccentPrimary else Color.White,
                    shadowOffset = if (isSelected) 2.dp else 1.5.dp,
                    cornerRadius = 8.dp
                ) {
                    Text(
                        text = category,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ModTableRowItem(
    item: ModDisplayItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(1.dp, Color(0xFFE5E7EB))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Status Toggle Switch
        Box(
            modifier = Modifier
                .width(55.dp)
                .clickable { onToggle() }
        ) {
            Box(
                modifier = Modifier
                    .background(
                        if (item.isEnabled) NeobrutalTokens.AccentMint else Color(0xFFE5E7EB),
                        RoundedCornerShape(4.dp)
                    )
                    .border(1.5.dp, Color.Black, RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (item.isEnabled) "AKTIF" else "NONAKTIF",
                    fontSize = 7.5.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }
        }

        // Mod Name & Badge
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.name,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.width(4.dp))
            NeobrutalPillBadge(
                text = item.badge,
                backgroundColor = item.badgeBg,
                textColor = item.badgeText,
                borderWidth = 1.dp
            )
        }

        // Mod Version
        Text(
            text = item.versionStr,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = NeobrutalTokens.TextMuted,
            modifier = Modifier.width(60.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Action Button
        Box(modifier = Modifier.width(65.dp)) {
            NeobrutalButton(
                onClick = onDelete,
                backgroundColor = Color(0xFFFFF1F2),
                shadowOffset = 1.5.dp,
                cornerRadius = 4.dp
            ) {
                Text(
                    text = "HAPUS",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFE11D48),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun RecommendationModItem(
    name: String,
    desc: String,
    onInstall: () -> Unit
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
                Text(text = name, fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.Black)
                Text(text = desc, fontSize = 8.sp, color = NeobrutalTokens.TextMuted, maxLines = 1)
            }
            Spacer(modifier = Modifier.width(4.dp))
            NeobrutalButton(
                onClick = onInstall,
                backgroundColor = NeobrutalTokens.AccentMint,
                shadowOffset = 1.5.dp,
                cornerRadius = 4.dp
            ) {
                Text(
                    text = "+ PASANG",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun ModpackCollectionCard(
    title: String,
    badge: String,
    description: String,
    modChips: List<String>,
    buttonText: String,
    onInstall: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                NeobrutalPillBadge(
                    text = badge,
                    backgroundColor = NeobrutalTokens.AccentMint,
                    textColor = Color(0xFF166534),
                    borderWidth = 1.dp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 9.sp,
                color = NeobrutalTokens.TextMuted,
                lineHeight = 13.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Mod chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                modChips.take(5).forEach { chip ->
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFF3F4F6), RoundedCornerShape(4.dp))
                            .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(text = chip, fontSize = 7.5.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            NeobrutalButton(
                onClick = onInstall,
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = NeobrutalTokens.AccentPrimary,
                shadowOffset = 2.dp,
                cornerRadius = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = buttonText, fontSize = 9.5.sp, fontWeight = FontWeight.Black, color = Color.Black)
                }
            }
        }
    }
}
