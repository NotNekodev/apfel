package io.github.notnekodev.apfel.input

import org.lwjgl.glfw.GLFW

data class Keybind(
    val name: String,
    var key: Key,
    val defaultKey: Key = key
) {
    constructor(name: String, keyCode: Int, defaultKeyCode: Int = keyCode)
            : this(name, Key(keyCode, 0), Key(defaultKeyCode, 0))

    fun reset() {
        key = defaultKey.copy()
    }

    @Suppress("unused")
    companion object {
        fun forward() = Keybind("forward", GLFW.GLFW_KEY_W)
        fun backward() = Keybind("backward", GLFW.GLFW_KEY_S)
        fun left() = Keybind("left", GLFW.GLFW_KEY_A)
        fun right() = Keybind("right", GLFW.GLFW_KEY_D)
        fun jump() = Keybind("jump", GLFW.GLFW_KEY_SPACE)
        fun sprint() = Keybind("sprint", GLFW.GLFW_KEY_LEFT_SHIFT)
        fun crouch() = Keybind("crouch", GLFW.GLFW_KEY_LEFT_CONTROL)
        fun interact() = Keybind("interact", GLFW.GLFW_KEY_E)
        fun inventory() = Keybind("inventory", GLFW.GLFW_KEY_TAB)
        fun pause() = Keybind("pause", GLFW.GLFW_KEY_ESCAPE)

        fun save() = Keybind("save", Key(GLFW.GLFW_KEY_S, GLFW.GLFW_MOD_CONTROL))
        fun quickSave() = Keybind("quick_save", Key(GLFW.GLFW_KEY_F5, 0))
    }
}