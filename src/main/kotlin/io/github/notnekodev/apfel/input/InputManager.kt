package io.github.notnekodev.apfel.input

import io.github.notnekodev.apfel.render.Window
import org.lwjgl.glfw.GLFW

class InputManager(private val window: Window) {
    private val keybindings = mutableMapOf<String, Keybind>()
    private val keyStates = mutableMapOf<Int, KeyState>()
    private var currentModifiers = 0

    // Mouse state
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
                // Don't calculate delta on first mouse movement
                mouseDeltaX = 0f
                mouseDeltaY = 0f
                return@glfwSetCursorPosCallback
            }

            mouseDeltaX = ((xpos - lastMouseX) * mouseSensitivity).toFloat()
            mouseDeltaY = ((lastMouseY - ypos) * mouseSensitivity).toFloat()

            lastMouseX = xpos
            lastMouseY = ypos
        }

        // Reset firstMouse when window gains focus to prevent jumps
        GLFW.glfwSetWindowFocusCallback(window.handle) { _, focused ->
            if (focused && mouseLocked) {
                firstMouse = true
            }
        }
    }

    // Register a keybinding
    fun register(keybinding: Keybind) {
        keybindings[keybinding.name] = keybinding
    }

    // Register multiple keybindings
    fun register(vararg keybindings: Keybind) {
        keybindings.forEach { register(it) }
    }

    // Check if modifiers match
    private fun modifiersMatch(required: Int): Boolean {
        if (required == 0) return true
        return (currentModifiers and required) == required
    }

    // Check if a keybinding is currently held down
    fun isDown(bindingName: String): Boolean {
        val binding = keybindings[bindingName] ?: return false
        val keyPressed = keyStates[binding.key.keyCode]?.pressed ?: false
        return keyPressed && modifiersMatch(binding.key.modifiers)
    }

    // Check if a keybinding was just pressed this frame
    fun isPressed(bindingName: String): Boolean {
        val binding = keybindings[bindingName] ?: return false
        val keyJustPressed = keyStates[binding.key.keyCode]?.justPressed ?: false
        return keyJustPressed && modifiersMatch(binding.key.modifiers)
    }

    // Check if a keybinding was just released this frame
    fun isReleased(bindingName: String): Boolean {
        val binding = keybindings[bindingName] ?: return false
        val keyJustReleased = keyStates[binding.key.keyCode]?.justReleased ?: false
        return keyJustReleased && modifiersMatch(binding.key.modifiers)
    }

    // Direct key code check (for non-bound keys)
    fun isKeyDown(keyCode: Int): Boolean {
        return keyStates[keyCode]?.pressed ?: false
    }

    fun isKeyPressed(keyCode: Int): Boolean {
        return keyStates[keyCode]?.justPressed ?: false
    }

    fun isKeyReleased(keyCode: Int): Boolean {
        return keyStates[keyCode]?.justReleased ?: false
    }

    // Check with modifiers
    fun isKeyDown(key: Key): Boolean {
        val keyPressed = keyStates[key.keyCode]?.pressed ?: false
        return keyPressed && modifiersMatch(key.modifiers)
    }

    fun isKeyPressed(key: Key): Boolean {
        val keyJustPressed = keyStates[key.keyCode]?.justPressed ?: false
        return keyJustPressed && modifiersMatch(key.modifiers)
    }

    // Get keybinding by name
    fun getBinding(name: String): Keybind? = keybindings[name]

    // Rebind a key
    fun rebind(bindingName: String, newKey: Key): Boolean {
        val binding = keybindings[bindingName] ?: return false
        binding.key = newKey
        return true
    }

    // Convenience rebind without modifiers
    fun rebind(bindingName: String, newKeyCode: Int): Boolean {
        return rebind(bindingName, Key(newKeyCode, 0))
    }

    // Reset all keybindings to defaults
    fun resetAllBindings() {
        keybindings.values.forEach { it.reset() }
    }

    // Call this at the end of each frame
    fun update() {
        // Clear "just pressed" and "just released" states
        keyStates.values.forEach {
            it.justPressed = false
            it.justReleased = false
        }
    }

    // Call this after consuming mouse delta (e.g., after camera.processMouseMovement)
    fun resetMouseDelta() {
        mouseDeltaX = 0f
        mouseDeltaY = 0f
    }

    // Get all registered keybindings
    fun getAllBindings(): Map<String, Keybind> = keybindings.toMap()

    // Get current modifiers (useful for debugging or UI)
    fun getCurrentModifiers(): Int = currentModifiers
}