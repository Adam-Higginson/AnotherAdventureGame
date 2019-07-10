package com.adam.adventure.entity.component.tilemap

import com.adam.adventure.entity.EntityComponent
import com.adam.adventure.entity.component.event.ComponentEvent
import com.adam.adventure.render.RenderQueue
import com.adam.adventure.render.renderable.TilemapRenderable
import com.adam.adventure.render.texture.Texture
import com.adam.adventure.render.texture.TextureFactory
import org.mapeditor.core.*
import org.mapeditor.core.Map
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject

class TilemapRendererComponent : EntityComponent() {
    val log : Logger = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private var textureFactory : TextureFactory? = null

    @Inject
    private var renderQueue : RenderQueue? = null


    override fun onComponentEvent(componentEvent: ComponentEvent?) {
        if (componentEvent is TilemapLoadedEvent) {
            loadTilemapToGpu(componentEvent.tilemap)
        }
    }

    private fun loadTilemapToGpu(tilemap : Map) {
        //Basic implementation for now, doesn't handle multiple layers
        if (tilemap.tileSets.size != 1 || tilemap.layerCount != 1) {
            throw IllegalStateException("Not yet implemented for multiple tilesets/tilelayers");
        } else {
            val tileSetTexture = loadTilesetTexture(tilemap.tileSets[0]);

            val tilemapLayer = tilemap.getLayer(0)
            if (tilemapLayer is TileLayer) {
                val dataTexture = loadTileDataAsTexture(tilemap, tilemapLayer.data)
                val tilemapRenderable = TilemapRenderable(tileSetTexture, dataTexture, tilemap.width, tilemap.height)
                renderQueue!!.addRenderable(tilemapRenderable)
            } else {
                throw IllegalStateException("Found incorrect tilemap layer type");
            }
        }
    }

    private fun loadTilesetTexture(tileSet : TileSet) : Texture {
        val tilesetImageFile = tileSet.tilebmpFile
        log.info("Loading tileset image from: {}", tilesetImageFile);
        val tilesetTexture = textureFactory!!.loadImageTextureFromFileName(tilesetImageFile)
        log.info("Successfully loaded tileset texture.")

        return tilesetTexture;
    }

    private fun loadTileDataAsTexture(tilemap: Map, data: Data): Texture {
        if (data.encoding != Encoding.CSV) {
            throw IllegalArgumentException("Data encoding is required to be in CSV format for loading tilemap!");
        }

        val dataByteArray = data.value.toByteArray(Charsets.UTF_8)
        return textureFactory!!.loadDataTexture(dataByteArray, tilemap.width, tilemap.height);
    }
}