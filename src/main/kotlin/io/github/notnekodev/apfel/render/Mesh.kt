package io.github.notnekodev.apfel.render

import io.github.notnekodev.apfel.render.texture.Texture
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL30.*
import kotlin.math.*

interface Mesh {
    val vao: Int
    val vertexCount: Int

    var position: Vector3f
    var rotation: Vector3f
    var scale: Vector3f
    var texture: Texture?

    fun getModelMatrix(): Matrix4f {
        return Matrix4f()
            .translate(position)
            .rotateXYZ(
                rotation.x.toRadians(),
                rotation.y.toRadians(),
                rotation.z.toRadians()
            )
            .scale(scale)
    }

    fun setTexture(tex: Texture): Mesh {
        texture = tex
        return this
    }

    fun render() {
        texture?.bind(0)
        glBindVertexArray(vao)
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0)
    }

    fun delete()
}

private fun Float.toRadians(): Float = this / 180f * PI.toFloat()