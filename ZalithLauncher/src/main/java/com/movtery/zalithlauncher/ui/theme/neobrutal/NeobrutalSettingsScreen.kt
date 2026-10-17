/*
 * SA Launcher - Neobrutalism Settings Screen
 * Implements Page 2 of Zalith.pdf exactly
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movtery.zalithlauncher.game.renderer.Renderers
import com.movtery.zalithlauncher.game.version.installed.VersionsManager
import com.movtery.zalithlauncher.setting.AllSettings
import com.movtery.zalithlauncher.setting.unit.getOrMin
import com.movtery.zalithlauncher.utils.platform.bytesToMB
import com.movtery.zalithlauncher.utils.platform.getTotalMemory

@Composable
fun NeobrutalSettingsScreen(
    onManageRuntimes: () -> Unit = {},
    onInstallPlugin: () -> Unit = {},
    onResetSettings: () -> Unit = {},
    onSaveSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val totalRamMb = remember(context) { runCatching { getTotalMemory(context).bytesToMB().toInt() }.getOrDefault(4096) }
    var ramAlloc by remember { mutableIntStateOf(AllSettings.ramAllocation.getOrMin().coerceIn(512, totalRamMb)) }
    var selectedCategory by remember { mutableStateOf("Renderer") }
    var selectedRenderer by remember { mutableStateOf(AllSettings.rendererV2.state.name) }
    var autoRam by remember { mutableStateOf(AllSettings.autoPickJavaRuntime.state) }
    var saveLog by remember { mutableStateOf(true) }
    var jvmArgs by remember { mutableStateOf("-XX:+UseG1GC -XX:+UnlockExperimentalVMOptions") }

    val categories = listOf(
        "Renderer", "Game", "Kontrol", "Gamepad", "Launcher", "Java", "Kontrol Java (Java Control)", "Tentang"
    )

    Row(
        modifier = modifier
            .fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // COLUMN 1: RENDERER & PLUGIN LIBRARY (Page 2 Left Column)
        NeobrutalWindowBox(
            title = "[ENGINE] RENDERER & PLUGIN LIBRARY",
            headerColor = NeobrutalTokens.AccentOrange,
            headerTextColor = Color.Black,
            modifier = Modifier
                .weight(1.1f)
                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Item 1: gl4es
                RendererItemCard(
                    title = "gl4es",
                    subtitle = "BUILT-IN • OpenGL ES 2/3 • Kompatibilitas luas",
                    isActive = selectedRenderer.contains("gl4es", ignoreCase = true),
                    onSelect = { selectedRenderer = "gl4es" }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Item 2: Zink
                RendererItemCard(
                    title = "Zink",
                    subtitle = "PLUGIN • Mesa 24.1.0 • Vulkan 1.1+",
                    isActive = selectedRenderer.contains("zink", ignoreCase = true),
                    canDelete = true,
                    onSelect = { selectedRenderer = "zink" }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Item 3: MobileGL v2.4
                RendererItemCard(
                    title = "MobileGL v2.4",
                    subtitle = "CUSTOM • Vulkan Pipe • ARM64-v8a",
                    isActive = selectedRenderer.contains("mobilegl", ignoreCase = true) || selectedRenderer.isEmpty(),
                    onSelect = { selectedRenderer = "mobilegl" }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Dashed Button: + PASANG PLUGIN LIBRARY (.so)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFAFAFA), RoundedCornerShape(8.dp))
                        .border(1.5.dp, Color.Black, RoundedCornerShape(8.dp))
                        .clickable { onInstallPlugin() }
                        .padding(vertical = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+ PASANG PLUGIN LIBRARY (.so)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.weight(1f, fill = false))
                Spacer(modifier = Modifier.height(8.dp))

                // Info Footer
                Text(
                    text = "ⓘ Plugin harus cocok dengan ARM64. Renderer baru diterapkan saat game diluncurkan ulang.",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeobrutalTokens.TextMuted,
                    lineHeight = 13.sp
                )
            }
        }

        // COLUMN 2: RAM, JAVA & RUNTIME (Page 2 Center Column)
        NeobrutalWindowBox(
            title = "[SYS] RAM, JAVA & RUNTIME",
            headerColor = NeobrutalTokens.AccentSecondary,
            headerTextColor = Color.White,
            statusPill = "MEMORY SAFE: OK",
            statusPillColor = NeobrutalTokens.AccentMint,
            modifier = Modifier
                .weight(1.4f)
                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // ALOKASI RAM Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ALOKASI RAM",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )

                    // Counter Pill [-] 2400 / 4096 MB [+]
                    Row(
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(6.dp))
                            .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "−",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier
                                .clickable {
                                    if (ramAlloc > 512) {
                                        ramAlloc -= 128
                                        AllSettings.ramAllocation.save(ramAlloc)
                                    }
                                }
                                .padding(horizontal = 6.dp)
                        )
                        Text(
                            text = "$ramAlloc / $totalRamMb MB",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                        Text(
                            text = "+",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier
                                .clickable {
                                    if (ramAlloc < totalRamMb) {
                                        ramAlloc += 128
                                        AllSettings.ramAllocation.save(ramAlloc)
                                    }
                                }
                                .padding(horizontal = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Slider(
                    value = ramAlloc.toFloat(),
                    onValueChange = {
                        ramAlloc = it.toInt()
                        AllSettings.ramAllocation.save(ramAlloc)
                    },
                    valueRange = 512f..totalRamMb.toFloat(),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.Black,
                        activeTrackColor = NeobrutalTokens.AccentPrimary,
                        inactiveTrackColor = Color(0xFFE5E7EB)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("MIN. 512 MB", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = NeobrutalTokens.TextMuted)
                    Text("REKOMENDASI: 2048–2560 MB", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color.Black)
                    Text("$totalRamMb MB", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = NeobrutalTokens.TextMuted)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row: Java Runtime & Resolusi Game Dropdowns
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("JAVA RUNTIME", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        Spacer(modifier = Modifier.height(3.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(6.dp))
                                .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
                                .clickable { onManageRuntimes() }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text("OpenJDK 17 (ARM64) ▾", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("RESOLUSI GAME", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        Spacer(modifier = Modifier.height(3.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(6.dp))
                                .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text("100% • 1600 × 720 ▾", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ARGUMEN JVM TAMBAHAN
                Text("ARGUMEN JVM TAMBAHAN", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.Black)
                Spacer(modifier = Modifier.height(3.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(6.dp))
                        .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (jvmArgs.isNotBlank()) jvmArgs else "-XX:+UseG1GC -XX:+UnlockExperimentalVMOptions",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Switches & Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    NeobrutalToggleOption(
                        text = "Auto RAM",
                        checked = autoRam,
                        onCheckedChange = {
                            autoRam = it
                            AllSettings.autoPickJavaRuntime.save(it)
                        }
                    )

                    NeobrutalToggleOption(
                        text = "Simpan log saat crash",
                        checked = saveLog,
                        onCheckedChange = { saveLog = it }
                    )

                    NeobrutalButton(
                        onClick = onManageRuntimes,
                        backgroundColor = Color.White,
                        shadowOffset = 2.dp,
                        cornerRadius = 6.dp
                    ) {
                        Text(
                            text = ">_ KELOLA RUNTIME",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Info Footer Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF0FDF4), RoundedCornerShape(6.dp))
                        .border(1.5.dp, Color(0xFF16A34A), RoundedCornerShape(6.dp))
                        .padding(6.dp)
                ) {
                    Text(
                        text = "v2.8-STABLE • Pengaturan diterapkan untuk instance aktif. RAM perangkat tetap disisakan untuk sistem Android.",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF166534),
                        lineHeight = 12.sp
                    )
                }
            }
        }

        // COLUMN 3: CATEGORY VERTICAL MENU (Page 2 Right Column)
        Column(
            modifier = Modifier
                .width(130.dp)
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
private fun RendererItemCard(
    title: String,
    subtitle: String,
    isActive: Boolean,
    canDelete: Boolean = false,
    onSelect: () -> Unit
) {
    val bgColor = if (isActive) Color(0xFFFFFBEB) else Color.White
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(8.dp))
            .border(1.5.dp, Color.Black, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
                Text(
                    text = subtitle,
                    fontSize = 8.5.sp,
                    color = NeobrutalTokens.TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            if (isActive) {
                NeobrutalPillBadge(
                    text = "ACTIVE",
                    backgroundColor = Color.Black,
                    textColor = Color.White,
                    borderWidth = 1.dp
                )
            } else {
                NeobrutalButton(
                    onClick = onSelect,
                    backgroundColor = Color.White,
                    shadowOffset = 1.5.dp,
                    cornerRadius = 6.dp
                ) {
                    Text(
                        text = "GUNAKAN",
                        fontSize = 9.sp,
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
private fun NeobrutalToggleOption(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onCheckedChange(!checked) }
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(if (checked) NeobrutalTokens.AccentMint else Color.White, RoundedCornerShape(4.dp))
                .border(1.5.dp, Color.Black, RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Text(text = "✓", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.Black)
            }
        }
        Spacer(modifier = Modifier.width(5.dp))
        Text(text = text, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}
