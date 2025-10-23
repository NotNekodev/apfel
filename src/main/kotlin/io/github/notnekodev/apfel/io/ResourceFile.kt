package io.github.notnekodev.apfel.io

import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

@Suppress("unused")
object ResourceFile {
    fun get(path: String): File {
        val cleanPath = path.removePrefix("/")

        val resource = javaClass.classLoader.getResource(cleanPath)
            ?: throw IllegalArgumentException("Resource not found: $cleanPath")

        if (resource.protocol == "file") {
            return File(resource.toURI())
        }

        if (resource.protocol == "jar") {
            val inputStream = javaClass.classLoader.getResourceAsStream(cleanPath)
                ?: throw IllegalArgumentException("Could not open resource: $cleanPath")

            val extension = cleanPath.substringAfterLast('.', "")
            val tempFile = Files.createTempFile("resource_", ".$extension").toFile()
            tempFile.deleteOnExit()

            inputStream.use { input ->
                Files.copy(input, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }

            return tempFile
        }

        throw IllegalStateException("Unsupported resource protocol: ${resource.protocol}")
    }

    fun readText(path: String): String {
        return get(path).readText()
    }

    fun exists(path: String): Boolean {
        val cleanPath = path.removePrefix("/")
        return javaClass.classLoader.getResource(cleanPath) != null
    }
}