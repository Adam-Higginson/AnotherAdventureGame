package com.adam.adventure.scene

import com.adam.adventure.event.Event

data class NewSceneActivatedEvent(val newScene : Scene) : Event()