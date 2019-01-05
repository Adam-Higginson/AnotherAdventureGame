package com.adam.adventure;

import com.adam.adventure.lib.flatbuffer.schema.Player;
import com.adam.adventure.lib.flatbuffer.schema.Vec3;
import com.google.flatbuffers.FlatBufferBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;

public class AdventureClient {
    private static final Logger LOG = LoggerFactory.getLogger(AdventureClient.class);
    private final DatagramSocket datagramSocket;

    public AdventureClient() throws SocketException {
        datagramSocket = new DatagramSocket();
        DatagramSocket socket = datagramSocket;
    }

    public void send()
    {
        FlatBufferBuilder builder = new FlatBufferBuilder(256);
        Player.startPlayer(builder);
        Player.addPosition(builder, Vec3.createVec3(builder, 1.0f, 2.0f, 3.0f));
        int player = Player.endPlayer(builder);
        builder.finish(player);
        byte[] buffer = builder.sizedByteArray();

        InetAddress address = null;
        try {
            address = InetAddress.getByName("localhost");
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 4445);
            LOG.info("Sending packet...");
            datagramSocket.send(packet);
            LOG.info("Sent.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
