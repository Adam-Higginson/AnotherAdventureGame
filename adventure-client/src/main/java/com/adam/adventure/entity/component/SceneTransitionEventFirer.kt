package com.adam.adventure.entity.component

import com.adam.adventure.entity.EntityComponent
import com.adam.adventure.event.Event
import com.adam.adventure.event.EventBus
import com.adam.adventure.event.EventSubscribe
import com.adam.adventure.scene.NewSceneActivatedEvent
import javax.inject.Inject

class SceneTransitionEventFirer constructor(private val eventToFireProducer : () -> Event,
                                            vararg scenesToFireOn : String) : EntityComponent() {

    private val scenesToFireOnLowerCase : Set<String> = scenesToFireOn.map(String::toLowerCase).toSet()

    @Inject
    private var eventBus : EventBus? = null

    override fun activate() {
        eventBus!!.register(this)
    }

    override fun destroy() {
        eventBus!!.unsubscribe(this)
    }

    @EventSubscribe
    fun onNewSceneActivated(newSceneEvent: NewSceneActivatedEvent) {
        val newSceneName = newSceneEvent.newScene.name.toLowerCase()
        if (scenesToFireOnLowerCase.contains(newSceneName)) {
            eventBus!!.publishEvent(eventToFireProducer.invoke())
        }
    }
}