package com.adam.adventure.entity.component.tilemap

import com.adam.adventure.entity.EntityComponent
import com.google.common.io.Resources
import org.mapeditor.io.TMXMapReader
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TilemapComponent(private val tilemapLocation : String) : EntityComponent() {
    private val log : Logger = LoggerFactory.getLogger(this.javaClass)

    override fun activate() {
        val tilemapResource = Resources.getResource(tilemapLocation)
        log.info("Loading tilemap resource from: {}", tilemapResource)
        val map = TMXMapReader().readMap(tilemapResource.path)
        log.info("Successfully loaded tilemap")

        broadcastComponentEvent(TilemapLoadedEvent(map))
    }
}