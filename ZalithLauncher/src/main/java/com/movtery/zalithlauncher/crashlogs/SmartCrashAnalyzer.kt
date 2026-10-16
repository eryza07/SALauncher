/*
 * SA Launcher - Smart Crash Analyzer
 * Implements FR-004, FR-005, MH-02, MH-03 from PRD Launcher.md
 */

package com.movtery.zalithlauncher.crashlogs

import java.io.File
import java.util.regex.Pattern

data class CrashAnalysis(
    val title: String,
    val cause: String,
    val recommendations: List<String>,
    val rawLogSnippet: String,
    val isOom: Boolean = false,
    val isMissingMod: Boolean = false,
    val isNativeCrash: Boolean = false
)

object SmartCrashAnalyzer {

    private val OOM_PATTERNS = listOf(
        Pattern.compile("java\\.lang\\.OutOfMemoryError", Pattern.CASE_INSENSITIVE),
        Pattern.compile("OutOfMemoryError: Java heap space", Pattern.CASE_INSENSITIVE),
        Pattern.compile("Could not reserve enough space for .* object heap", Pattern.CASE_INSENSITIVE),
        Pattern.compile("Out of Memory: Kill process", Pattern.CASE_INSENSITIVE)
    )

    private val MISSING_MOD_PATTERNS = listOf(
        Pattern.compile("ModResolutionException:?\\s*([^\r\n]+)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("MissingMandatoryModException:?\\s*([^\r\n]+)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("requires (?:version [^\\s,]+ of )?(?:mod )?['\"]?([a-zA-Z0-9_.-]+)['\"]?", Pattern.CASE_INSENSITIVE),
        Pattern.compile("Incompatible mod set found", Pattern.CASE_INSENSITIVE),
        Pattern.compile("net\\.fabricmc\\.loader\\.discovery\\.ModResolutionException", Pattern.CASE_INSENSITIVE),
        Pattern.compile("net\\.minecraftforge\\.fml\\.ModLoadingException", Pattern.CASE_INSENSITIVE),
        Pattern.compile("net\\.neoforged\\.fml\\.ModLoadingException", Pattern.CASE_INSENSITIVE)
    )

    private val NATIVE_CRASH_PATTERNS = listOf(
        Pattern.compile("Fatal signal 11 \\(SIGSEGV\\)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("SIGSEGV", Pattern.CASE_INSENSITIVE),
        Pattern.compile("libvulkan.*\\.so", Pattern.CASE_INSENSITIVE),
        Pattern.compile("libgl4es.*\\.so", Pattern.CASE_INSENSITIVE),
        Pattern.compile("libMobileGL.*\\.so", Pattern.CASE_INSENSITIVE),
        Pattern.compile("Failed to choose a suitable EGLConfig", Pattern.CASE_INSENSITIVE),
        Pattern.compile("GL_INVALID_OPERATION", Pattern.CASE_INSENSITIVE)
    )

    private val JAVA_VERSION_PATTERNS = listOf(
        Pattern.compile("UnsupportedClassVersionError: .* class file version (\\d+)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("has been compiled by a more recent version of the Java Runtime", Pattern.CASE_INSENSITIVE)
    )

    /**
     * Menganalisis file log dan kode keluar JVM untuk menghasilkan diagnosa bahasa manusia
     */
    fun analyze(logFile: File?, exitCode: Int, isSignal: Boolean): CrashAnalysis {
        val rawLog = readLogContent(logFile)
        val snippet = extractSnippet(rawLog)

        // 1. Deteksi Out of Memory (OOM)
        val isOomBySignal = (isSignal && exitCode == 9) || exitCode == 137
        val isOomByLog = OOM_PATTERNS.any { it.matcher(rawLog).find() }

        if (isOomBySignal || isOomByLog) {
            return CrashAnalysis(
                title = "Pemberhentian Paksa: Kehabisan Memori (OOM)",
                cause = "JVM meminta alokasi RAM yang melebihi batas memori bebas perangkat, sehingga dihentikan paksa oleh Android Low Memory Killer (LMK).",
                recommendations = listOf(
                    "Turunkan alokasi RAM profil ke batas yang aman (sisakan minimal 1.2 GB untuk OS Android).",
                    "Tutup semua aplikasi latar belakang lain sebelum memulai game.",
                    "Gunakan mod optimasi memori seperti FerriteCore atau Memory Leak Fix.",
                    "Turunkan jarak render (Render Distance) di pengaturan video."
                ),
                rawLogSnippet = snippet,
                isOom = true
            )
        }

        // 2. Deteksi Missing Mod / Bentrok Dependensi
        for (pattern in MISSING_MOD_PATTERNS) {
            val matcher = pattern.matcher(rawLog)
            if (matcher.find()) {
                val detail = try {
                    if (matcher.groupCount() >= 1) matcher.group(1)?.trim() else null
                } catch (_: Exception) { null }

                val causeMessage = if (!detail.isNullOrBlank()) {
                    "Dependensi mod tidak lengkap atau versi tidak cocok: $detail"
                } else {
                    "Terdapat mod yang memerlukan library/mod pendukung yang belum terpasang atau versi mod tidak cocok."
                }

                return CrashAnalysis(
                    title = "Mod Hilang / Konflik Dependensi",
                    cause = causeMessage,
                    recommendations = listOf(
                        "Periksa mod yang membutuhkan dependensi tambahan (seperti Fabric API) dan pasang versi yang sesuai.",
                        "Pastikan versi semua mod cocok dengan versi Minecraft yang sedang dimainkan.",
                        "Nonaktifkan atau hapus mod yang baru saja ditambahkan untuk mengisolasi penyebab error."
                    ),
                    rawLogSnippet = snippet,
                    isMissingMod = true
                )
            }
        }

        // 3. Deteksi Native Crash / Driver GPU Failure
        val isNativeBySignal = (isSignal && (exitCode == 11 || exitCode == 139))
        val isNativeByLog = NATIVE_CRASH_PATTERNS.any { it.matcher(rawLog).find() }

        if (isNativeBySignal || isNativeByLog) {
            return CrashAnalysis(
                title = "Kegagalan Driver Grafis / Native Crash (Signal 11)",
                cause = "Renderer grafis (.so) atau library native mengalami segmentation fault saat berkomunikasi dengan driver GPU perangkat.",
                recommendations = listOf(
                    "Ganti pilihan renderer di pengaturan profil (coba beralih ke gl4es bawaan, ANGLE, atau Zink).",
                    "Jika perangkat menggunakan GPU Mali, gunakan renderer yang kompatibel dengan arsitektur Mali.",
                    "Matikan shaderpack (Iris / Oculus) atau mod grafis berat yang tidak didukung driver GPU."
                ),
                rawLogSnippet = snippet,
                isNativeCrash = true
            )
        }

        // 4. Deteksi Java Runtime Mismatch
        for (pattern in JAVA_VERSION_PATTERNS) {
            if (pattern.matcher(rawLog).find()) {
                return CrashAnalysis(
                    title = "Versi Java Runtime Tidak Cocok",
                    cause = "Minecraft atau mod dikompilasi untuk versi Java yang lebih baru daripada Java Runtime yang digunakan saat ini.",
                    recommendations = listOf(
                        "Ganti Java Runtime pada pengaturan versi (Gunakan Java 17 atau Java 21 untuk Minecraft 1.18+).",
                        "Pastikan Java Runtime terpasang dengan lengkap di perangkat."
                    ),
                    rawLogSnippet = snippet
                )
            }
        }

        // 5. Default Fallback
        val exitTitle = if (isSignal) "Sinyal Sistem Tidak Normal (Signal $exitCode)" else "Game Berhenti Tidak Normal (Kode: $exitCode)"
        return CrashAnalysis(
            title = exitTitle,
            cause = "Minecraft berhenti secara tiba-tiba dengan kode keluar $exitCode. Log error telah direkam di penyimpanan perangkat.",
            recommendations = listOf(
                "Tekan tombol 'Upload ke mclo.gs & Salin Link' untuk melihat analisis log lengkap dan membagikannya ke komunitas.",
                "Periksa mod yang baru saja dipasang atau reset konfigurasi game jika perlu."
            ),
            rawLogSnippet = snippet
        )
    }

    /**
     * Memotong log sesuai PRD FR-005 & Section 12:
     * Ambil 500 baris pertama + 2500 baris terakhir jika melebihi 3000 baris (maks 10 MB payload)
     */
    fun prepareLogForUpload(logFile: File): String {
        if (!logFile.exists() || !logFile.isFile) return ""

        return try {
            val lines = logFile.readLines()
            if (lines.size <= 3000) {
                truncateBytesIfNeeded(lines.joinToString("\n"))
            } else {
                val header = lines.take(500).joinToString("\n")
                val footer = lines.takeLast(2500).joinToString("\n")
                val truncatedText = "$header\n\n... [DIPOTONG OLEH SA LAUNCHER CRASH ANALYZER - MENGHEMAT BANDWIDTH MCLO.GS] ...\n\n$footer"
                truncateBytesIfNeeded(truncatedText)
            }
        } catch (_: Exception) {
            runCatching { logFile.readText() }.getOrDefault("")
        }
    }

    private fun truncateBytesIfNeeded(text: String, maxBytes: Int = 10 * 1024 * 1024): String {
        val bytes = text.toByteArray(Charsets.UTF_8)
        if (bytes.size <= maxBytes) return text
        return String(bytes, 0, maxBytes, Charsets.UTF_8)
    }

    private fun readLogContent(logFile: File?): String {
        if (logFile == null || !logFile.exists() || !logFile.isFile) return ""
        return runCatching {
            val lines = logFile.readLines()
            // Baca maksimal 2500 baris terakhir untuk diagnosa crash yang efisien
            lines.takeLast(2500).joinToString("\n")
        }.getOrDefault("")
    }

    private fun extractSnippet(rawLog: String): String {
        if (rawLog.isBlank()) return "Tidak ada data log yang tersedia."
        val lines = rawLog.lines()
        return lines.takeLast(60).joinToString("\n")
    }
}
