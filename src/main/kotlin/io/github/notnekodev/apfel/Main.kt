package io.github.notnekodev.apfel

import io.github.notnekodev.apfel.assets.AssetManager
import io.github.notnekodev.apfel.assets.types.TextureAsset
import io.github.notnekodev.apfel.input.InputManager
import io.github.notnekodev.apfel.input.Keybind
import io.github.notnekodev.apfel.render.meshes.Cube
import io.github.notnekodev.apfel.render.Window
import io.github.notnekodev.apfel.render.ShaderManager
import io.github.notnekodev.apfel.render.camera.Camera
import io.github.notnekodev.apfel.render.camera.Projection
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback
import org.lwjgl.opengl.GL11.glClearColor
import org.lwjgl.opengl.GL11.glViewport

object Main {
    var window: Window = Window(800, 600, "apfel")
    private val assets = AssetManager()

    @JvmStatic
    fun main(args: Array<String>) {
        loop()
        window.delete()
    }

    private fun loop() {
        val shaderManager = ShaderManager()
        val shader = shaderManager.loadShader("basic", "shaders/basic.vert", "shaders/basic.frag")

        val input = InputManager(window)
        input.register(
            Keybind.forward(),
            Keybind.backward(),
            Keybind.left(),
            Keybind.right(),
            Keybind.sprint(),
            Keybind.pause(),
            Keybind.jump(),
            Keybind.crouch(),
        )

        input.mouseLocked = true
        input.mouseSensitivity = 500.0f

        val camera = Camera(Vector3f(0f, 0f, 3f))
        val projection = Projection(Math.toRadians(70.0).toFloat(), 800f / 600f, 0.1f, 100f)

        val windexTextureAsset = assets.register(TextureAsset("windex", "textures/windex.png", lazyLoad = true))
        windexTextureAsset.load()
        val windexTexture = windexTextureAsset.texture!!

        val cube = Cube()
        cube.setPosition(0f, 0f, -3f)
            .setRotation(0f, 0f, 0f)
            .setScale(2f, 2f, 2f)
            .setTexture(windexTexture)

        camera.lookAt(cube.position)

        val cube2 = Cube()
        cube2.setPosition(3f, 0f, -3f)
            .setScale(1f, 1f, 1f)
            .setTexture(windexTexture)

        var lastFrame = 0.0
        var deltaTime: Float

        glfwSetWindowSizeCallback(window.handle) { _, width, height ->
            glViewport(0, 0, width, height)
            projection.updateAspect(width, height)
        }

        while (!window.shouldClose()) {
            val currentFrame = GLFW.glfwGetTime()
            deltaTime = (currentFrame - lastFrame).toFloat()
            lastFrame = currentFrame

            shaderManager.checkForReloads()

            if (input.isDown("forward")) camera.processKeyboard("FORWARD", deltaTime)
            if (input.isDown("backward")) camera.processKeyboard("BACKWARD", deltaTime)
            if (input.isDown("left")) camera.processKeyboard("LEFT", deltaTime)
            if (input.isDown("right")) camera.processKeyboard("RIGHT", deltaTime)
            if (input.isDown("jump")) camera.processKeyboard("UP", deltaTime)
            if (input.isDown("crouch")) camera.processKeyboard("DOWN", deltaTime)

            camera.processMouseMovement(input.mouseDeltaX * deltaTime, input.mouseDeltaY * deltaTime)
            input.resetMouseDelta()

            window.clear()
            glClearColor(0f, 0f, 0f, 1f)

            shader.use()
            shader.setBool("hasTexture", cube.texture != null)
            shader.setMat4("model", cube.getModelMatrix())
            camera.uploadView(shader)
            projection.uploadProjection(shader)
            cube.render()

            shader.use()
            shader.setBool("hasTexture", cube2.texture != null)
            shader.setMat4("model", cube2.getModelMatrix())
            camera.uploadView(shader)
            projection.uploadProjection(shader)
            cube2.render()

            window.swap()
            window.poll()
            input.update()
        }

        cube.delete()
        cube2.delete()
        shaderManager.cleanup()
    }
}