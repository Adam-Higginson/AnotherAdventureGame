package com.adam.adventure.server.tick;

public interface ServerTickSchedulerFactory {
    ServerTickScheduler create(final int tickrate);
}
