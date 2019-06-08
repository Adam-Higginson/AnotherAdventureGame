package com.adam.adventure.server.tick;

import com.adam.adventure.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OnNewServerTickEvent extends Event {
    private final OutputPacketQueue outputPacketQueue;
    private final long deltaTime;
}
