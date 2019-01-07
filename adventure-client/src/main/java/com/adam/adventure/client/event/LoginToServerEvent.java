package com.adam.adventure.client.event;

import com.adam.adventure.lib.flatbuffer.schema.packet.LoginPacket;
import com.google.flatbuffers.FlatBufferBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
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
    public void handle(final DatagramSocket datagramSocket) throws IOException {
        LOG.info("Logging into server...");
        final FlatBufferBuilder builder = new FlatBufferBuilder(32);
        final int usernameId = builder.createString(getUsername());
        LoginPacket.startLoginPacket(builder);
        LoginPacket.addUsername(builder, usernameId);
        final int loginPacketId = LoginPacket.endLoginPacket(builder);
        builder.finish(loginPacketId);

        final byte[] buffer = builder.sizedByteArray();
        final DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 4445);
        datagramSocket.send(packet);
    }
}
