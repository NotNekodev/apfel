package io.github.notnekodev.apfel.render.camera

import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class Camera(
    var position: Vector3f = Vector3f(0f, 0f, 3f),
    var yaw: Float = -90f,
    var pitch: Float = 0f
) {
    var front = Vector3f(0f, 0f, -1f)
    private val up = Vector3f(0f, 1f, 0f)
    private val right = Vector3f(1f, 0f, 0f)
    private val worldUp = Vector3f(0f, 1f, 0f)
    private val movementSpeed = 2.5f
    private val mouseSensitivity = 0.1f

    init {
        updateVectors()
    }

    private fun updateVectors() {
        val yawRad = Math.toRadians(yaw.toDouble()).toFloat()
        val pitchRad = Math.toRadians(pitch.toDouble()).toFloat()

        front.set(
            cos(yawRad) * cos(pitchRad),
            sin(pitchRad),
            sin(yawRad) * cos(pitchRad)
        ).normalize()

        val r = Vector3f(front).cross(worldUp).normalize()
        val u = Vector3f(r).cross(front).normalize()

        right.set(r)
        up.set(u)
    }

    fun getViewMatrix(): Matrix4f {
        val center = Vector3f(position).add(front)
        return Matrix4f().lookAt(position, center, up)
    }

    var smoothX = 0.0f
    var smoothY = 0.0f
    val smoothing = 0.3f

    fun processMouseMovement(rawX: Float, rawY: Float) {
        smoothX += (rawX - smoothX) * smoothing
        smoothY += (rawY - smoothY) * smoothing

        yaw += smoothX * mouseSensitivity
        pitch += smoothY * mouseSensitivity

        pitch = pitch.coerceIn(-89f, 89f)

        updateVectors()
    }

    fun processKeyboard(direction: String, deltaTime: Float) {
        val velocity = movementSpeed * deltaTime

        val horizontalFront = Vector3f(front.x, 0f, front.z).normalize()
        val horizontalRight = Vector3f(right.x, 0f, right.z).normalize()

        when (direction) {
            "FORWARD"  -> position.add(Vector3f(horizontalFront).mul(velocity))
            "BACKWARD" -> position.sub(Vector3f(horizontalFront).mul(velocity))
            "LEFT"     -> position.sub(Vector3f(horizontalRight).mul(velocity))
            "RIGHT"    -> position.add(Vector3f(horizontalRight).mul(velocity))
            "UP"       -> position.add(Vector3f(worldUp).mul(velocity))
            "DOWN"     -> position.sub(Vector3f(worldUp).mul(velocity))
        }
    }

    fun uploadView(shader: io.github.notnekodev.apfel.render.Shader) {
        val viewMatrix = getViewMatrix()
        shader.setMat4("view", viewMatrix)
    }

    fun lookAt(target: Vector3f) {
        val dir = Vector3f(target).sub(position).normalize()
        if (dir.length() < 1e-6f) return

        yaw = Math.toDegrees(atan2(dir.z.toDouble(), dir.x.toDouble())).toFloat()
        pitch = Math.toDegrees(asin(dir.y.coerceIn(-1.0f, 1.0f).toDouble())).toFloat()

        updateVectors()
    }
}
