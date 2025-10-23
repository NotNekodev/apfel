package io.github.notnekodev.apfel.render

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

    fun render() {
        glBindVertexArray(vao)
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0)
    }

    fun delete()
}

private fun Float.toRadians(): Float = this / 180f * PI.toFloat()
