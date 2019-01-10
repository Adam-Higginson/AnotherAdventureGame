package com.adam.adventure.server;

import com.adam.adventure.server.module.AdventureServerModule;
import com.adam.adventure.server.receiver.ServerReceiver;
import com.adam.adventure.server.tick.ServerTickScheduler;
import com.adam.adventure.server.tick.ServerTickSchedulerFactory;
import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@Slf4j
public class AdventureServer implements Runnable {

    @CommandLine.Option(names = {"-t", "--tickrate"}, description = "The tickrate of the server", required = true)
    private int tickrate;

    @CommandLine.Option(names = {"-p", "--port"}, description = "The port to bind the server to", required = true)
    private int port;

    public void run() {
        LOG.info("Starting adventure server on port: {} with tickrate: {}", port, tickrate);

        final Injector injector = Guice.createInjector(new AdventureServerModule(port));

        ServerTickScheduler serverTickScheduler = injector.getInstance(ServerTickSchedulerFactory.class)
                .create(tickrate);
        serverTickScheduler.start();
        final ServerReceiver serverReceiver = injector.getInstance(ServerReceiver.class);
        final Thread serverReceiveThread = new Thread(serverReceiver);
        serverReceiveThread.start();

        LOG.info("Adventure server started.");

        addShutdownHook(serverReceiver, serverReceiveThread, serverTickScheduler);
    }

    private void addShutdownHook(final ServerReceiver serverReceiver,
                                 final Thread serverReceiveThread,
                                 final ServerTickScheduler serverTickScheduler) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    LOG.info("Stopping adventure server...");
                    serverReceiver.stop();
                    serverReceiveThread.join(1000);
                    serverTickScheduler.stop();
                    LOG.info("Adventure server stopped");
                } catch (Exception e) {
                    LOG.error("Error when stopping adventure server", e);
                }
            }
        });
    }

    public static void main(String[] args) {
        CommandLine.run(new AdventureServer(), args);
    }
}
