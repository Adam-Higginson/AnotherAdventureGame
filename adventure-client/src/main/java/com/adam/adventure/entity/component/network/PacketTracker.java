package com.adam.adventure.entity.component.network;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Class designed to keep track of which packets have been sent and which have been ACKed
 */
public class PacketTracker {
    private final AtomicLong nextPacketId;


    public PacketTracker() {
        nextPacketId = new AtomicLong();
    }

    public long getNextPacketId() {
        return nextPacketId.getAndIncrement();
    }
}
