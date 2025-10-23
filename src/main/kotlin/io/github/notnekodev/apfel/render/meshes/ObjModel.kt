package io.github.notnekodev.apfel.render.meshes

import de.javagl.obj.ObjData
import de.javagl.obj.ObjReader
import de.javagl.obj.ObjUtils
import io.github.notnekodev.apfel.render.Mesh
import io.github.notnekodev.apfel.render.texture.Texture
import org.joml.Vector3f
import org.lwjgl.opengl.GL30.*
import java.io.InputStream

class ObjModel(input: InputStream) : Mesh {
    override val vao: Int = glGenVertexArrays()
    private val vbo: Int
    private val cbo: Int
    private val ebo: Int
    private val tbo: Int
    private val nbo: Int
    override var vertexCount: Int

    override var position = Vector3f(0f, 0f, 0f)
    override var rotation = Vector3f(0f, 0f, 0f)
    override var scale = Vector3f(1f, 1f, 1f)

    override var texture: Texture? = null

    init {
        val obj = ObjUtils.convertToRenderable(ObjReader.read(input))

        val indices = ObjData.getFaceVertexIndices(obj)
        vertexCount = indices.limit()

        val vertices = ObjData.getVertices(obj)
        val colors = FloatArray((vertices.limit() / 3) * 3) { 1f }
        val texCoords = ObjData.getTexCoords(obj, 2)
        val normals = ObjData.getNormals(obj)  // Add this

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

        tbo = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, tbo)
        glBufferData(GL_ARRAY_BUFFER, texCoords, GL_STATIC_DRAW)
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0)
        glEnableVertexAttribArray(2)

        nbo = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, nbo)
        glBufferData(GL_ARRAY_BUFFER, normals, GL_STATIC_DRAW)
        glVertexAttribPointer(3, 3, GL_FLOAT, false, 0, 0)
        glEnableVertexAttribArray(3)

        ebo = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)
    }

    fun setPosition(x: Float, y: Float, z: Float): ObjModel {
        position.set(x, y, z)
        return this
    }

    fun setRotation(x: Float, y: Float, z: Float): ObjModel {
        rotation.set(x, y, z)
        return this
    }

    fun setScale(x: Float, y: Float, z: Float): ObjModel {
        scale.set(x, y, z)
        return this
    }

    override fun delete() {
        glDeleteVertexArrays(vao)
        glDeleteBuffers(vbo)
        glDeleteBuffers(cbo)
        glDeleteBuffers(ebo)
        glDeleteBuffers(tbo)
        glDeleteBuffers(nbo)
    }
}