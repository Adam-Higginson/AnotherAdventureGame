package com.adam.adventure.server.tick;

import com.adam.adventure.event.EventBus;
import com.adam.adventure.event.EventSubscribe;
import com.adam.adventure.server.module.ServerDatagramSocket;
import com.adam.adventure.server.player.PlayerSessionRegistry;
import com.adam.adventure.server.state.WorldStateTickable;
import com.adam.adventure.server.tick.event.ServerTickEvent;
import com.adam.adventure.server.tick.event.processor.ServerTickEventProcessorRegistry;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
class ServerTick implements Runnable {

    private final ConcurrentLinkedQueue<ServerTickEvent> serverTickEvents;
    private final Lock serverTickEventsLock;
    private final DatagramSocket datagramSocket;
    private final ServerTickEventProcessorRegistry serverTickEventProcessorRegistry;
    private final PlayerSessionRegistry playerSessionRegistry;
    private final WorldStateTickable worldStateTickable;

    /**
     * Messages which need to be written to the socket
     */
    private final OutputMessageQueue outputMessageQueue;

    @Inject
    public ServerTick(
            @ServerDatagramSocket final DatagramSocket datagramSocket,
            final EventBus eventBus,
            final ServerTickEventProcessorRegistry serverTickEventProcessorRegistry,
            final PlayerSessionRegistry playerSessionRegistry,
            final WorldStateTickable worldStateTickable) {
        this.datagramSocket = datagramSocket;
        this.serverTickEventProcessorRegistry = serverTickEventProcessorRegistry;
        this.playerSessionRegistry = playerSessionRegistry;
        this.worldStateTickable = worldStateTickable;
        this.serverTickEvents = new ConcurrentLinkedQueue<>();
        this.serverTickEventsLock = new ReentrantLock();
        this.outputMessageQueue = new OutputMessageQueue();

        eventBus.register(this);
    }

    /**
     * Executes a single invocation of a server tickTickables.
     */
    @Override
    public void run() {
        handleNewEvents();
        tickTickables();
        writeOutputMessages();
    }

    private void handleNewEvents() {
        final List<ServerTickEvent> eventsToHandle = drainQueue();

        eventsToHandle.forEach(event -> {
            try {
                serverTickEventProcessorRegistry.process(event);
            } catch (Exception e) {
                LOG.error("Error when processing event: {}", event.getClass().getSimpleName(), e);
            }
        });
    }

    private void tickTickables() {
        playerSessionRegistry.tick(outputMessageQueue);
        worldStateTickable.tick(outputMessageQueue);
    }

    private void writeOutputMessages() {
        outputMessageQueue.pollEach(outputMessage -> {
            try {
                outputMessage.write(datagramSocket);
            } catch (Exception e) {
                LOG.error("Error when writing output message", e);
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
