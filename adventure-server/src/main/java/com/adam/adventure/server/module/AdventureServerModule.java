package com.adam.adventure.server.module;

import com.adam.adventure.entity.EntityModule;
import com.adam.adventure.entity.component.tilemap.data.JsonTileMapLoader;
import com.adam.adventure.entity.component.tilemap.data.JsonTileSetLoader;
import com.adam.adventure.entity.component.tilemap.data.TileMapLoader;
import com.adam.adventure.entity.component.tilemap.data.TileSetLoader;
import com.adam.adventure.entity.repository.EntityRepository;
import com.adam.adventure.event.EventBus;
import com.adam.adventure.scene.SceneManager;
import com.adam.adventure.server.entity.repository.ServerEntityRepository;
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
    private final long tickrate;

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
            bind(SceneManager.class).in(Singleton.class);
            bind(Long.class).annotatedWith(Names.named("tickrate")).toInstance(tickrate);
            bind(EntityRepository.class).to(ServerEntityRepository.class).in(Singleton.class);
            bind(TileMapLoader.class).to(JsonTileMapLoader.class).in(Singleton.class);
            bind(TileSetLoader.class).to(JsonTileSetLoader.class).in(Singleton.class);


            install(new EntityModule());
            install(new ReceiverModule());
            install(new TickModule());
        } catch (final SocketException e) {
            throw new IllegalStateException(e);
        }
    }
}
