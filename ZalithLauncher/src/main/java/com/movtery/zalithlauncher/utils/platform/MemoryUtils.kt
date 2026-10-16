/*
 * Zalith Launcher 2
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

package com.movtery.zalithlauncher.utils.platform

import android.app.ActivityManager
import android.content.Context
import androidx.annotation.WorkerThread
import com.movtery.zalithlauncher.utils.device.Architecture
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.round

private const val BYTES_PER_MB = 1024L * 1024

private inline val Context.activityManager: ActivityManager
    get() = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

private fun getMemoryInfo(context: Context): ActivityManager.MemoryInfo {
    return ActivityManager.MemoryInfo().apply {
        context.activityManager.getMemoryInfo(this)
    }
}

/**
 * 获取系统总内存（单位：字节）
 */
@WorkerThread
fun getTotalMemory(context: Context) = getMemoryInfo(context).totalMem

/**
 * 获取已使用内存（单位：字节）
 */
@WorkerThread
fun getUsedMemory(context: Context): Long {
    val info = getMemoryInfo(context)
    return info.totalMem - info.availMem
}

/**
 * 获取当前可用内存（单位：字节）
 */
@WorkerThread
fun getFreeMemory(context: Context) = getMemoryInfo(context).availMem

/**
 * Hasil perhitungan Adaptive Heap Limiter (SH-02).
 */
data class AdaptiveHeapResult(
    val initialHeapMb: Int, // Digunakan untuk -Xms
    val maxHeapMb: Int,     // Digunakan untuk -Xmx
    val totalRamMb: Int,
    val reservedOsMb: Int,
    val wasCapped: Boolean
)

/**
 * Adaptive Heap Limiter (PRD SH-02):
 * Menghitung batas alokasi JVM aman (-Xmx & -Xms) secara dinamis
 * dengan menyisakan minimal 1.2 GB (1200 MB) RAM untuk OS Android,
 * guna mencegah penghentian paksa oleh Android Low Memory Killer (LMK / Signal 9).
 */
@WorkerThread
fun calculateAdaptiveHeap(context: Context, requestedRamMb: Int): AdaptiveHeapResult {
    val totalRam = getTotalMemory(context).bytesToMB().toInt()

    // Sisakan minimal 1200 MB untuk OS Android pada RAM >= 3GB, atau 768 MB pada perangkat <= 2GB
    val osReserve = if (totalRam <= 2048) 768 else 1200

    // Batas aman maksimal JVM game
    val safeMaxRam = if (Architecture.is32BitsDevice) {
        min(1024, (totalRam - 512).coerceAtLeast(384))
    } else {
        (totalRam - osReserve).coerceAtLeast(512)
    }

    val wasCapped = requestedRamMb > safeMaxRam
    val effectiveMax = if (wasCapped) safeMaxRam else requestedRamMb

    // Alokasi awal (-Xms) tidak boleh langsung sama dengan -Xmx agar OS tidak kekurangan memori saat start
    val initialHeap = min(512, (effectiveMax / 2)).coerceAtLeast(256)

    return AdaptiveHeapResult(
        initialHeapMb = initialHeap,
        maxHeapMb = effectiveMax,
        totalRamMb = totalRam,
        reservedOsMb = osReserve,
        wasCapped = wasCapped
    )
}

/**
 * 为设置项获取最大可设置的内存值（为系统预留一些可用内存）
 * PRD SH-02: Menjamin alokasi menyisakan minimal 1200 MB untuk OS Android.
 */
@WorkerThread
fun getMaxMemoryForSettings(context: Context): Int {
    val deviceRam = getTotalMemory(context).bytesToMB()
    val maxRam: Int = if (Architecture.is32BitsDevice || deviceRam < 2048) {
        min(1024.0, (deviceRam - 512).coerceAtLeast(384.0)).toInt()
    } else {
        // Sisakan minimal 1200 MB agar OS Android bernapas lega (PRD SH-02)
        (deviceRam - 1200).coerceAtLeast(512.0).toInt()
    }
    return maxRam
}


/**
 * 转换为 MB 单位
 */
fun Long.bytesToMB(decimals: Int = 2, roundDown: Boolean = false): Double {
    val megaBytes = this.toDouble() / BYTES_PER_MB
    return if (decimals == 0) {
        if (roundDown) floor(megaBytes) else round(megaBytes)
    } else {
        val roundingMode = if (roundDown) RoundingMode.DOWN else RoundingMode.HALF_UP
        BigDecimal(megaBytes).setScale(decimals, roundingMode).toDouble()
    }
}