package com.adam.adventure.entity.component.tilemap.data

import java.io.InputStream

interface TileSetLoader {
    fun load(inputStream : InputStream) : TileSet
}