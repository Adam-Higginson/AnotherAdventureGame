package com.adam.adventure.server.tick;

import com.adam.adventure.event.EventBus;
import com.adam.adventure.event.EventSubscribe;
import com.adam.adventure.server.tick.event.ServerTickEvent;
import com.adam.adventure.server.tick.event.processor.ServerTickEventProcessorRegistry;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
class ServerTick implements Runnable {

    private final ConcurrentLinkedQueue<ServerTickEvent> serverTickEvents;
    private final Lock serverTickEventsLock;
    private final EventBus eventBus;
    private ServerTickEventProcessorRegistry serverTickEventProcessorRegistry;

    @Inject
    public ServerTick(final EventBus eventBus, final ServerTickEventProcessorRegistry serverTickEventProcessorRegistry) {
        this.eventBus = eventBus;
        this.serverTickEventProcessorRegistry = serverTickEventProcessorRegistry;
        this.serverTickEvents = new ConcurrentLinkedQueue<>();
        this.serverTickEventsLock = new ReentrantLock();
        eventBus.register(this);
    }

    /**
     * Executes a single invocation of a server tick.
     */
    @Override
    public void run() {
        final List<ServerTickEvent> eventsToHandle = drainQueue();

        eventsToHandle.forEach(event -> {
            try {
                serverTickEventProcessorRegistry.process(event);
            } catch (Exception e) {
                LOG.error("Error when processing event: {}", event.getClass().getSimpleName(), e);
            }
        });

    }

    private List<ServerTickEvent> drainQueue() {
        final List<ServerTickEvent> eventsToHandle = new ArrayList<>(serverTickEvents.size());
        try {
            serverTickEventsLock.lock();
            ServerTickEvent serverTickEvent = serverTickEvents.poll();
            while (serverTickEvent != null) {
                eventsToHandle.add(serverTickEvent);
                serverTickEvent = serverTickEvents.poll();
            }
        } finally {
            serverTickEventsLock.unlock();
        }
        return eventsToHandle;
    }

    @EventSubscribe
    public void onServerTickEvent(final ServerTickEvent serverTickEvent) {
        try {
            serverTickEventsLock.lock();
            serverTickEvents.add(serverTickEvent);
        } finally {
            serverTickEventsLock.unlock();
        }
    }
}
