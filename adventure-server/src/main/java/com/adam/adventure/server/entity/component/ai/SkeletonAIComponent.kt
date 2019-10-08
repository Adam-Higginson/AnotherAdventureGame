package com.adam.adventure.server.entity.component.ai

import com.adam.adventure.entity.Entity
import com.adam.adventure.entity.EntityComponent
import com.adam.adventure.entity.component.tilemap.TilemapComponent
import com.adam.adventure.entity.component.tilemap.data.Tile
import com.adam.adventure.scene.Scene
import com.adam.adventure.scene.SceneManager
import org.joml.Vector3f
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import kotlin.math.abs

class SkeletonAIComponent : EntityComponent() {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    private enum class State { DO_NOTHING, SELECTING_TARGET, MOVING_TO_TARGET }

    @Inject
    private var sceneManager: SceneManager? = null

    private var state = State.SELECTING_TARGET
    private var target: Entity? = null
    private var path : Stack<Node>? = null
    private var currentNode : Node? = null

    override fun update(deltaTime: Float) {
        if (sceneManager!!.currentScene.isEmpty) {
            return
        }
        val scene = sceneManager!!.currentScene.get()

        if (state == State.SELECTING_TARGET) {
            buildPathToTarget(scene)
        } else if (state == State.MOVING_TO_TARGET) {
            moveToTarget(deltaTime)
        }
    }

    private fun buildPathToTarget(scene: Scene) {
        selectTarget(scene)
        path = findPathToTarget(scene)
        state = State.MOVING_TO_TARGET
    }

    private fun moveToTarget(deltaTime: Float) {
        calculateCurrentTargetNode()
        if (currentNode == null) {
            state = State.SELECTING_TARGET
        } else {
            moveTowardsTargetNode(deltaTime)
        }
    }

    private fun calculateCurrentTargetNode() {
        if (currentNode == null) {
            popStackToCurrentNode()
        }

        val tileMap = getTilemap()
        val ourTile = tileMap.getTileForEntity(entity)
        if (ourTile == null || currentNode == null) {
            return
        }

        if (ourTile.id == currentNode!!.tile.id) {
                popStackToCurrentNode()
        }
    }

    private fun popStackToCurrentNode() {
        if (!path!!.empty()) {
            currentNode = path!!.pop()
        } else {
            currentNode = null
        }
    }

    private fun moveTowardsTargetNode(deltaTime : Float) {
        currentNode?.apply {

            val entityTranslation = Vector3f()
            transformComponent.transform.getTranslation(entityTranslation)

            val tilemap = getTilemap()
            val targetPosition = tilemap.getRealTilePosition(this.tile)

            log.info("For tile: {} target position is: \n{}",this.tile, targetPosition)

            val direction = targetPosition.sub(entityTranslation).normalize()
            log.info("Direction: {}", direction)
            transformComponent.transform.translate(direction.x * 0.015f * deltaTime, direction.y * 0.015f * deltaTime, 0.0f)
            log.info("Transform:\n{}", transformComponent.transform)
        }
    }

    private fun selectTarget(scene: Scene) {
        val players = scene.findEntitiesByName("Player")
        if (players.isNotEmpty()) {
            target = players.random()
        }
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

    private fun findPathToTarget(scene: Scene): Stack<Node> {
        val entityTileMap = getTilemap()

        val ourTile = entityTileMap.getTileForEntity(entity)
        val goalTile = entityTileMap.getTileForEntity(target!!)
        if (ourTile == null || goalTile == null) {
            //Entity is off the map somehow
            return Stack()
        }

        val ourNode = Node(ourTile, 0)
        val goalNode = Node(goalTile)
        val reachable = mutableSetOf(ourNode)
        val explored = mutableSetOf<Node>()

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

    private fun chooseNode(ourNode: Node, goalNode: Node, reachable: Set<Node>): Node? {
        var minCost = Integer.MAX_VALUE
        var bestNode: Node? = null

        for (node in reachable) {
            val totalCost = 1 + estimateDistance(ourNode, goalNode)
            if (minCost > totalCost) {
                minCost = totalCost
                bestNode = node
            }
        }

        return bestNode
    }

    private fun estimateDistance(ourNode: Node, goalNode: Node): Int {
        return abs(ourNode.tile.x - goalNode.tile.x) + abs(ourNode.tile.y - goalNode.tile.y)
    }

    private fun getAdjacentNodes(entityTileMap: TilemapComponent.EntityTileMap, node: Node): Set<Node> {
        return entityTileMap.getAdjacentTiles(node.tile).map { Node(it) }.toSet()
    }

    private fun buildPath(node: Node): Stack<Node> {
        val path = Stack<Node>()
        var toNode: Node? = node
        while (toNode != null) {
            path.push(toNode)
            toNode = toNode.previous
        }

        return path
    }

    private inner class Node(val tile: Tile, var cost: Int = Integer.MAX_VALUE) {
        var previous: Node? = null

        override fun hashCode(): Int {
            return tile.id
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Node

            return tile.id == other.tile.id
        }

        override fun toString(): String {
            val tileX = tile.x
            val tileY = tile.y
            return "Node(tile=($tileX, $tileY), cost=$cost)"
        }
    }
}