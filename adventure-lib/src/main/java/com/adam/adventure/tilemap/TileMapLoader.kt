package com.adam.adventure.tilemap

import java.io.InputStream

interface TileMapLoader {
    fun load(inputStream : InputStream) : TileMap
}

