package com.adam.adventure.server.tick;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class OutputPacketQueue {

    /**
     * Each function on the queue is a function which takes a packet id and a timestamp and produces a datagram packet
     */
    private final BlockingQueue<BiFunction<Long, Long, DatagramPacket>> outputPackets;

    public OutputPacketQueue() {
        this.outputPackets = new LinkedBlockingQueue<>();
    }

    public void addOutputPacketSupplier(final BiFunction<Long, Long, DatagramPacket> outputMessage) {
        outputPackets.add(outputMessage);
    }

    List<BiFunction<Long, Long, DatagramPacket>> drain() {
        final List<BiFunction<Long, Long, DatagramPacket>> packets = new ArrayList<>(outputPackets.size());
        outputPackets.drainTo(packets);
        return packets;
    }

}
