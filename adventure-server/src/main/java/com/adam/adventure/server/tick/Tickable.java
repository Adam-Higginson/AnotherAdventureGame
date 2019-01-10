package com.adam.adventure.server.tick;

public interface Tickable {
    /**
     * Called on a new tick event
     * @param outputMessageQueue the queue of output messages. Can be added to if any outbound messages are required.
     */
    void tick(final OutputMessageQueue outputMessageQueue);
}
