package com.adam.adventure.server.tick;

import com.adam.adventure.event.EventBus;
import com.adam.adventure.event.EventSubscribe;
import com.adam.adventure.server.module.ServerDatagramSocket;
import com.adam.adventure.server.tick.event.ServerTickEvent;
import com.adam.adventure.server.tick.event.processor.ServerTickEventProcessorRegistry;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
class ServerTick implements Runnable {

    private final BlockingQueue<ServerTickEvent> serverTickEvents;
    private final DatagramSocket datagramSocket;
    private final EventBus eventBus;
    private final ServerTickEventProcessorRegistry serverTickEventProcessorRegistry;

    /**
     * Messages which need to be written to the socket
     */
    private final OutputPacketQueue outputPacketQueue;

    @Inject
    public ServerTick(
            @ServerDatagramSocket final DatagramSocket datagramSocket,
            final EventBus eventBus,
            final ServerTickEventProcessorRegistry serverTickEventProcessorRegistry) {
        this.datagramSocket = datagramSocket;
        this.eventBus = eventBus;
        this.serverTickEventProcessorRegistry = serverTickEventProcessorRegistry;
        this.serverTickEvents = new LinkedBlockingQueue<>();
        this.outputPacketQueue = new OutputPacketQueue();

        eventBus.register(this);
    }

    /**
     * Executes a single invocation of a server tick.
     */
    @Override
    public void run() {
        handleNewEvents();
        publishServerTickEvent();
        writeOutputMessages();
    }

    private void handleNewEvents() {
        final List<ServerTickEvent> eventsToHandle = new ArrayList<>(serverTickEvents.size());
        serverTickEvents.drainTo(eventsToHandle);

        eventsToHandle.forEach(event -> {
            try {
                serverTickEventProcessorRegistry.process(event);
            } catch (final Exception e) {
                LOG.error("Error when processing event: {}", event.getClass().getSimpleName(), e);
            }
        });
    }

    private void publishServerTickEvent() {
        eventBus.publishEvent(new OnNewServerTickEvent(outputPacketQueue));
    }


    private void writeOutputMessages() {
        outputPacketQueue.drain().forEach(outputPacketSupplier -> {
            try {
                final DatagramPacket datagramPacket = outputPacketSupplier.get();
                datagramSocket.send(datagramPacket);
            } catch (final Throwable t) {
                LOG.error("Error when writing output message", t);
            }
        });
    }

    @EventSubscribe
    public void onServerTickEvent(final ServerTickEvent serverTickEvent) {
        serverTickEvents.add(serverTickEvent);
    }
}
