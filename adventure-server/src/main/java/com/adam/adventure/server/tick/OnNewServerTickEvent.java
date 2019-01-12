package com.adam.adventure.server.tick;

import com.adam.adventure.event.Event;
import lombok.Getter;

@Getter
public class OnNewServerTickEvent extends Event {
    private final OutputPacketQueue outputPacketQueue;

    OnNewServerTickEvent(final OutputPacketQueue outputPacketQueue) {
        this.outputPacketQueue = outputPacketQueue;
    }
}
