package com.adam.adventure.server;

import com.adam.adventure.lib.flatbuffer.schema.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class AdventureServer {
    private static final Logger LOG = LoggerFactory.getLogger(AdventureServer.class);

    private boolean running;
    private int port;

    public AdventureServer(int port) throws SocketException {
        this.port = port;
    }

    public void start() throws IOException {
        running = true;
        final DatagramSocket datagramSocket = new DatagramSocket(port);
        acceptData(datagramSocket);
    }

    private void acceptData(DatagramSocket datagramSocket) throws IOException {
        byte[] buffer = new byte[256];

        while (running) {

            // receive request
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            datagramSocket.receive(packet);
            final ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, packet.getOffset(), packet.getLength());
            final Player player = Player.getRootAsPlayer(byteBuffer);

            LOG.info("Received player with position: {}", player.position().x());
        }

    }

    public void stop() {
        running = false;
    }
}
