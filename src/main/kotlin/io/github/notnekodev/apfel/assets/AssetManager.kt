package io.github.notnekodev.apfel.assets

@Suppress("unused")
class AssetManager {
    private val assets = mutableMapOf<Int, Asset>()

    fun <T : Asset> register(asset: T): T {
        assets[asset.id] = asset
        return asset
    }

    fun <T : Asset> load(asset: T): T {
        register(asset)
        asset.load()
        return asset
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Asset> get(id: Int, autoLoad: Boolean = true): T? {
        val asset = assets[id] as? T ?: return null
        if (autoLoad && !asset.loaded) asset.load()
        return asset
    }

    fun unload(id: Int) {
        assets[id]?.unload()
        assets.remove(id)
    }

    fun unloadAll() {
        assets.values.forEach { it.unload() }
        assets.clear()
    }
}