package com.adam.adventure.tilemap

import java.io.InputStream

interface TileSetLoader {
    fun load(inputStream : InputStream) : TileSet
}