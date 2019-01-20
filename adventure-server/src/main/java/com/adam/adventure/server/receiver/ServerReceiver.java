package com.adam.adventure.server.receiver;

import com.adam.adventure.lib.flatbuffer.schema.packet.Packet;
import com.adam.adventure.lib.flatbuffer.schema.packet.PacketBatch;
import com.adam.adventure.server.module.ServerDatagramSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.function.BiConsumer;

public class ServerReceiver implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(ServerReceiver.class);

    private final DatagramSocket datagramSocket;
    private final Map<Byte, BiConsumer<DatagramPacket, Packet>> packetTypeToPacketProcessors;
    private boolean running;

    @Inject
    ServerReceiver(@ServerDatagramSocket final DatagramSocket datagramSocket,
                   final Map<Byte, BiConsumer<DatagramPacket, Packet>> packetTypeToPacketProcessors) {
        this.datagramSocket = datagramSocket;
        this.packetTypeToPacketProcessors = packetTypeToPacketProcessors;
        this.running = true;
    }


    public void stop() {
        this.running = false;
    }

    @Override
    public void run() {
        final byte[] buffer = new byte[1024];

        while (running) {
            try {
                final DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(datagramPacket);
                final PacketBatch packetBatch = receivePacketBatch(datagramPacket, buffer);
                for (int i = 0; i < packetBatch.packetsLength(); i++) {
                    processPacket(datagramPacket, packetBatch.packets(i));
                }
            } catch (final Exception e) {
                LOG.error("Exception thrown when receiving packet", e);
            }
        }
    }

    private PacketBatch receivePacketBatch(final DatagramPacket datagramPacket, final byte[] buffer) {
        final ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, datagramPacket.getOffset(), datagramPacket.getLength());
        return PacketBatch.getRootAsPacketBatch(byteBuffer);
    }

    private void processPacket(final DatagramPacket datagramPacket, final Packet packet) {
        final BiConsumer<DatagramPacket, Packet> packetProcessor = packetTypeToPacketProcessors.getOrDefault(packet.packetType(),
                (dp, p) -> logUnhandledPacket(p));
        packetProcessor.accept(datagramPacket, packet);
    }

    private void logUnhandledPacket(final Packet packet) {
        LOG.error("Could not find handler for packet type: {}", packet.packetType());
    }


}
