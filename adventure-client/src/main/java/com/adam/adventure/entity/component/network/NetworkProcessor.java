package com.adam.adventure.entity.component.network;

import com.adam.adventure.domain.WorldState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

class NetworkProcessor {
    private static final int SOCKET_TIMEOUT_MILLIS = 200;
    private static final Logger LOG = LoggerFactory.getLogger(NetworkProcessor.class);

    private final DatagramSocket socket;
    private final PacketFactory packetFactory;
    private InetAddress serverAddress;
    private int serverPort;
    private WorldState initialWorldState;

    /**
     * Creates a new network processor, which will use the given serverAddress/serverPort to send events to.
     */
    private NetworkProcessor(final InetAddress serverAddress, final int serverPort) throws SocketException {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.packetFactory = new PacketFactory();
        this.socket = new DatagramSocket();
    }

    static NetworkProcessor login(String username, InetAddress address, int port) throws IOException {
        NetworkProcessor networkProcessor = new NetworkProcessor(address, port);
        networkProcessor.login(username);
        return networkProcessor;
    }

    public WorldState getInitialWorldState() {
        return initialWorldState;
    }

    private void login(final String username) throws IOException {
        LOG.info("Logging into server with address: {}, port: {}, for username: {}", serverAddress, serverPort, username);
        final byte[] loginPacket = packetFactory.newLoginPacket(username);
        final DatagramPacket packet = new DatagramPacket(loginPacket, loginPacket.length, serverAddress, serverPort);
        socket.send(packet);

        this.initialWorldState = awaitWorldState();
    }

    private WorldState awaitWorldState() throws IOException {
        final byte[] buffer = new byte[256];
        final DatagramPacket incomingPacket = new DatagramPacket(buffer, buffer.length);
        socket.setSoTimeout(SOCKET_TIMEOUT_MILLIS);
        socket.receive(incomingPacket);

        return packetFactory.fromPacket(buffer, incomingPacket);
    }
}
