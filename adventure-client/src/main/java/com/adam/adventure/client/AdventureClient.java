package com.adam.adventure.client;

import com.adam.adventure.lib.flatbuffer.schema.Player;
import com.adam.adventure.lib.flatbuffer.schema.Vec3;
import com.google.flatbuffers.FlatBufferBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class AdventureClient {
    private static final Logger LOG = LoggerFactory.getLogger(AdventureClient.class);
    private final DatagramSocket datagramSocket;

    public AdventureClient() throws SocketException {
        datagramSocket = new DatagramSocket();
        final DatagramSocket socket = datagramSocket;
    }

    public void send() {
        final FlatBufferBuilder builder = new FlatBufferBuilder(256);
        Player.startPlayer(builder);
        Player.addPosition(builder, Vec3.createVec3(builder, 1.0f, 2.0f, 3.0f));
        final int player = Player.endPlayer(builder);
        builder.finish(player);
        final byte[] buffer = builder.sizedByteArray();

        InetAddress address = null;
        try {
            address = InetAddress.getByName("localhost");
            final DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 4445);
            LOG.info("Sending packet...");
            datagramSocket.send(packet);
            LOG.info("Sent.");
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
