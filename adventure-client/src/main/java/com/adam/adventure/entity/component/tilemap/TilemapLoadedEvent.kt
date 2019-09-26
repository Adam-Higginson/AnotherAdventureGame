package com.adam.adventure.entity.component.tilemap

import com.adam.adventure.entity.component.event.ComponentEvent
import com.adam.adventure.tilemap.TileMap
import com.adam.adventure.tilemap.TileSet

class TilemapLoadedEvent(val tileMap : TileMap,
                         val tileSet : TileSet) : ComponentEvent()