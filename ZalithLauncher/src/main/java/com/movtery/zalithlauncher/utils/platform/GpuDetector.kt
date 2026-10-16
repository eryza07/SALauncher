package com.movtery.zalithlauncher.utils.platform

import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.EGLContext
import android.opengl.EGLDisplay
import android.opengl.EGLSurface
import android.opengl.GLES20
import android.os.Build
import com.movtery.zalithlauncher.game.renderer.renderers.GL4ESRenderer
import com.movtery.zalithlauncher.utils.logging.Logger
import java.io.File

/**
 * GPU Detection & Compatibility Analyzer
 * Detects GPU architecture (Mali, Adreno, PowerVR, etc.)
 * Mitigates MobileGL / Mali compatibility risks (PRD Section 12)
 */
object GpuDetector {
    private const val TAG = "GpuDetector"

    sealed class CompatibilityResult {
        object Compatible : CompatibilityResult()
        data class Warning(
            val message: String,
            val suggestedFallbackId: String? = null
        ) : CompatibilityResult()
    }

    private var cachedGpuRenderer: String? = null
    private var cachedGpuVendor: String? = null

    val gpuRenderer: String
        get() {
            if (cachedGpuRenderer == null) detectGpuInfo()
            return cachedGpuRenderer ?: "Unknown"
        }

    val gpuVendor: String
        get() {
            if (cachedGpuVendor == null) detectGpuInfo()
            return cachedGpuVendor ?: "Unknown"
        }

    val isMali: Boolean
        get() {
            val renderer = gpuRenderer.lowercase()
            val vendor = gpuVendor.lowercase()
            if (renderer.contains("mali") || vendor.contains("arm") || vendor.contains("mali")) {
                return true
            }
            // Check SoC info fallback
            val hardware = Build.HARDWARE.lowercase()
            val soc = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Build.SOC_MODEL else "").lowercase()
            val board = Build.BOARD.lowercase()
            return hardware.contains("exynos") || hardware.contains("kirin") ||
                    hardware.contains("mt") || hardware.contains("mediatek") ||
                    soc.contains("dimensity") || soc.contains("helio") ||
                    board.contains("exynos") || board.contains("kirin") || board.contains("mt")
        }

    val isAdreno: Boolean
        get() {
            val renderer = gpuRenderer.lowercase()
            val vendor = gpuVendor.lowercase()
            return renderer.contains("adreno") || vendor.contains("qualcomm") ||
                    Build.HARDWARE.lowercase().contains("qcom")
        }

    /**
     * Checks compatibility of chosen renderer against detected hardware
     */
    fun checkCompatibility(rendererIdOrName: String): CompatibilityResult {
        val lower = rendererIdOrName.lowercase()
        if (isMali && (lower.contains("mobilegl") || lower.contains("mobileglue"))) {
            return CompatibilityResult.Warning(
                message = "GPU Mali terdeteksi! Plugin MobileGL dapat menyebabkan instan crash (SIGSEGV) pada arsitektur GPU Mali. " +
                        "Disarankan beralih ke GL4ES bawaan atau Krypton Wrapper untuk stabilitas maksimal.",
                suggestedFallbackId = GL4ESRenderer.getUniqueIdentifier()
            )
        }
        return CompatibilityResult.Compatible
    }

    private synchronized fun detectGpuInfo() {
        if (cachedGpuRenderer != null && cachedGpuVendor != null) return

        runCatching {
            val display: EGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
            if (display == EGL14.EGL_NO_DISPLAY) return@runCatching

            val version = IntArray(2)
            if (!EGL14.eglInitialize(display, version, 0, version, 1)) return@runCatching

            val configAttribs = intArrayOf(
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT,
                EGL14.EGL_NONE
            )
            val configs = arrayOfNulls<EGLConfig>(1)
            val numConfigs = IntArray(1)
            if (!EGL14.eglChooseConfig(display, configAttribs, 0, configs, 0, 1, numConfigs, 0) || numConfigs[0] == 0) {
                EGL14.eglTerminate(display)
                return@runCatching
            }

            val contextAttribs = intArrayOf(
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL14.EGL_NONE
            )
            val context: EGLContext = EGL14.eglCreateContext(display, configs[0], EGL14.EGL_NO_CONTEXT, contextAttribs, 0)
            if (context == EGL14.EGL_NO_CONTEXT) {
                EGL14.eglTerminate(display)
                return@runCatching
            }

            val pbufferAttribs = intArrayOf(
                EGL14.EGL_WIDTH, 1,
                EGL14.EGL_HEIGHT, 1,
                EGL14.EGL_NONE
            )
            val surface: EGLSurface = EGL14.eglCreatePbufferSurface(display, configs[0], pbufferAttribs, 0)
            if (surface == EGL14.EGL_NO_SURFACE) {
                EGL14.eglDestroyContext(display, context)
                EGL14.eglTerminate(display)
                return@runCatching
            }

            if (EGL14.eglMakeCurrent(display, surface, surface, context)) {
                cachedGpuRenderer = GLES20.glGetString(GLES20.GL_RENDERER)
                cachedGpuVendor = GLES20.glGetString(GLES20.GL_VENDOR)
                Logger.info(TAG, "Detected GPU: $cachedGpuRenderer by $cachedGpuVendor")
            }

            EGL14.eglMakeCurrent(display, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)
            EGL14.eglDestroySurface(display, surface)
            EGL14.eglDestroyContext(display, context)
            EGL14.eglTerminate(display)
        }.onFailure { e ->
            Logger.warning(TAG, "Failed to query EGL GPU info, fallback to system properties", e)
        }

        if (cachedGpuRenderer == null) {
            cachedGpuRenderer = Build.HARDWARE
            cachedGpuVendor = Build.MANUFACTURER
        }
    }
}
