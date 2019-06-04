package com.adam.adventure.server;

import com.adam.adventure.server.module.AdventureServerModule;
import com.adam.adventure.server.module.ServerDatagramSocket;
import com.adam.adventure.server.player.PlayerLoginCompleter;
import com.adam.adventure.server.receiver.ServerReceiver;
import com.adam.adventure.server.state.WorldStateManager;
import com.adam.adventure.server.tick.ServerTickScheduler;
import com.adam.adventure.server.tick.ServerTickSchedulerFactory;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.net.DatagramSocket;

@Slf4j
public class AdventureServer implements Runnable {

    @CommandLine.Option(names = {"-t", "--tickrate"}, description = "The tickrate of the server", required = true)
    private int tickrate;

    @CommandLine.Option(names = {"-p", "--port"}, description = "The port to bind the server to", required = true)
    private int port;

    @Override
    public void run() {
        LOG.info("Starting adventure server on port: {} with tickrate: {}", port, tickrate);

        final Injector injector = Guice.createInjector(new AdventureServerModule(port));
        //Creates instance and allows it to start listening for events.
        injector.getInstance(PlayerLoginCompleter.class);
        injector.getInstance(WorldStateManager.class);

        final ServerTickScheduler serverTickScheduler = injector.getInstance(ServerTickSchedulerFactory.class)
                .create(tickrate);
        serverTickScheduler.start();
        final ServerReceiver serverReceiver = injector.getInstance(ServerReceiver.class);
        final Thread serverReceiveThread = new Thread(serverReceiver);
        serverReceiveThread.start();

        LOG.info("Adventure server started.");

        addShutdownHook(serverReceiver,
                serverReceiveThread,
                serverTickScheduler,
                injector.getInstance(Key.get(DatagramSocket.class, ServerDatagramSocket.class)));
    }

    private void addShutdownHook(final ServerReceiver serverReceiver,
                                 final Thread serverReceiveThread,
                                 final ServerTickScheduler serverTickScheduler,
                                 final DatagramSocket datagramSocket) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                LOG.info("Stopping adventure server...");
                serverReceiver.stop();
                serverReceiveThread.join(1000);
                serverTickScheduler.stop();
                datagramSocket.close();
                LOG.info("Adventure server stopped");
            } catch (final Exception e) {
                LOG.error("Error when stopping adventure server", e);
            }
        }));
    }

    public static void main(final String[] args) {
        CommandLine.run(new AdventureServer(), args);
    }
}
