package com.adam.adventure.server.entity.component.ai

import com.adam.adventure.entity.EntityComponent
import com.adam.adventure.entity.component.event.ComponentEvent
import com.adam.adventure.entity.component.event.MovementComponentEvent
import com.adam.adventure.entity.component.tilemap.TilemapComponent
import com.adam.adventure.scene.SceneManager
import org.joml.Vector3f
import org.slf4j.LoggerFactory
import java.lang.Math.*
import java.util.*
import javax.inject.Inject

/**
 * Class which moves an entity along a given path
 */
class PathMovingComponent(private val speed : Float) : EntityComponent() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private enum class DirectionAnimation(val animationName: MovementComponentEvent.MovementType) {
        EAST(MovementComponentEvent.MovementType.ENTITY_MOVE_EAST),
        NORTH(MovementComponentEvent.MovementType.ENTITY_MOVE_NORTH),
        WEST(MovementComponentEvent.MovementType.ENTITY_MOVE_WEST),
        SOUTH(MovementComponentEvent.MovementType.ENTITY_MOVE_SOUTH)
    }

    @Inject
    private lateinit var sceneManager : SceneManager

    private var path: Stack<PathNode> = Stack()
    private var currentNode : PathNode? = null


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

        if (!ourTile.walkable) {
            log.warn("Our tile is not walkable! {}", ourTile)
        }
        if(currentNode?.tile?.walkable == false) {
            log.warn("Path is taking us on a non-walkable tile of: {}", currentNode?.tile)
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

            calculateAnimation(direction)
        }
    }

    private fun calculateAnimation(direction : Vector3f) {

        val angle = atan2(direction.y.toDouble(), direction.x.toDouble())
        val quadrant = round(4 * angle / (2*PI) + 4) % 4
        val animation = DirectionAnimation.values()[quadrant.toInt()].animationName
        broadcastComponentEvent(MovementComponentEvent(animation))
    }

    private fun popStackToCurrentNode() {
        if (!path.empty()) {
            currentNode = path.pop()
        } else {
            currentNode = null
        }
    }
}