package com.adam.adventure.server.receiver;

import com.adam.adventure.lib.flatbuffer.schema.packet.Packet;
import com.adam.adventure.lib.flatbuffer.schema.packet.PacketType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
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
    ServerReceiver(@Named("serverDatagramSocket") final DatagramSocket datagramSocket,
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
        final byte[] buffer = new byte[256];

        while (running) {
            try {
                final DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(datagramPacket);
                final Packet packet = receivePacket(datagramPacket, buffer);
                processPacket(datagramPacket, packet);
            } catch (Exception e) {
                LOG.error("Exception thrown when receiving packet", e);
            }

        }
    }

    private Packet receivePacket(final DatagramPacket datagramPacket, byte[] buffer) throws IOException {

        final ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, datagramPacket.getOffset(), datagramPacket.getLength());
        return Packet.getRootAsPacket(byteBuffer);
    }

    private void processPacket(final DatagramPacket datagramPacket, final Packet packet) {
        BiConsumer<DatagramPacket, Packet> packetProcessor = packetTypeToPacketProcessors.getOrDefault(packet.packetType(),
                (dp, p) -> logUnhandledPacket(p));
        packetProcessor.accept(datagramPacket, packet);
    }

    private void logUnhandledPacket(Packet packet) {
        LOG.error("Could not find handler for packet type: {}", packet.packetType());
    }


}
