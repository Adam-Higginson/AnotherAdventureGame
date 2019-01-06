package com.adam.adventure.client.event;

import com.adam.adventure.event.Event;

import java.net.DatagramSocket;

public abstract class NetworkEvent extends Event {

    public abstract void handle(final DatagramSocket datagramSocket);
}
