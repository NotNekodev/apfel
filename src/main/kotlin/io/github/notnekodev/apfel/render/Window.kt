package io.github.notnekodev.apfel.render

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil.NULL

@Suppress("unused")
class Window(width: Int, height: Int, title: String) {
    var handle: Long

    init {
        if (!glfwInit()) error("Unable to initialize GLFW")

        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)

        handle = glfwCreateWindow(width, height, title, NULL, NULL)
        if (handle == NULL) {
            throw RuntimeException("Failed to create the GLFW window")
        }

        glfwMakeContextCurrent(handle)
        glfwSwapInterval(1)
        glfwShowWindow(handle)

        GL.createCapabilities()

        glEnable(GL_DEPTH_TEST)
        glClearColor(0f, 0f, 0f, 1f)
    }

    fun delete() {
        glfwDestroyWindow(handle)
        glfwTerminate()
    }

    fun clear() = glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

    fun isKeyDown(key: Int): Boolean = glfwGetKey(handle, key) == GLFW_PRESS

    fun poll() = glfwPollEvents()
    fun shouldClose(): Boolean = glfwWindowShouldClose(handle)
    fun swap() = glfwSwapBuffers(handle)
}