package com.adam.adventure.entity.component.tilemap

import com.adam.adventure.entity.component.event.ComponentEvent
import org.mapeditor.core.Map

class TilemapLoadedEvent(val tilemap : Map) : ComponentEvent();