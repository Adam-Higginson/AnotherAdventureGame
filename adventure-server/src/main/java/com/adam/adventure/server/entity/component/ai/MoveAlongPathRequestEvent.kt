package com.adam.adventure.server.entity.component.ai

import com.adam.adventure.entity.component.event.ComponentEvent
import java.util.*

data class MoveAlongPathRequestEvent(val path : Stack<PathFindingComponent.PathNode>) : ComponentEvent()
