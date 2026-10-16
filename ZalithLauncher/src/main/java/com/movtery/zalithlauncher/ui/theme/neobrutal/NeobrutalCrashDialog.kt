/*
 * SA Launcher - Neobrutalism Crash Analyzer Modal Dialog
 * Implements Section 3.2 from UI_UX Product Requirement Document.md
 */

package com.movtery.zalithlauncher.ui.theme.neobrutal

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun NeobrutalCrashDialog(
    isVisible: Boolean,
    diagnosisTitle: String = "Pemberhentian Paksa: Kehabisan Memori (OOM)",
    causeText: String = "JVM meminta alokasi RAM melebihi kapasitas bebas yang diizinkan oleh sistem Android.",
    recommendations: List<String> = listOf(
        "Turunkan alokasi RAM profil ke 2048 MB.",
        "Matikan shaderpack berat atau mod render berlebih."
    ),
    rawLog: String = "",
    onDismiss: () -> Unit,
    onUploadMclogs: () -> Unit,
    onOpenRamSettings: () -> Unit
) {
    if (!isVisible) return

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        NeobrutalCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color.White,
            borderWidth = NeobrutalTokens.BorderHeavy,
            shadowOffset = NeobrutalTokens.ShadowHero,
            cornerRadius = 14.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // 1. Alert Header Bar (Bubblegum Pink)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NeobrutalTokens.AccentPink)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚠ SYSTEM ALERT: CRASH DETECTED",
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.weight(1f)
                    )

                    // Close retro button
                    Box(
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(4.dp))
                            .border(1.5.dp, Color.Black, RoundedCornerShape(4.dp))
                            .clickable { onDismiss() }
                            .padding(horizontal = 6.dp, vertical = 1.dp)
                    ) {
                        Text(text = "✕", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Black)
                    }
                }

                // Black separator line
                Box(modifier = Modifier.fillMaxWidth().height(2.5.dp).background(Color.Black))

                // 2. Dialog Body
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Highlight Diagnosis Card (Electric Yellow)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NeobrutalTokens.AccentPrimary, RoundedCornerShape(8.dp))
                            .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "DIAGNOSA:",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                            Text(
                                text = "\"$diagnosisTitle\"",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }
                    }

                    // Cause Section
                    Column {
                        Text(
                            text = "PENYEBAB:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = causeText,
                            fontSize = 11.sp,
                            color = NeobrutalTokens.TextMuted,
                            lineHeight = 15.sp
                        )
                    }

                    // Recommendation Bullets
                    Column {
                        Text(
                            text = "REKOMENDASI:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        recommendations.forEach { item ->
                            Text(
                                text = "• $item",
                                fontSize = 11.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Primary Action: Upload to mclo.gs (Mint Neo)
                    NeobrutalButton(
                        onClick = onUploadMclogs,
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = NeobrutalTokens.AccentMint,
                        shadowOffset = 3.dp,
                        cornerRadius = 8.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⬆ UPLOAD KE MCLO.GS & SALIN LINK",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }
                    }

                    // Secondary Action Row: Copy Raw Log | Open RAM Settings
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        NeobrutalButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(rawLog))
                                Toast.makeText(context, "Log mentah disalin ke clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            backgroundColor = Color.White,
                            shadowOffset = 2.5.dp,
                            cornerRadius = 8.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "⎘ Salin Log Mentah",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }

                        NeobrutalButton(
                            onClick = onOpenRamSettings,
                            modifier = Modifier.weight(1f),
                            backgroundColor = Color.White,
                            shadowOffset = 2.5.dp,
                            cornerRadius = 8.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "⚙ Buka Setting RAM",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
