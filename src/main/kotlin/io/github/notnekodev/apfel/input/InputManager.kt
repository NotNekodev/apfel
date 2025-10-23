package io.github.notnekodev.apfel.input

import io.github.notnekodev.apfel.render.Window
import org.lwjgl.glfw.GLFW

@Suppress("unused")
class InputManager(private val window: Window) {
    private val keybindings = mutableMapOf<String, Keybind>()
    private val keyStates = mutableMapOf<Int, KeyState>()
    private var currentModifiers = 0

    var mouseX = 0.0
        private set
    var mouseY = 0.0
        private set
    var mouseDeltaX = 0f
        private set
    var mouseDeltaY = 0f
        private set
    private var lastMouseX = 0.0
    private var lastMouseY = 0.0
    private var firstMouse = true

    var mouseSensitivity = 1.0f
    var mouseLocked = false
        set(value) {
            if (field != value) {
                field = value
                if (value) {
                    GLFW.glfwSetInputMode(window.handle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED)
                    firstMouse = true
                } else {
                    GLFW.glfwSetInputMode(window.handle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL)
                }
            }
        }

    data class KeyState(
        var pressed: Boolean = false,
        var justPressed: Boolean = false,
        var justReleased: Boolean = false
    )

    init {
        setupCallbacks()
    }

    private fun setupCallbacks() {
        GLFW.glfwSetKeyCallback(window.handle) { _, key, _, action, mods ->
            currentModifiers = mods
            when (action) {
                GLFW.GLFW_PRESS -> {
                    val state = keyStates.getOrPut(key) { KeyState() }
                    state.pressed = true
                    state.justPressed = true
                }
                GLFW.GLFW_RELEASE -> {
                    val state = keyStates.getOrPut(key) { KeyState() }
                    state.pressed = false
                    state.justReleased = true
                }
            }
        }

        GLFW.glfwSetCursorPosCallback(window.handle) { _, xpos, ypos ->
            mouseX = xpos
            mouseY = ypos

            if (firstMouse) {
                lastMouseX = xpos
                lastMouseY = ypos
                firstMouse = false
                mouseDeltaX = 0f
                mouseDeltaY = 0f
                return@glfwSetCursorPosCallback
            }

            mouseDeltaX = ((xpos - lastMouseX) * mouseSensitivity).toFloat()
            mouseDeltaY = ((lastMouseY - ypos) * mouseSensitivity).toFloat()

            lastMouseX = xpos
            lastMouseY = ypos
        }

        GLFW.glfwSetWindowFocusCallback(window.handle) { _, focused ->
            if (focused && mouseLocked) {
                firstMouse = true
            }
        }
    }

    fun register(keybinding: Keybind) {
        keybindings[keybinding.name] = keybinding
    }

    fun register(vararg keybindings: Keybind) {
        keybindings.forEach { register(it) }
    }

    private fun modifiersMatch(required: Int): Boolean {
        if (required == 0) return true
        return (currentModifiers and required) == required
    }

    fun isDown(bindingName: String): Boolean {
        val binding = keybindings[bindingName] ?: return false
        val keyPressed = keyStates[binding.key.keyCode]?.pressed ?: false
        return keyPressed && modifiersMatch(binding.key.modifiers)
    }

    fun isPressed(bindingName: String): Boolean {
        val binding = keybindings[bindingName] ?: return false
        val keyJustPressed = keyStates[binding.key.keyCode]?.justPressed ?: false
        return keyJustPressed && modifiersMatch(binding.key.modifiers)
    }

    fun isReleased(bindingName: String): Boolean {
        val binding = keybindings[bindingName] ?: return false
        val keyJustReleased = keyStates[binding.key.keyCode]?.justReleased ?: false
        return keyJustReleased && modifiersMatch(binding.key.modifiers)
    }

    fun isKeyDown(keyCode: Int): Boolean {
        return keyStates[keyCode]?.pressed ?: false
    }

    fun isKeyPressed(keyCode: Int): Boolean {
        return keyStates[keyCode]?.justPressed ?: false
    }

    fun isKeyReleased(keyCode: Int): Boolean {
        return keyStates[keyCode]?.justReleased ?: false
    }

    fun isKeyDown(key: Key): Boolean {
        val keyPressed = keyStates[key.keyCode]?.pressed ?: false
        return keyPressed && modifiersMatch(key.modifiers)
    }

    fun isKeyPressed(key: Key): Boolean {
        val keyJustPressed = keyStates[key.keyCode]?.justPressed ?: false
        return keyJustPressed && modifiersMatch(key.modifiers)
    }

    fun getBinding(name: String): Keybind? = keybindings[name]

    fun rebind(bindingName: String, newKey: Key): Boolean {
        val binding = keybindings[bindingName] ?: return false
        binding.key = newKey
        return true
    }

    fun rebind(bindingName: String, newKeyCode: Int): Boolean {
        return rebind(bindingName, Key(newKeyCode, 0))
    }

    fun resetAllBindings() {
        keybindings.values.forEach { it.reset() }
    }

    fun update() {
        keyStates.values.forEach {
            it.justPressed = false
            it.justReleased = false
        }
    }

    fun resetMouseDelta() {
        mouseDeltaX = 0f
        mouseDeltaY = 0f
    }

    fun getAllBindings(): Map<String, Keybind> = keybindings.toMap()

    fun getCurrentModifiers(): Int = currentModifiers
}