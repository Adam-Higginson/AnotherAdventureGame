package com.adam.adventure.entity.component.tilemap.data

import java.io.InputStream

interface TileMapLoader {
    fun load(inputStream : InputStream) : TileMap
}

