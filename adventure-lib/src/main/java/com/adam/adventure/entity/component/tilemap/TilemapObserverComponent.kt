package com.adam.adventure.entity.component.tilemap

import com.adam.adventure.entity.EntityComponent
import com.adam.adventure.entity.component.tilemap.event.TilemapEntityTransformEvent
import com.adam.adventure.event.EventBus
import org.joml.Matrix4f
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Class which allows the [TilemapComponent] to be notified of any translations for this entity. Entities
 * which are not registered with this will not have their locations tracked by the [TilemapComponent]
 */
class TilemapObserverComponent : EntityComponent() {
    val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private var eventBus: EventBus? = null

    private var beforeTransform: Matrix4f? = null

    override fun beforeUpdate(deltaTime: Float) {
        beforeTransform = Matrix4f(transformComponent.transform)
    }

    override fun afterUpdate(deltaTime: Float) {
        if (beforeTransform != null &&
                !transformComponent.transform.equals(beforeTransform, 0.001f)) {
            eventBus!!.publishEvent(TilemapEntityTransformEvent(entity))
        }
    }
}