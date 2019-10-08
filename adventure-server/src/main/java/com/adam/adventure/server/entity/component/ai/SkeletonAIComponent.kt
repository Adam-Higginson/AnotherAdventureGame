package com.adam.adventure.server.entity.component.ai

import com.adam.adventure.entity.Entity
import com.adam.adventure.entity.EntityComponent
import com.adam.adventure.entity.component.event.ComponentEvent
import com.adam.adventure.scene.Scene
import com.adam.adventure.scene.SceneManager
import org.joml.Matrix4f
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject

class SkeletonAIComponent : EntityComponent() {
    //Amount of millis between recalculating targets
    private val targetRecalculationTime = 2000L
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    private enum class State { SELECTING_TARGET, FIND_PATH_TO_TARGET, MOVING_TO_TARGET }

    @Inject
    private lateinit var sceneManager: SceneManager

    private var state = State.SELECTING_TARGET
    private var target: Entity? = null
    private var lastFoundTargetTransform : Matrix4f? = null
    private var lastRecalculatedPathTime : Long = System.currentTimeMillis()


    override fun update(deltaTime: Float) {
        if (sceneManager.currentScene.isEmpty) {
            return
        }

        val scene = sceneManager.currentScene.get()

        when(state) {
            State.SELECTING_TARGET -> {
                selectTarget(scene)
                state = State.FIND_PATH_TO_TARGET
            }

            State.FIND_PATH_TO_TARGET -> {
                if (System.currentTimeMillis() - lastRecalculatedPathTime >= targetRecalculationTime) {
                    lastRecalculatedPathTime = System.currentTimeMillis()
                    target?.let { broadcastComponentEvent(FindPathRequestEvent(it)) }
                    state = State.MOVING_TO_TARGET
                }
            }

            State.MOVING_TO_TARGET -> {
                checkForTargetMoving()
            }
        }
    }

    override fun onComponentEvent(componentEvent: ComponentEvent?) {
        if (componentEvent is PathFoundEvent) {
            val path = componentEvent.path
            broadcastComponentEvent(MoveAlongPathRequestEvent(path))
            state = State.MOVING_TO_TARGET
        }
    }

    private fun selectTarget(scene: Scene) {
        val players = scene.findEntitiesByName("Player")
        if (players.isNotEmpty()) {
            target = players.random()
            lastFoundTargetTransform = Matrix4f(target!!.transform)
        }
    }

    private fun checkForTargetMoving() {
        target?.let {
            if (it.transform != lastFoundTargetTransform) {
                lastFoundTargetTransform = Matrix4f(target!!.transform)
                state = State.FIND_PATH_TO_TARGET
            }
        }
    }
}