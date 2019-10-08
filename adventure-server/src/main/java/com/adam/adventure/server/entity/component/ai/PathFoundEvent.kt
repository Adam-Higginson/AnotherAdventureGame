package com.adam.adventure.server.entity.component.ai

import com.adam.adventure.entity.component.event.ComponentEvent
import java.util.*

data class PathFoundEvent(val path: Stack<PathNode>) : ComponentEvent()