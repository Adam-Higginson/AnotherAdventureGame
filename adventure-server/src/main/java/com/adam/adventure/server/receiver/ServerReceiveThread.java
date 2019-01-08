package com.adam.adventure.server.receiver;

import com.adam.adventure.lib.flatbuffer.schema.packet.Packet;
import com.adam.adventure.lib.flatbuffer.schema.packet.PacketType;
import com.google.inject.assistedinject.Assisted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.function.Consumer;

public class ServerReceiveThread implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(ServerReceiveThread.class);

    private final DatagramSocket datagramSocket;
    private final Map<Byte, Consumer<Packet>> packetProcessors;
    private boolean running;

    @Inject
    public ServerReceiveThread(@Assisted DatagramSocket datagramSocket,
                               final Map<Byte, Consumer<Packet>> packetProcessors) {
        this.datagramSocket = datagramSocket;
        this.packetProcessors = packetProcessors;
        this.running = true;
    }


    public void stop() {
        this.running = false;
    }

    @Override
    public void run() {
        final byte[] buffer = new byte[256];

        while (running) {
            try {
                final Packet packet = receivePacket(buffer);
                processPacket(packet);
            } catch (Exception e) {
                LOG.error("Exception thrown when receiving packet", e);
            }

        }
    }

    private Packet receivePacket(byte[] buffer) throws IOException {
        final DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
        datagramSocket.receive(datagramPacket);

        final ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, datagramPacket.getOffset(), datagramPacket.getLength());
        return Packet.getRootAsPacket(byteBuffer);
    }

    private void processPacket(Packet packet) {
        Consumer<Packet> packetProcessor = packetProcessors.getOrDefault(packet.packetType(), this::logUnhandledPacket);
        packetProcessor.accept(packet);
    }

    private void logUnhandledPacket(Packet packet) {
        LOG.error("Could not find handler for packet type: {}", PacketType.name(packet.packetType()));
    }


}
