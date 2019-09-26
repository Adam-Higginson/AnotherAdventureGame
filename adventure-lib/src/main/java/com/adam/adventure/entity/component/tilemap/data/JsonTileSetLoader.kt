package com.adam.adventure.entity.component.tilemap.data

import com.google.gson.GsonBuilder
import java.io.InputStream

class JsonTileSetLoader : TileSetLoader {
    private val gson = GsonBuilder()
            .setFieldNamingStrategy{ field -> field.name.toLowerCase()}
            .create();

    override fun load(inputStream: InputStream): TileSet {
        val json = inputStream.reader().use { it.readText() }
        return gson.fromJson(json, TileSet::class.java)
    }
}