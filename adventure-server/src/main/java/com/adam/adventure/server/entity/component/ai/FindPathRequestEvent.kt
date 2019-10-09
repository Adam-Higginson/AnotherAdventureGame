package com.adam.adventure.server.entity.component.ai

import com.adam.adventure.entity.Entity
import com.adam.adventure.entity.component.event.ComponentEvent

data class FindPathRequestEvent(val target : Entity) : ComponentEvent()
