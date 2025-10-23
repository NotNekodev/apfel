package io.github.notnekodev.apfel.assets.types

import io.github.notnekodev.apfel.assets.Asset
import io.github.notnekodev.apfel.io.ResourceFile
import io.github.notnekodev.apfel.render.texture.Texture
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer

class TextureAsset(
    name: String,
    override val path: String,
    lazyLoad: Boolean = true
) : Asset(name, path, lazyLoad) {

    var texture: Texture? = null
        private set

    override fun onLoad() {
        println("Loading texture asset '$name' from $path")

        val file = ResourceFile.get(path)

        MemoryStack.stackPush().use { stack ->
            val w = stack.mallocInt(1)
            val h = stack.mallocInt(1)
            val channels = stack.mallocInt(1)

            STBImage.stbi_set_flip_vertically_on_load(true)
            val image: ByteBuffer = STBImage.stbi_load(file.absolutePath, w, h, channels, 4)
                ?: throw RuntimeException("Failed to load image '$path': ${STBImage.stbi_failure_reason()}")

            val width = w[0]
            val height = h[0]

            val pixels = ByteArray(width * height * 4)
            image.get(pixels)

            texture = Texture(width, height, pixels)

            texture?.upload()

            // TODO: Make this not crash, when freeing the image!
            // STBImage.stbi_image_free(image) we do a little memory leak
        }
    }

    override fun onUnload() {
        println("Unloading texture asset '$name'")
        texture?.destroy()
        texture = null
    }
}