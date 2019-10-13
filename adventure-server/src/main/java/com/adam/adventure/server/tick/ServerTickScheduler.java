package com.adam.adventure.server.tick;

import com.google.inject.assistedinject.Assisted;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Schedules and manages 'ticks' of the server
 */
@Slf4j
public class ServerTickScheduler {

    private final ServerTick serverTick;
    private final long millisPerTick;
    private final ScheduledExecutorService scheduledExecutorService;

    @Inject
    public ServerTickScheduler(final ServerTick serverTick,
                               @Assisted final long tickrate) {
        this.serverTick = serverTick;
        this.millisPerTick = 1000 / tickrate;
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        LOG.info("Starting server tick with millisPerTick: {}", millisPerTick);
        scheduledExecutorService.scheduleAtFixedRate(serverTick, millisPerTick, millisPerTick, TimeUnit.MILLISECONDS);
        LOG.info("Server tick started.");
    }

    public void stop() throws InterruptedException {
        LOG.info("Stopping server tick...");
        scheduledExecutorService.shutdown();
        serverTick.stop();
        scheduledExecutorService.awaitTermination(1, TimeUnit.SECONDS);
    }
}
