package io.github.notnekodev.apfel.assets

abstract class Asset(val name: String, open val path: String, lazyLoad: Boolean = true) {
    companion object {
        private var nextID = 0
        private fun genID(): Int = nextID++
    }

    val id: Int = genID()

    var loaded = false
        protected set

    init {
        if (!lazyLoad) {
            load()
        }
    }

    protected abstract fun onLoad()
    protected abstract fun onUnload()

    fun load() {
        if (!loaded) {
            onLoad()
            loaded = true
        }
    }

    fun unload() {
        if (loaded) {
            onUnload()
            loaded = false
        }
    }

    override fun toString(): String {
        return "Asset(id=$id, name='$name', path='$path', loaded=$loaded)"
    }
}