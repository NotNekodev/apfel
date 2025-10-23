package io.github.notnekodev.apfel.render.camera

import org.joml.Matrix4f
import io.github.notnekodev.apfel.render.Shader

class Projection(
    var fov: Float,
    var aspect: Float,
    var near: Float,
    var far: Float
) {
    fun getProjectionMatrix(): Matrix4f {
        return Matrix4f().perspective(fov, aspect, near, far)
    }

    fun uploadProjection(shader: Shader) {
        shader.setMat4("projection", getProjectionMatrix())
    }

    fun updateAspect(width: Int, height: Int) {
        aspect = width.toFloat() / height.toFloat()
    }
}