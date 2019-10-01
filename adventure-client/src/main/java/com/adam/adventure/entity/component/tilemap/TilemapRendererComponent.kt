package com.adam.adventure.entity.component.tilemap

import com.adam.adventure.entity.EntityComponent
import com.adam.adventure.entity.component.event.ComponentEvent
import com.adam.adventure.entity.component.tilemap.data.TileMap
import com.adam.adventure.entity.component.tilemap.data.TileMapLayer
import com.adam.adventure.entity.component.tilemap.data.TileSet
import com.adam.adventure.entity.component.tilemap.event.TilemapLoadedEvent
import com.adam.adventure.render.RenderQueue
import com.adam.adventure.render.renderable.Renderable
import com.adam.adventure.render.renderable.TilemapRenderable
import com.adam.adventure.render.texture.Texture
import com.adam.adventure.render.texture.TextureFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject

class TilemapRendererComponent : EntityComponent() {
    val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private var textureFactory: TextureFactory? = null

    @Inject
    private var renderQueue: RenderQueue? = null

    private var tileMapRenderable : Renderable? = null


    override fun onComponentEvent(componentEvent: ComponentEvent?) {
        if (componentEvent is TilemapLoadedEvent) {
            loadTilemapToGpu(componentEvent.tileMap, componentEvent.tileSet)
        }
    }

    override fun update(deltaTime: Float) {
        tileMapRenderable?.let { r -> renderQueue!!.addRenderable(r) }
    }

    override fun destroy() {
        tileMapRenderable?.destroy()
    }

    private fun loadTilemapToGpu(tileMap: TileMap, tileSet : TileSet) {
        //Basic implementation for now, doesn't handle multiple layers
        if (tileMap.tileSets.size != 1 || tileMap.layers.size != 1) {
            throw IllegalStateException("Not yet implemented for multiple tilesets/tilelayers");
        } else {
            val tileSetTexture = loadTilesetTexture(tileSet);
            val dataTexture = loadTileDataAsTexture(tileMap.layers[0])
            tileMapRenderable = TilemapRenderable(entity.transform,
                    tileSetTexture,
                    dataTexture,
                    tileMap.width,
                    tileMap.height,
                    tileSet.columns,
                    tileSet.tileWidth,
                    tileMap.tileSets[0].firstgid)
        }
    }

    private fun loadTilesetTexture(tileSet: TileSet): Texture {
        //TODO For now hardcode directory, would be better to dynamically figure out
        val tilesetImageFile = "/tilemaps/" + tileSet.image
        log.info("Loading tileset image from: {}", tilesetImageFile);
        val tilesetTexture = textureFactory!!.loadImageTextureFromFileNameInResources(tilesetImageFile)
        log.info("Successfully loaded tileset texture.")

        return tilesetTexture;
    }

    private fun loadTileDataAsTexture(tileMapLayer: TileMapLayer): Texture {
        return textureFactory!!.loadDataTexture(tileMapLayer.data, tileMapLayer.width, tileMapLayer.height);
    }
}