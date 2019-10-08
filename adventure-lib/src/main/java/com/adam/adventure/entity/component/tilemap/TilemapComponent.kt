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
import org.joml.Vector4f
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

    var entityTileMap: EntityTileMap? = null
        private set

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
        val tileIdToTileSetTile = tileSet.tiles.associateBy(keySelector = TileSetTile::id, valueTransform = { t -> t }).toMutableMap()

        val tileMapLayer = tileMap.layers[0]
        val tiles = mutableListOf<Tile>()

        var tileX = 0
        var tileY = 0
        for ((gid, tileData) in tileMapLayer.data.withIndex()) {

            //TODO tile with tileSetId 0 is a special case of empty tile
            val tileId = tileMap.tileSets[0].firstgid + tileData
            val tileSetTile = tileIdToTileSetTile.getOrPut(tileId, { TileSetTile(tileId, emptyList(), "unknown") })

            val tile = Tile(tileSetTile, tileX, tileY, gid)
            tiles.add(tile)
            tileX++
            if (tileX % tileMapLayer.width == 0) {
                tileX = 0
                tileY++
            }
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
    inner class EntityTileMap(private val tileMap: TileMap,
                              private val tileSet: TileSet,
                              private val tiles: List<Tile>,
                              private val tileIdToEntities: Multimap<Int, Entity> = ArrayListMultimap.create(),
                              val entityIdToTile: MutableMap<Int, Tile> = mutableMapOf()) {

        fun getTileAt(x: Int, y: Int): Tile? {
            if (isOutOfBounds(x, y)) {
                return null
            }

            return tiles[(tileMap.width * y) + x]
        }

        fun getEntitiesAt(x: Int, y: Int): Collection<Entity> {
            val tile = getTileAt(x, y) ?: return emptyList()
            val tileId = tile.id
            return tileIdToEntities[tileId]
        }

        fun updateEntity(entity: Entity) {
            removeFromMaps(entity)
            val tilePos = getTilePosForEntity(entity)

            val tile = getTileAt(tilePos.x, tilePos.y)
            if (tile != null) {
                tileIdToEntities[tile.id].add(entity)
                entityIdToTile[entity.id] = tile
            }
        }

        fun getTileForEntity(entity: Entity): Tile? {
            return entityIdToTile[entity.id]
        }

        fun getAdjacentTiles(tile: Tile): List<Tile> {
            return listOfNotNull(
                    getTileAt(tile.x, tile.y + 1),
                    getTileAt(tile.x + 1, tile.y + 1),
                    getTileAt(tile.x + 1, tile.y),
                    getTileAt(tile.x + 1, tile.y - 1),
                    getTileAt(tile.x, tile.y - 1),
                    getTileAt(tile.x -1, tile.y - 1),
                    getTileAt(tile.x - 1, tile.y),
                    getTileAt(tile.x -1, tile.y + 1))
        }

        fun getRealTilePosition(tile: Tile) : Vector3f {
            val tileX = (tile.x.toFloat() * tileSet.tileWidth) + (tileSet.tileWidth / 2)
            val tileY = (-(tile.y.toFloat()) * tileSet.tileHeight) - (tileSet.tileHeight / 2)

            val vectorPosition = Vector4f(tileX, tileY, 0.0f, 1.0f)
            transformComponent.transform.transform(vectorPosition)
            return Vector3f(vectorPosition.x, vectorPosition.y, vectorPosition.z)
        }


        private fun isOutOfBounds(x: Int, y: Int): Boolean {
            if (x < 0 || x >= tileMap.width || y < 0 || y >= tileMap.height) {
                return true
            }
            val index = (tileMap.width * y) + x
            return index < 0 || index >= tiles.size
        }

        private fun getTilePosForEntity(entity: Entity): Vector2i {
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

        private fun removeFromMaps(entity: Entity) {
            val existingTile = entityIdToTile.remove(entity.id)
            if (existingTile != null) {
                tileIdToEntities[existingTile.id].removeIf { it.id == entity.id }
            }
        }
    }

}