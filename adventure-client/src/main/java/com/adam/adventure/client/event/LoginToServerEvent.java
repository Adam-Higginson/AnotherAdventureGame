package com.adam.adventure.client.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class LoginToServerEvent extends NetworkEvent {
    private static final Logger LOG = LoggerFactory.getLogger(LoginToServerEvent.class);

    private final String username;
    private final InetAddress address;
    private final int port;

    public LoginToServerEvent(final String username, final InetAddress address, final int port) {
        this.username = username;
        this.address = address;
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    @Override
    public void handle(final DatagramSocket datagramSocket) {
        LOG.info("Logging into server...");
    }
}
