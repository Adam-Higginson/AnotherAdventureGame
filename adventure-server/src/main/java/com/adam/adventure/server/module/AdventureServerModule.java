package com.adam.adventure.server.module;

import com.adam.adventure.entity.EntityModule;
import com.adam.adventure.event.EventBus;
import com.adam.adventure.server.player.PlayerSessionRegistry;
import com.adam.adventure.server.receiver.ReceiverModule;
import com.adam.adventure.server.state.WorldStateTickable;
import com.adam.adventure.server.tick.TickModule;
import com.google.inject.AbstractModule;

import javax.inject.Singleton;
import java.net.DatagramSocket;
import java.net.SocketException;

public class AdventureServerModule extends AbstractModule {

    private int port;

    public AdventureServerModule(int port) {
        this.port = port;
    }


    @Override
    protected void configure() {
        try {
            bind(DatagramSocket.class)
                    .annotatedWith(ServerDatagramSocket.class)
                    .toInstance(new DatagramSocket(port));
            bind(EventBus.class).toInstance(new EventBus());
            bind(WorldStateTickable.class).in(Singleton.class);
            bind(PlayerSessionRegistry.class).in(Singleton.class);

            install(new EntityModule());
            install(new ReceiverModule());
            install(new TickModule());
        } catch (SocketException e) {
            throw new IllegalStateException(e);
        }
    }
}
