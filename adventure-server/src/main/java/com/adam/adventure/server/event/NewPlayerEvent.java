package com.adam.adventure.server.event;

import com.adam.adventure.server.tick.event.ServerTickEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.InetAddress;

@Getter
@AllArgsConstructor
public class NewPlayerEvent extends ServerTickEvent {
    private final String username;
    private final InetAddress address;
    private final int port;
}
