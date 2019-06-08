package com.adam.adventure.server.module;

import com.adam.adventure.entity.EntityModule;
import com.adam.adventure.event.EventBus;
import com.adam.adventure.server.player.PlayerLoginCompleter;
import com.adam.adventure.server.player.PlayerSessionRegistry;
import com.adam.adventure.server.receiver.ReceiverModule;
import com.adam.adventure.server.tick.TickModule;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import javax.inject.Singleton;
import java.net.DatagramSocket;
import java.net.SocketException;

public class AdventureServerModule extends AbstractModule {

    private final int port;
    private long tickrate;

    public AdventureServerModule(final int port, final long tickrate) {
        this.port = port;
        this.tickrate = tickrate;
    }


    @Override
    protected void configure() {
        try {
            bind(DatagramSocket.class)
                    .annotatedWith(ServerDatagramSocket.class)
                    .toInstance(new DatagramSocket(port));
            bind(EventBus.class).toInstance(new EventBus());
            bind(PlayerSessionRegistry.class).in(Singleton.class);
            bind(PlayerLoginCompleter.class).in(Singleton.class);
            bind(Long.class).annotatedWith(Names.named("tickrate")).toInstance(tickrate);

            install(new EntityModule());
            install(new ReceiverModule());
            install(new TickModule());
        } catch (final SocketException e) {
            throw new IllegalStateException(e);
        }
    }
}
