package com.adam.adventure.server.tick.event;

import com.adam.adventure.event.Event;

/**
 * An event which is processed on invocation of a server tick. The {@link com.adam.adventure.server.tick.ServerTick}
 * class invokes all queued events of this type on each tick.
 */
public abstract class ServerTickEvent extends Event {

}
