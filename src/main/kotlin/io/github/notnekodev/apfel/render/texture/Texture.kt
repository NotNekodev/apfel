package io.github.notnekodev.apfel.render.texture

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL30
import java.nio.ByteBuffer

class Texture(
    val width: Int,
    val height: Int,
    private val pixels: ByteArray,
    private val generateMipmaps: Boolean = true,
    private val minFilter: Int = GL11.GL_LINEAR_MIPMAP_LINEAR,
    private val magFilter: Int = GL11.GL_LINEAR,
    private val wrapS: Int = GL11.GL_REPEAT,
    private val wrapT: Int = GL11.GL_REPEAT
) {
    var glId: Int? = null

    fun upload() {
        if (glId != null) return

        glId = GL11.glGenTextures()
        bind()

        val buffer: ByteBuffer = BufferUtils.createByteBuffer(pixels.size)
        buffer.put(pixels)
        buffer.flip()

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0,
            GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer)

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, wrapS)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, wrapT)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, minFilter)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, magFilter)

        if (generateMipmaps) GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)

        unbind()
    }

    fun bind(unit: Int = 0) {
        if (glId == null) upload()
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, glId!!)
    }

    fun unbind(unit: Int = 0) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
    }

    fun destroy() {
        glId?.let {
            GL11.glDeleteTextures(it)
            glId = null
        }
    }
}
