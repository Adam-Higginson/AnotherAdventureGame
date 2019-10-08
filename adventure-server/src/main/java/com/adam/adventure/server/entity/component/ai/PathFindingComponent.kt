package com.adam.adventure.server.entity.component.ai

import com.adam.adventure.entity.Entity
import com.adam.adventure.entity.EntityComponent
import com.adam.adventure.entity.component.event.ComponentEvent
import com.adam.adventure.entity.component.tilemap.TilemapComponent
import com.adam.adventure.entity.component.tilemap.data.Tile
import com.adam.adventure.scene.SceneManager
import org.joml.Vector2f
import java.util.*
import javax.inject.Inject

class PathFindingComponent : EntityComponent() {

    @Inject
    private var sceneManager : SceneManager? = null

    override fun onComponentEvent(componentEvent: ComponentEvent?) {
        if (componentEvent is FindPathRequestEvent) {
            val path = findPathToTarget(componentEvent.target)
            broadcastComponentEvent(PathFoundEvent(path))
        }
    }

    private fun findPathToTarget(target : Entity): Stack<PathNode> {
        val entityTileMap = getTilemap()

        val ourTile = entityTileMap.getTileForEntity(entity)
        val goalTile = entityTileMap.getTileForEntity(target)
        if (ourTile == null || goalTile == null) {
            //Entity is off the map somehow
            return Stack()
        }

        val ourNode = PathNode(ourTile, 0)
        val goalNode = PathNode(goalTile)
        val reachable = mutableSetOf(ourNode)
        val explored = mutableSetOf<PathNode>()

        while (reachable.isNotEmpty()) {
            val chosenNode = chooseNode(ourNode, goalNode, reachable) ?: return Stack()
            if (chosenNode == goalNode) {
                return buildPath(chosenNode)
            }

            reachable.remove(chosenNode)
            explored.add(chosenNode)

            val adjacentNodes = getAdjacentNodes(entityTileMap, chosenNode).minus(explored)
            for (adjacent in adjacentNodes) {
                if (!reachable.contains(adjacent)) {
                    reachable.add(adjacent)
                }

                if (chosenNode.cost + 1 < adjacent.cost) {
                    adjacent.previous = chosenNode
                    adjacent.cost = chosenNode.cost + 1
                }
            }
        }

        return Stack()
    }


    private fun getTilemap(): TilemapComponent.EntityTileMap {
        if (sceneManager!!.currentScene.isEmpty) {
            throw IllegalStateException("AI present but no active scene!")
        }
        val scene = sceneManager!!.currentScene.get()

        val tilemapComponent = scene.findEntityComponents(TilemapComponent::class.java).firstOrNull()
                ?: throw IllegalStateException("AI present but no tilemap!")
        return tilemapComponent.entityTileMap ?: throw IllegalStateException("AI present but no tilemap!")
    }

    private fun chooseNode(ourNode: PathNode, goalNode: PathNode, reachable: Set<PathNode>): PathNode? {
        var minCost = Integer.MAX_VALUE
        var bestNode: PathNode? = null

        for (node in reachable) {
            val totalCost = 1 + estimateDistance(ourNode, goalNode)
            if (minCost > totalCost) {
                minCost = totalCost
                bestNode = node
            }
        }

        return bestNode
    }

    private fun estimateDistance(ourNode: PathNode, goalNode: PathNode): Int {
        return Vector2f(ourNode.tile.x.toFloat(), ourNode.tile.y.toFloat())
                .distance(goalNode.tile.x.toFloat(), goalNode.tile.y.toFloat()).toInt()
    }

    private fun getAdjacentNodes(entityTileMap: TilemapComponent.EntityTileMap, node: PathNode): Set<PathNode> {
        return entityTileMap.getAdjacentTiles(node.tile).map { PathNode(it) }.toSet()
    }

    private fun buildPath(node: PathNode): Stack<PathNode> {
        val path = Stack<PathNode>()
        var toNode: PathNode? = node
        while (toNode != null) {
            path.push(toNode)
            toNode = toNode.previous
        }

        return path
    }

    inner class PathNode(val tile: Tile, var cost: Int = Integer.MAX_VALUE) {
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
}