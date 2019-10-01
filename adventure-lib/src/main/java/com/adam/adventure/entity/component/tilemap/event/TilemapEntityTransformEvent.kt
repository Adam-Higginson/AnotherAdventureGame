package com.adam.adventure.entity.component.tilemap.event

import com.adam.adventure.entity.Entity
import com.adam.adventure.event.Event

/**
 * Called when an entity is detected to have transformed and needs its tilemap location updating
 */
class TilemapEntityTransformEvent(val entity : Entity) : Event() {
}