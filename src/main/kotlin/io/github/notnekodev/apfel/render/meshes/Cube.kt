package io.github.notnekodev.apfel.render.meshes

import io.github.notnekodev.apfel.render.Mesh
import org.joml.Vector3f
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.*

class Cube : Mesh {
    override val vao = glGenVertexArrays()
    private val vbo: Int
    private val cbo: Int
    private val ebo: Int
    override val vertexCount: Int

    override var position = Vector3f(0f, 0f, 0f)
    override var rotation = Vector3f(0f, 0f, 0f)
    override var scale = Vector3f(1f, 1f, 1f)

    init {
        val vertices = floatArrayOf(
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f,  0.5f, -0.5f,
            -0.5f,  0.5f, -0.5f,
            -0.5f, -0.5f,  0.5f,
            0.5f, -0.5f,  0.5f,
            0.5f,  0.5f,  0.5f,
            -0.5f,  0.5f,  0.5f
        )

        val indices = intArrayOf(
            0,1,2, 2,3,0,
            4,5,6, 6,7,4,
            0,4,7, 7,3,0,
            1,5,6, 6,2,1,
            3,2,6, 6,7,3,
            0,1,5, 5,4,0
        )
        vertexCount = indices.size

        val colors = floatArrayOf(
            1f,0f,0f, 1f,0f,0f, 1f,0f,0f, 1f,0f,0f,
            0f,1f,0f, 0f,1f,0f, 0f,1f,0f, 0f,1f,0f
        )

        glBindVertexArray(vao)

        vbo = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)
        glEnableVertexAttribArray(0)

        cbo = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, cbo)
        glBufferData(GL_ARRAY_BUFFER, colors, GL_STATIC_DRAW)
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0)
        glEnableVertexAttribArray(1)

        ebo = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)
    }

    fun setPosition(x: Float, y: Float, z: Float): Cube {
        position.set(x, y, z)
        return this
    }

    fun setRotation(x: Float, y: Float, z: Float): Cube {
        rotation.set(x, y, z)
        return this
    }

    fun setScale(x: Float, y: Float, z: Float): Cube {
        scale.set(x, y, z)
        return this
    }

    override fun delete() {
        glDeleteVertexArrays(vao)
        glDeleteBuffers(vbo)
        glDeleteBuffers(cbo)
        glDeleteBuffers(ebo)
    }
}
