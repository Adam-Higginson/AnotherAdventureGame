package com.adam.adventure.server.module;

import com.adam.adventure.entity.EntityModule;
import com.adam.adventure.server.receiver.ReceiverModule;
import com.adam.adventure.server.tick.TickModule;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

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
                    .annotatedWith(Names.named("serverDatagramSocket"))
                    .toInstance(new DatagramSocket(port));

            install(new EntityModule());
            install(new ReceiverModule());
            install(new TickModule());
        } catch (SocketException e) {
            throw new IllegalStateException(e);
        }
    }
}
