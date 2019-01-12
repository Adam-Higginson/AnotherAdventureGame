package com.adam.adventure.server.tick;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;

public class OutputPacketQueue {

    private final BlockingQueue<Supplier<DatagramPacket>> outputPackets;

    public OutputPacketQueue() {
        this.outputPackets = new LinkedBlockingQueue<>();
    }

    public void addOutputPacket(final Supplier<DatagramPacket> outputMessage) {
        outputPackets.add(outputMessage);
    }

    List<Supplier<DatagramPacket>> drain() {
        final List<Supplier<DatagramPacket>> packets = new ArrayList<>(outputPackets.size());
        outputPackets.drainTo(packets);
        return packets;
    }

}
