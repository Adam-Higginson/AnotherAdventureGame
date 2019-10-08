package com.adam.adventure.server.entity.component.ai

import com.adam.adventure.entity.EntityComponent
import com.adam.adventure.entity.component.event.ComponentEvent
import com.adam.adventure.entity.component.tilemap.TilemapComponent
import com.adam.adventure.scene.SceneManager
import org.joml.Vector3f
import java.util.*
import javax.inject.Inject

/**
 * Class which moves an entity along a given path
 */
class PathMovingComponent(private val speed : Float) : EntityComponent() {

    @Inject
    private lateinit var sceneManager : SceneManager

    private var path: Stack<PathFindingComponent.PathNode> = Stack()
    private var currentNode : PathFindingComponent.PathNode? = null


    override fun onComponentEvent(componentEvent: ComponentEvent?) {
        if (componentEvent is MoveAlongPathRequestEvent) {
            path = componentEvent.path
        }
    }

    override fun update(deltaTime: Float) {
        moveToTarget(deltaTime)
    }

    private fun moveToTarget(deltaTime: Float) {
        calculateCurrentTargetNode()
        moveTowardsTargetNode(deltaTime)
    }

    private fun calculateCurrentTargetNode() {
        if (currentNode == null) {
            popStackToCurrentNode()
        }

        val tileMap = sceneManager.getSingletonEntityComponent(TilemapComponent::class.java).entityTileMap!!
        val ourTile = tileMap.getTileForEntity(entity)
        if (ourTile == null || currentNode == null) {
            return
        }

        if (ourTile.id == currentNode!!.tile.id) {
            popStackToCurrentNode()
        }
    }

    private fun moveTowardsTargetNode(deltaTime : Float) {
        currentNode?.apply {

            val entityTranslation = Vector3f()
            transformComponent.transform.getTranslation(entityTranslation)

            val tilemap = sceneManager.getSingletonEntityComponent(TilemapComponent::class.java).entityTileMap!!
            val targetPosition = tilemap.getRealTilePosition(this.tile)

            val direction = targetPosition.sub(entityTranslation).normalize()
            transformComponent.transform.translate(direction.x * speed * deltaTime, direction.y * speed * deltaTime, 0.0f)
        }
    }

    private fun popStackToCurrentNode() {
        if (!path.empty()) {
            currentNode = path.pop()
        } else {
            currentNode = null
        }
    }
}