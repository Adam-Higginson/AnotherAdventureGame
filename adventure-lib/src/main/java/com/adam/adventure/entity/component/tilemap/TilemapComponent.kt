package com.adam.adventure.entity.component.tilemap

import com.adam.adventure.entity.Entity
import com.adam.adventure.entity.EntityComponent
import com.adam.adventure.entity.component.tilemap.data.*
import com.adam.adventure.entity.component.tilemap.event.TilemapEntityTransformEvent
import com.adam.adventure.entity.component.tilemap.event.TilemapLoadedEvent
import com.adam.adventure.event.EventBus
import com.adam.adventure.event.EventSubscribe
import com.google.common.io.Resources
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URL
import javax.inject.Inject

class TilemapComponent(private val tilemapLocation : String) : EntityComponent() {
    private val log : Logger = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private var tileMapLoader : TileMapLoader? = null

    @Inject
    private var tileSetLoader : TileSetLoader? = null

    @Inject
    private var eventBus : EventBus? = null

    private var entityTileMap : EntityTileMap? = null

    override fun activate() {
        val tilemapResource = Resources.getResource(tilemapLocation)
        log.info("Loading tileMap resource from: {}", tilemapResource)
        val tileMap = tileMapLoader!!.load(tilemapResource.openStream())
        assertValidTileMap(tileMap)

        val tileSet = loadTileSet(tilemapResource, tileMap)
        buildEntityTileMap(tileMap, tileSet)
        eventBus!!.register(this)

        log.info("Successfully loaded tileMap")
        broadcastComponentEvent(TilemapLoadedEvent(tileMap, tileSet))
    }

    private fun assertValidTileMap(map: TileMap) {
        if (map.tileSets.size != 1) {
            throw IllegalStateException("Only 1 tileset is currently supported!")
        }
        if (map.layers.size != 1) {
            throw java.lang.IllegalStateException("Only 1 tilemap layer is currently supported!")
        }
    }

    private fun loadTileSet(tilemapResource: URL, tileMap: TileMap): TileSet {
        val file = File(tilemapResource.toURI())
        val tileSetFile = File(file.parent.plus(File.separatorChar).plus(tileMap.tileSets[0].source))
        log.info("Opening tileset at: {}", tileSetFile)
        return tileSetLoader!!.load(tileSetFile.inputStream())
    }


    private fun buildEntityTileMap(tileMap: TileMap, tileSet : TileSet) {
        val tileIdToTile = tileSet.tiles.associateBy(keySelector = Tile::id, valueTransform = { t -> t }).toMutableMap()

        val tilesWithEntity = mutableListOf<TileWithEntity>()
        val tileMapLayer = tileMap.layers[0]
        for((index, tileData) in tileMapLayer.data.withIndex()) {
            //TODO tile with id 0 is a special case of empty tile
            val tileId = tileMap.tileSets[0].firstgid + tileData

            val tile = tileIdToTile.getOrPut(tileId, { Tile(tileId, "unknown", emptyList()) } )
            val tileWithEntity = TileWithEntity(tile, index % tileMapLayer.width, index / tileMapLayer.width)
            tilesWithEntity.add(tileWithEntity)
        }

        entityTileMap = EntityTileMap(tileMapLayer.width, tileMapLayer.height, tilesWithEntity)
    }



    @EventSubscribe
    fun onTilemapEntityTransform(tilemapEntityTransformEvent: TilemapEntityTransformEvent) {
        log.info("Entity: {} detected to have transformed!", tilemapEntityTransformEvent.entity)
    }

    /**
     * Class which holds all entity information on this tile map
     */
    class EntityTileMap (val width : Int,
                         val height : Int,
                         val tilesWithEntity : List<TileWithEntity>) {

    }

    class TileWithEntity (val tile : Tile,
                          val x : Int,
                          val y : Int) {
        private var entity : Entity? = null
    }
}