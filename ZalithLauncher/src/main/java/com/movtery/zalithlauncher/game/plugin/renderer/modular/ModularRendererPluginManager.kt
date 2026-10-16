package com.movtery.zalithlauncher.game.plugin.renderer.modular

import android.content.Context
import com.movtery.zalithlauncher.game.renderer.RendererInterface
import com.movtery.zalithlauncher.path.PathManager
import com.movtery.zalithlauncher.utils.logging.Logger
import com.movtery.zalithlauncher.utils.platform.GpuDetector
import org.apache.commons.io.FileUtils
import org.json.JSONObject
import java.io.File

private const val TAG = "ModularRendererPlugin"

/**
 * Modular Dynamic Native Renderer Plugin (.so)
 * Supports loading third-party renderers (MobileGL, custom GL4ES, Zink, ANGLE)
 * from plugins/renderers/ directory.
 */
class ModularRendererPlugin(
    private val id: String,
    private val uniqueId: String,
    private val displayName: String,
    private val summaryText: String?,
    private val sourceSoFile: File,
    private val envVars: Map<String, String> = emptyMap(),
    private val dlopenLibs: List<String> = emptyList(),
    private val minMc: String? = null,
    private val maxMc: String? = null
) : RendererInterface {

    /**
     * Internal executable copy/symlink to adhere to Android W^X / noexec policies on external storage
     */
    private val targetInternalFile: File by lazy {
        val dest = File(PathManager.DIR_MODULAR_RENDERERS_INTERNAL, sourceSoFile.name)
        syncExecutableLibrary(sourceSoFile, dest)
        dest
    }

    override fun getRendererId(): String = id

    override fun getUniqueIdentifier(): String = uniqueId

    override fun getRendererName(): String = displayName

    override fun getRendererSummary(): String? {
        val maliWarning = if (GpuDetector.isMali && (displayName.lowercase().contains("mobilegl") || sourceSoFile.name.lowercase().contains("mobilegl"))) {
            " ⚠️ Peringatan: GPU Mali terdeteksi, rawan crash SIGSEGV."
        } else ""
        return (summaryText ?: "Modular Plugin (${sourceSoFile.name})") + maliWarning
    }

    override fun getMinMCVersion(): String? = minMc

    override fun getMaxMCVersion(): String? = maxMc

    override fun getRendererEnv(): Lazy<Map<String, String>> = lazy {
        val baseEnv = mutableMapOf<String, String>()
        if (id.startsWith("opengles3")) {
            baseEnv["LIBGL_USE_MC_COLOR"] = "1"
            baseEnv["LIBGL_GL"] = "31"
            baseEnv["LIBGL_ES"] = "3"
            baseEnv["LIBGL_NORMALIZE"] = "1"
            baseEnv["LIBGL_NOERROR"] = "1"
        }
        baseEnv.putAll(envVars)
        baseEnv
    }

    override fun getDlopenLibrary(): Lazy<List<String>> = lazy { dlopenLibs }

    override fun getRendererLibrary(): String {
        // Return executable path from internal sandbox
        syncExecutableLibrary(sourceSoFile, targetInternalFile)
        return targetInternalFile.absolutePath
    }

    override fun getRendererEGL(): String? = null

    companion object {
        fun syncExecutableLibrary(source: File, destination: File) {
            runCatching {
                if (!destination.parentFile.exists()) destination.parentFile.mkdirs()
                if (!destination.exists() || destination.length() != source.length() || destination.lastModified() < source.lastModified()) {
                    FileUtils.copyFile(source, destination)
                    destination.setReadable(true, false)
                    destination.setExecutable(true, false)
                    Logger.info(TAG, "Synced modular renderer .so from ${source.absolutePath} to ${destination.absolutePath}")
                }
            }.onFailure { e ->
                Logger.error(TAG, "Failed to sync executable library: ${source.absolutePath}", e)
            }
        }
    }
}

/**
 * Manager for scanning and registering Modular Renderer Plugins (.so)
 * from Android/data/com.eryza.salauncher/files/plugins/renderers/
 */
object ModularRendererPluginManager {
    private val loadedPlugins: MutableList<ModularRendererPlugin> = mutableListOf()

    fun getLoadedPlugins(): List<ModularRendererPlugin> = loadedPlugins

    /**
     * Scans plugins/renderers/ and loads all detected .so plugins into Renderers
     */
    fun loadPlugins(context: Context): List<ModularRendererPlugin> {
        loadedPlugins.clear()
        val pluginsDir = PathManager.DIR_RENDERER_PLUGINS
        if (!pluginsDir.exists()) {
            pluginsDir.mkdirs()
            return emptyList()
        }

        // 1. Scan direct .so files in plugins/renderers/
        pluginsDir.listFiles { file -> file.isFile && file.name.endsWith(".so") }?.forEach { soFile ->
            parseSoFile(soFile, null)?.let { loadedPlugins.add(it) }
        }

        // 2. Scan subdirectories in plugins/renderers/ (e.g. plugins/renderers/MobileGL/)
        pluginsDir.listFiles { file -> file.isDirectory }?.forEach { subDir ->
            val jsonConfig = File(subDir, "renderer.json").takeIf { it.exists() }
                ?: File(subDir, "plugin.json").takeIf { it.exists() }
                ?: File(subDir, "manifest.json").takeIf { it.exists() }

            subDir.listFiles { file -> file.isFile && file.name.endsWith(".so") }?.forEach { soFile ->
                parseSoFile(soFile, jsonConfig)?.let { loadedPlugins.add(it) }
            }
        }

        Logger.info(TAG, "Loaded ${loadedPlugins.size} modular renderer plugins from ${pluginsDir.absolutePath}")
        return loadedPlugins
    }

    private fun parseSoFile(soFile: File, configFile: File?): ModularRendererPlugin? {
        return runCatching {
            val fileName = soFile.name
            val baseName = fileName.removePrefix("lib").removeSuffix(".so")

            var displayName = when {
                baseName.contains("mobilegl", ignoreCase = true) -> "MobileGL (Plugin SO)"
                baseName.contains("gl4es", ignoreCase = true) -> "GL4ES ($baseName Plugin)"
                baseName.contains("zink", ignoreCase = true) -> "Zink Vulkan ($baseName Plugin)"
                baseName.contains("angle", ignoreCase = true) -> "ANGLE ($baseName Plugin)"
                baseName.contains("virgl", ignoreCase = true) -> "VirGL ($baseName Plugin)"
                else -> "$baseName (Custom SO Plugin)"
            }

            var rendererId = when {
                baseName.contains("zink", ignoreCase = true) || baseName.contains("vulkan", ignoreCase = true) -> "vulkan"
                baseName.contains("gl4es_114", ignoreCase = true) -> "opengles2"
                else -> "opengles3"
            }

            var summary = "Modular native library: $fileName"
            val envMap = mutableMapOf<String, String>()
            val dlopenLibs = mutableListOf<String>()
            var minMc: String? = null
            var maxMc: String? = null

            // Read JSON metadata if present
            if (configFile != null && configFile.exists()) {
                val jsonStr = configFile.readText(Charsets.UTF_8)
                val json = JSONObject(jsonStr)
                if (json.has("name")) displayName = json.getString("name")
                if (json.has("id")) rendererId = json.getString("id")
                if (json.has("summary")) summary = json.getString("summary")
                if (json.has("minMCVer")) minMc = json.getString("minMCVer")
                if (json.has("maxMCVer")) maxMc = json.getString("maxMCVer")

                if (json.has("env")) {
                    val envJson = json.getJSONObject("env")
                    for (key in envJson.keys()) {
                        envMap[key] = envJson.getString(key)
                    }
                }
                if (json.has("dlopen")) {
                    val dlArray = json.getJSONArray("dlopen")
                    for (i in 0 until dlArray.length()) {
                        dlopenLibs.add(dlArray.getString(i))
                    }
                }
            }

            val uniqueId = "modular_renderer_${soFile.parentFile.name}_${soFile.nameWithoutExtension}"

            ModularRendererPlugin(
                id = rendererId,
                uniqueId = uniqueId,
                displayName = displayName,
                summaryText = summary,
                sourceSoFile = soFile,
                envVars = envMap,
                dlopenLibs = dlopenLibs,
                minMc = minMc,
                maxMc = maxMc
            )
        }.onFailure { e ->
            Logger.error(TAG, "Failed to parse modular renderer plugin: ${soFile.absolutePath}", e)
        }.getOrNull()
    }
}
