package io.github.notnekodev.apfel.assets.types

import io.github.notnekodev.apfel.assets.Asset
import io.github.notnekodev.apfel.render.meshes.ObjModel
import io.github.notnekodev.apfel.io.ResourceFile
import java.io.FileInputStream
import java.io.InputStream

class ObjModelAsset(
    name: String,
    path: String,
    lazyLoad: Boolean = true
) : Asset(name, path, lazyLoad) {

    var model: ObjModel? = null
        private set

    override fun onLoad() {
        println("Loading 3D .obj Model $name from $path")

        val file = ResourceFile.get(path)

        val input: InputStream = FileInputStream(file)

        model = ObjModel(input)
    }

    override fun onUnload() {
        model?.delete()
        model = null
    }
}