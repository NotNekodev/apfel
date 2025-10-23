package io.github.notnekodev.apfel.render

import io.github.notnekodev.apfel.io.ResourceFile
import org.lwjgl.opengl.GL20.*
import java.io.File
import java.nio.file.*

@Suppress("unused")
class ShaderManager {
    private val shaders = mutableMapOf<String, Shader>()
    private val watchService: WatchService = FileSystems.getDefault().newWatchService()
    private val watchKeys = mutableMapOf<WatchKey, Path>()

    data class Shader(
        var shaderWrapper: io.github.notnekodev.apfel.render.Shader,
        val vertexPath: String,
        val fragmentPath: String,
        var lastModified: Long
    )

    fun loadShader(name: String, vertexPath: String, fragmentPath: String, useResources: Boolean = true): io.github.notnekodev.apfel.render.Shader {
        if (useResources) {
            val vertFile = ResourceFile.get(vertexPath)
            val fragFile = ResourceFile.get(fragmentPath)

            val program = compileAndLinkShader(vertFile, fragFile)
            val lastModified = maxOf(vertFile.lastModified(), fragFile.lastModified())
            val shader = Shader(program)

            shaders[name] = Shader(shader, vertFile.absolutePath, fragFile.absolutePath, lastModified)
            println("Loaded shader '$name' from resources (ID: $program) - hot reload disabled")
            return shader
        } else {
            val vertFile = File(vertexPath)
            val fragFile = File(fragmentPath)

            if (!vertFile.exists() || !fragFile.exists()) {
                throw IllegalArgumentException("Shader files not found: $vertexPath or $fragmentPath")
            }

            val program = compileAndLinkShader(vertFile, fragFile)
            val lastModified = maxOf(vertFile.lastModified(), fragFile.lastModified())
            val shader = Shader(program)

            shaders[name] = Shader(shader, vertFile.absolutePath, fragFile.absolutePath, lastModified)

            // Enable hot reload
            registerWatch(vertFile.parentFile.toPath())
            registerWatch(fragFile.parentFile.toPath())

            println("Loaded shader '$name' from filesystem (ID: $program) - hot reload enabled")
            return shader
        }
    }

    fun loadShaderFromSource(name: String, vertexSource: String, fragmentSource: String): io.github.notnekodev.apfel.render.Shader {
        val program = compileAndLinkShaderFromSource(vertexSource, fragmentSource)
        val shader = Shader(program)
        println("Loaded shader '$name' from source (ID: $program)")
        return shader
    }

    private fun registerWatch(path: Path) {
        if (!watchKeys.values.contains(path)) {
            val key = path.register(
                watchService,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_CREATE
            )
            watchKeys[key] = path
        }
    }

    fun checkForReloads() {
        val key = watchService.poll() ?: return
        val dir = watchKeys[key] ?: return

        for (event in key.pollEvents()) {
            val kind = event.kind()
            if (kind == StandardWatchEventKinds.OVERFLOW) continue

            val filename = event.context() as Path
            val fullPath = dir.resolve(filename)

            shaders.forEach { (name, shader) ->
                val vertFile = File(shader.vertexPath)
                val fragFile = File(shader.fragmentPath)

                if (fullPath == vertFile.toPath() || fullPath == fragFile.toPath()) {
                    val currentModified = maxOf(vertFile.lastModified(), fragFile.lastModified())
                    if (currentModified > shader.lastModified) {
                        println("Detected change in shader '$name', reloading...")
                        reloadShader(name, shader)
                    }
                }
            }
        }

        key.reset()
    }

    private fun reloadShader(name: String, shader: Shader) {
        try {
            val vertFile = File(shader.vertexPath)
            val fragFile = File(shader.fragmentPath)
            val newProgram = compileAndLinkShader(vertFile, fragFile)
            shader.shaderWrapper.delete()
            shader.shaderWrapper = Shader(newProgram)
            shader.lastModified = maxOf(vertFile.lastModified(), fragFile.lastModified())
            println("Successfully reloaded shader '$name' (new ID: $newProgram)")
        } catch (e: Exception) {
            println("Failed to reload shader '$name': ${e.message}")
        }
    }

    private fun compileAndLinkShader(vertexFile: File, fragmentFile: File): Int {
        val vertexSource = vertexFile.readText()
        val fragmentSource = fragmentFile.readText()
        return compileAndLinkShaderFromSource(vertexSource, fragmentSource)
    }

    private fun compileAndLinkShaderFromSource(vertexSource: String, fragmentSource: String): Int {
        val vertexShader = glCreateShader(GL_VERTEX_SHADER)
        glShaderSource(vertexShader, vertexSource)
        glCompileShader(vertexShader)
        checkCompileErrors(vertexShader, "VERTEX")

        val fragmentShader = glCreateShader(GL_FRAGMENT_SHADER)
        glShaderSource(fragmentShader, fragmentSource)
        glCompileShader(fragmentShader)
        checkCompileErrors(fragmentShader, "FRAGMENT")

        val program = glCreateProgram()
        glAttachShader(program, vertexShader)
        glAttachShader(program, fragmentShader)
        glLinkProgram(program)
        checkLinkErrors(program)

        glDeleteShader(vertexShader)
        glDeleteShader(fragmentShader)

        return program
    }

    private fun checkCompileErrors(shader: Int, type: String) {
        val success = glGetShaderi(shader, GL_COMPILE_STATUS)
        if (success == GL_FALSE) {
            val infoLog = glGetShaderInfoLog(shader)
            throw RuntimeException("Shader compilation error ($type):\n$infoLog")
        }
    }

    private fun checkLinkErrors(program: Int) {
        val success = glGetProgrami(program, GL_LINK_STATUS)
        if (success == GL_FALSE) {
            val infoLog = glGetProgramInfoLog(program)
            throw RuntimeException("Shader linking error:\n$infoLog")
        }
    }

    fun getShader(name: String): Int? {
        return shaders[name]?.shaderWrapper?.program
    }

    fun useShader(name: String) {
        shaders[name]?.let { glUseProgram(it.shaderWrapper.program) }
    }

    fun cleanup() {
        shaders.values.forEach { it.shaderWrapper.delete() }
        shaders.clear()
        watchService.close()
    }
}