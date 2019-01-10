package com.adam.adventure.server.tick;

import java.net.DatagramSocket;

@FunctionalInterface
public interface OutputMessage {
    void write(final DatagramSocket datagramSocket);
}
