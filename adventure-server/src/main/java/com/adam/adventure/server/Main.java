package com.adam.adventure.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.net.SocketException;

public class Main implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    @CommandLine.Option(names = {"-p", "--port"}, description = "The port to bind the server to", required = true)
    private int port;

    public void run() {
        LOG.info("Starting adventure server...");

        try {
            final AdventureServer adventureServer = new AdventureServer(port);
            addShutdownHook(adventureServer);
            adventureServer.start();
        } catch (java.io.IOException e) {
            LOG.error("Error when attempting to run adventure server", e);
        }
    }

    private void addShutdownHook(final AdventureServer adventureServer) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    LOG.info("Stopping adventure server...");
                    adventureServer.stop();
                    LOG.info("Adventure server stopped");
                }
                catch (Exception e)
                {
                    LOG.error("Error when stopping adventure server", e);
                }
            }
        });
    }

    public static void main(String[] args)
    {
        CommandLine.run(new Main(), args);
    }
}
