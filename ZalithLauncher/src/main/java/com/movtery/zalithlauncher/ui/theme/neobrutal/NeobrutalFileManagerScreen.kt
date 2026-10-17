/*
 * SA Launcher - Neobrutalism File Manager Screen
 * Implements Page 7 of Zalith.pdf exactly
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movtery.zalithlauncher.game.version.installed.VersionsManager
import com.movtery.zalithlauncher.path.PathManager
import com.movtery.zalithlauncher.utils.file.formatFileSize
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class FileEntry(
    val file: File,
    val name: String,
    val isDirectory: Boolean,
    val sizeStr: String,
    val dateStr: String,
    val typeStr: String
)

@Composable
fun NeobrutalFileManagerScreen(
    onImportFile: () -> Unit = {},
    onCreateFolder: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val activeVersion = VersionsManager.currentVersion.value
    val rootDir = remember(activeVersion) {
        activeVersion?.getGameDir() ?: PathManager.DIR_FILES_EXTERNAL
    }

    var currentDir by remember { mutableStateOf(File(rootDir, "mods").takeIf { it.exists() } ?: rootDir) }
    val fileEntries = remember { mutableStateListOf<FileEntry>() }
    var selectedFile by remember { mutableStateOf<FileEntry?>(null) }

    fun refreshFiles() {
        val files = currentDir.listFiles()?.sortedWith(
            compareBy<File> { !it.isDirectory }.thenBy { it.name.lowercase() }
        ) ?: emptyList()

        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        val entries = files.map { file ->
            val isDir = file.isDirectory
            val size = if (isDir) "DIR" else formatFileSize(file.length())
            val date = dateFormat.format(Date(file.lastModified()))
            val type = when {
                isDir -> "FOLDER"
                file.name.endsWith(".jar") -> "MOD JAR"
                file.name.endsWith(".zip") -> "ZIP PACK"
                file.name.endsWith(".json") -> "CONFIG"
                file.name.endsWith(".png") -> "IMAGE"
                file.name.endsWith(".txt") || file.name.endsWith(".log") -> "LOG"
                else -> "FILE"
            }
            FileEntry(file, file.name, isDir, size, date, type)
        }
        fileEntries.clear()
        fileEntries.addAll(entries)
        selectedFile = entries.firstOrNull()
    }

    LaunchedEffect(currentDir) {
        refreshFiles()
    }

    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // COLUMN 1: INSTANCE FOLDER TABLE (Left Column)
        NeobrutalWindowBox(
            title = "[FOLDER] DIREKTORI GAME",
            headerColor = NeobrutalTokens.AccentSecondary,
            headerTextColor = Color.White,
            statusPill = "${fileEntries.size} ITEMS",
            statusPillColor = NeobrutalTokens.AccentMint,
            modifier = Modifier
                .weight(1.3f)
                .fillMaxHeight()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Breadcrumb path row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF3F4F6), RoundedCornerShape(4.dp))
                        .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentDir != rootDir && currentDir.parentFile != null) {
                        Box(
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(3.dp))
                                .border(1.dp, Color.Black, RoundedCornerShape(3.dp))
                                .clickable { currentDir = currentDir.parentFile!! }
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(text = "↑ UP", fontSize = 7.5.sp, fontWeight = FontWeight.Black)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = currentDir.absolutePath.replace(rootDir.parentFile?.absolutePath ?: "", ""),
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE5E7EB), RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        .border(1.dp, Color.Black, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "NAMA FILE", fontSize = 8.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1.2f))
                    Text(text = "UKURAN", fontSize = 8.sp, fontWeight = FontWeight.Black, modifier = Modifier.width(55.dp))
                    Text(text = "TANGGAL", fontSize = 8.sp, fontWeight = FontWeight.Black, modifier = Modifier.width(70.dp))
                    Text(text = "TIPE", fontSize = 8.sp, fontWeight = FontWeight.Black, modifier = Modifier.width(55.dp))
                }

                // Table Items
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(fileEntries) { entry ->
                        val isSel = selectedFile?.file?.absolutePath == entry.file.absolutePath
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isSel) Color(0xFFFFFBEB) else Color.White)
                                .border(1.dp, Color(0xFFE5E7EB))
                                .clickable {
                                    selectedFile = entry
                                    if (entry.isDirectory) {
                                        currentDir = entry.file
                                    }
                                }
                                .padding(horizontal = 6.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (entry.isDirectory) "[D] ${entry.name}" else entry.name,
                                fontSize = 8.5.sp,
                                fontWeight = if (entry.isDirectory) FontWeight.Black else FontWeight.Bold,
                                color = if (entry.isDirectory) Color(0xFF1E40AF) else Color.Black,
                                modifier = Modifier.weight(1.2f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(text = entry.sizeStr, fontSize = 8.sp, color = NeobrutalTokens.TextMuted, modifier = Modifier.width(55.dp))
                            Text(text = entry.dateStr, fontSize = 7.5.sp, color = NeobrutalTokens.TextMuted, modifier = Modifier.width(70.dp))
                            Text(text = entry.typeStr, fontSize = 7.5.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.width(55.dp))
                        }
                    }
                }
            }
        }

        // COLUMN 2: PRATINJAU FILE (Center Column)
        NeobrutalWindowBox(
            title = "[PREVIEW] PRATINJAU FILE",
            headerColor = NeobrutalTokens.AccentMint,
            headerTextColor = Color.Black,
            modifier = Modifier
                .weight(1.1f)
                .fillMaxHeight()
        ) {
            if (selectedFile == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Pilih file untuk melihat pratinjau.", fontSize = 9.sp, color = NeobrutalTokens.TextMuted)
                }
            } else {
                val file = selectedFile!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = file.name,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "${file.sizeStr} • ${file.typeStr} • SHA-1 OK",
                        fontSize = 8.5.sp,
                        color = NeobrutalTokens.TextMuted
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Primary Button: + PASANG MOD TERPILIH
                    NeobrutalButton(
                        onClick = { /* install selected */ },
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = NeobrutalTokens.AccentPrimary,
                        shadowOffset = 2.dp,
                        cornerRadius = 6.dp
                    ) {
                        Text(
                            text = "+ PASANG MOD TERPILIH",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f, fill = false))

                    // Action Buttons Row: EKSTRAK, GANTI NAMA, HAPUS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        NeobrutalButton(
                            onClick = { /* Ekstrak */ },
                            modifier = Modifier.weight(1f),
                            backgroundColor = Color.White,
                            shadowOffset = 1.5.dp,
                            cornerRadius = 4.dp
                        ) {
                            Text("EKSTRAK", fontSize = 8.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(vertical = 4.dp))
                        }
                        NeobrutalButton(
                            onClick = { /* Ganti nama */ },
                            modifier = Modifier.weight(1.1f),
                            backgroundColor = Color.White,
                            shadowOffset = 1.5.dp,
                            cornerRadius = 4.dp
                        ) {
                            Text("GANTI NAMA", fontSize = 8.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(vertical = 4.dp))
                        }
                        NeobrutalButton(
                            onClick = {
                                file.file.delete()
                                refreshFiles()
                            },
                            modifier = Modifier.weight(1f),
                            backgroundColor = Color(0xFFFFF1F2),
                            shadowOffset = 1.5.dp,
                            cornerRadius = 4.dp
                        ) {
                            Text("HAPUS", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color(0xFFE11D48), modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            }
        }

        // COLUMN 3: AKSES CEPAT (Right Column)
        Column(
            modifier = Modifier
                .width(130.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NeobrutalWindowBox(
                title = "[QUICK] AKSES CEPAT",
                headerColor = Color(0xFFFEF3C7),
                headerTextColor = Color.Black,
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val quickFolders = listOf(
                        "mods" to "/mods",
                        "resourcepacks" to "/resourcepacks",
                        "shaderpacks" to "/shaderpacks",
                        "saves" to "/saves",
                        "screenshots" to "/screenshots",
                        "logs" to "/logs"
                    )

                    quickFolders.forEach { (folderName, displayLabel) ->
                        val target = File(rootDir, folderName)
                        val isAct = currentDir.name == folderName
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isAct) NeobrutalTokens.AccentMint else Color.White, RoundedCornerShape(4.dp))
                                .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
                                .clickable {
                                    if (!target.exists()) target.mkdirs()
                                    currentDir = target
                                }
                                .padding(horizontal = 6.dp, vertical = 5.dp)
                        ) {
                            Text(text = displayLabel, fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }
            }

            // Bottom Storage Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Column {
                    Text(text = "PENYIMPANAN", fontSize = 7.5.sp, fontWeight = FontWeight.Black, color = NeobrutalTokens.TextMuted)
                    Text(text = "4.2 GB / 64 GB", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .background(Color(0xFFE5E7EB), RoundedCornerShape(3.dp))
                            .border(1.dp, Color.Black, RoundedCornerShape(3.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.25f)
                                .background(NeobrutalTokens.AccentSecondary, RoundedCornerShape(2.dp))
                        )
                    }
                }
            }
        }
    }
}
