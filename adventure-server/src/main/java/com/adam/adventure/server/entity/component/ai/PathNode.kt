package com.adam.adventure.server.entity.component.ai

import com.adam.adventure.entity.component.tilemap.data.Tile

class PathNode(val tile: Tile, var cost: Int = Integer.MAX_VALUE) {
    var previous: PathNode? = null

    override fun hashCode(): Int {
        return tile.id
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PathNode

        return tile.id == other.tile.id
    }

    override fun toString(): String {
        val tileX = tile.x
        val tileY = tile.y
        return "PathNode(tile=($tileX, $tileY), cost=$cost)"
    }
}