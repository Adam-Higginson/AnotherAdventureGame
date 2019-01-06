package com.adam.adventure.client;

import com.adam.adventure.client.event.NetworkEvent;
import com.adam.adventure.event.EventBus;
import com.adam.adventure.event.EventSubscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.net.DatagramSocket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NetworkClient {

    private static final Logger LOG = LoggerFactory.getLogger(NetworkClient.class);

    private final Queue<NetworkEvent> eventQueue;
    private final NetworkProcessor networkProcessor;
    private Thread networkProcessorThread;

    @Inject
    public NetworkClient(final EventBus eventBus) {
        this.networkProcessor = new NetworkProcessor();
        this.eventQueue = new ConcurrentLinkedQueue<>();
        eventBus.register(this);
    }


    public void start() {
        if (!networkProcessor.running) {
            LOG.info("Starting network processor...");
            networkProcessor.running = true;
            networkProcessorThread = new Thread(networkProcessor);
            networkProcessorThread.start();
            LOG.info("Network processor started...");
        }
    }

    public void stop() {
        LOG.info("Stopping network processor...");
        networkProcessor.running = false;
        try {
            networkProcessorThread.join(5000);
            LOG.info("Successfully waited for network processor to terminate");
        } catch (final InterruptedException e) {
            LOG.warn("Interrupted when waiting for network processor thread to finish...");
            Thread.currentThread().interrupt();
        }
    }


    @EventSubscribe
    public void onNewNetworkEvent(final NetworkEvent networkEvent) {
        eventQueue.add(networkEvent);
    }

    private class NetworkProcessor implements Runnable {
        boolean running;

        @Override
        public void run() {
            try (final DatagramSocket datagramSocket = new DatagramSocket()) {
                while (running) {
                    final NetworkEvent nextEvent = eventQueue.poll();
                    if (nextEvent != null) {
                        nextEvent.handle(datagramSocket);
                    }
                }
            } catch (final Exception e) {
                LOG.error("Exception in network client", e);
            }
        }
    }
}
