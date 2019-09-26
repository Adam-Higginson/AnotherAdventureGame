package com.adam.adventure.entity.component.tilemap.event

import com.adam.adventure.entity.component.event.ComponentEvent
import com.adam.adventure.entity.component.tilemap.data.TileMap
import com.adam.adventure.entity.component.tilemap.data.TileSet

class TilemapLoadedEvent(val tileMap : TileMap,
                         val tileSet : TileSet) : ComponentEvent()