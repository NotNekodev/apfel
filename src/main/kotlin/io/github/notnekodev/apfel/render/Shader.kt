package io.github.notnekodev.apfel.render

import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL20.*

@Suppress("unused")
class Shader(val program: Int) {
    private val uniformCache = mutableMapOf<String, Int>()

    fun use() {
        glUseProgram(program)
    }

    private fun getUniformLocation(name: String): Int {
        return uniformCache.getOrPut(name) {
            glGetUniformLocation(program, name)
        }
    }

    fun setInt(name: String, value: Int) {
        glUniform1i(getUniformLocation(name), value)
    }

    fun setFloat(name: String, value: Float) {
        glUniform1f(getUniformLocation(name), value)
    }

    fun setVec3(name: String, x: Float, y: Float, z: Float) {
        glUniform3f(getUniformLocation(name), x, y, z)
    }

    fun setVec3(name: String, vec: Vector3f) {
        glUniform3f(getUniformLocation(name), vec.x, vec.y, vec.z)
    }

    fun setMat4(name: String, matrix: Matrix4f) {
        glUniformMatrix4fv(getUniformLocation(name), false, matrix.get(FloatArray(16)))
    }

    fun setBool(name: String, value: Boolean) {
        glUniform1i(getUniformLocation(name), if (value) 1 else 0)
    }

    fun delete() {
        glDeleteProgram(program)
    }
}