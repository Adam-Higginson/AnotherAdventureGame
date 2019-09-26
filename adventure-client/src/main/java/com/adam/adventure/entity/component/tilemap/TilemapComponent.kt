package com.adam.adventure.entity.component.tilemap

import com.adam.adventure.entity.EntityComponent
import com.adam.adventure.tilemap.TileMapLoader
import com.adam.adventure.tilemap.TileSetLoader
import com.google.common.io.Resources
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import javax.inject.Inject

class TilemapComponent(private val tilemapLocation : String) : EntityComponent() {
    private val log : Logger = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private var tileMapLoader : TileMapLoader? = null

    @Inject
    private var tileSetLoader : TileSetLoader? = null

    override fun activate() {
        val tilemapResource = Resources.getResource(tilemapLocation)
        log.info("Loading tileMap resource from: {}", tilemapResource)
        val map = tileMapLoader!!.load(tilemapResource.openStream())

        if (map.tileSets.size != 1) {
            throw IllegalStateException("Only 1 tileset is currently supported!")
        }

        val file = File(tilemapResource.toURI())
        val tileSetFile = File(file.parent.plus(File.separatorChar).plus(map.tileSets[0].source))
        log.info("Opening tileset at: {}", tileSetFile)
        val tileSet = tileSetLoader!!.load(tileSetFile.inputStream())

        log.info("Successfully loaded tileMap")
        broadcastComponentEvent(TilemapLoadedEvent(map, tileSet))
    }
}