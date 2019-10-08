package com.adam.adventure.entity.debug

import com.adam.adventure.entity.Entity
import com.google.inject.Injector
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Entity which has its method invocations timed for debugging purposes
 */
class TimedEntity(name: String, id: Int, injector: Injector) : Entity(name, id, injector) {
    val log: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun activate() {
        val startTime = System.nanoTime()
        super.activate()
        log.info("Took {}ns to activate entity(name: {}, tileSetId: {})", System.nanoTime() - startTime, name, id)
    }

    override fun update(deltaTime: Float) {
        val startTime = System.nanoTime()
        super.update(deltaTime)
        log.info("Took {}ns to update entity(name: {}, tileSetId: {})", System.nanoTime() - startTime, name, id)
    }


    override fun destroy() {
        val startTime = System.nanoTime()
        super.destroy()
        log.info("Took {}ns to destroy entity(name: {}, tileSetId: {})", System.nanoTime() - startTime, name, id)
    }
}