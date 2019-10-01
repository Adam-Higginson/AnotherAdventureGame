package com.adam.adventure.entity.component.tilemap

import com.adam.adventure.entity.Entity
import com.adam.adventure.entity.EntityComponent
import com.adam.adventure.entity.component.tilemap.data.*
import com.adam.adventure.entity.component.tilemap.event.TilemapEntityTransformEvent
import com.adam.adventure.entity.component.tilemap.event.TilemapLoadedEvent
import com.adam.adventure.event.EventBus
import com.adam.adventure.event.EventSubscribe
import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import com.google.common.io.Resources
import org.joml.Vector2i
import org.joml.Vector3f
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URL
import javax.inject.Inject

class TilemapComponent(private val tilemapLocation: String) : EntityComponent() {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private var tileMapLoader: TileMapLoader? = null

    @Inject
    private var tileSetLoader: TileSetLoader? = null

    @Inject
    private var eventBus: EventBus? = null

    private var entityTileMap: EntityTileMap? = null

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

    /**
     * Called by the [TilemapObserverComponent] when an entity is detected to have been transformed.
     */
    @EventSubscribe
    fun onTilemapEntityTransform(tilemapEntityTransformEvent: TilemapEntityTransformEvent) {
        entityTileMap!!.updateEntity(tilemapEntityTransformEvent.entity)
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


    private fun buildEntityTileMap(tileMap: TileMap, tileSet: TileSet) {
        val tileIdToTile = tileSet.tiles.associateBy(keySelector = Tile::id, valueTransform = { t -> t }).toMutableMap()

        val tileMapLayer = tileMap.layers[0]
        val tiles = mutableListOf<Tile>()
        for (tileData in tileMapLayer.data) {
            //TODO tile with id 0 is a special case of empty tile
            val tileId = tileMap.tileSets[0].firstgid + tileData

            val tile = tileIdToTile.getOrPut(tileId, { Tile(tileId, "unknown", emptyList()) })
            tiles.add(tile)
        }

        entityTileMap = EntityTileMap(tileMap, tileSet, tiles)
    }

    fun dumpTileMapInfo() {
        if (entityTileMap == null) {
            log.info("Could not dump tile map info as no active tile map!")
            return
        }


        log.info("Entities in tiles: {}", entityTileMap!!.entityIdToTile)
    }


    /**
     * Class which holds all entity information on this tile map
     */
    private inner class EntityTileMap(val tileMap: TileMap,
                                val tileSet: TileSet,
                                val tiles: List<Tile>,
                                val tileIdToEntities: Multimap<Int, Entity> = ArrayListMultimap.create(),
                                val entityIdToTile: MutableMap<Int, Tile> = mutableMapOf()) {

        fun getTileAt(x: Int, y: Int): Tile {
            return tiles[tileMap.width * (x + y)]
        }

        fun getEntitiesAt(x: Int, y: Int): Collection<Entity> {
            val tileId = getTileAt(x, y).id
            return tileIdToEntities[tileId]
        }

        fun updateEntity(entity: Entity) {
            removeFromMaps(entity)
            val tilePos = getTilePosForEntity(entity)
            if (tilePos.x < 0 || tilePos.x >= tileMap.width || tilePos.y >= 0 || tilePos.y < tileMap.height) {
                // Out of bounds
                return
            }

            val tile = getTileAt(tilePos.x, tilePos.y)
            tileIdToEntities[tile.id].add(entity)
            entityIdToTile[entity.id] = tile
        }

        private fun removeFromMaps(entity: Entity) {
            val existingTile = entityIdToTile.remove(entity.id)
            if (existingTile != null) {
                tileIdToEntities[existingTile.id].removeIf { it.id == entity.id }
            }
        }


        private fun getTilePosForEntity(entity : Entity) : Vector2i {
            val entityTranslation = Vector3f()
            entity.transform.getTranslation(entityTranslation)

            val tilemapTranslation = Vector3f()
            transformComponent.transform.getTranslation(tilemapTranslation)

            entityTranslation.x -= tilemapTranslation.x
            entityTranslation.y -= tilemapTranslation.y
            val tileX = (entityTranslation.x / tileMap.tileWidth).toInt()
            val tileY = (entityTranslation.y / (tileMap.tileHeight * -1)).toInt()

            return Vector2i(tileX, tileY)
        }
    }

}