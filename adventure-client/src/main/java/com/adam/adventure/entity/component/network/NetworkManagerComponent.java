package com.adam.adventure.entity.component.network;

import com.adam.adventure.domain.EntityInfo;
import com.adam.adventure.domain.WorldState;
import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.EntityComponent;
import com.adam.adventure.event.*;
import com.adam.adventure.lib.flatbuffer.schema.converter.PacketConverter;
import com.adam.adventure.lib.flatbuffer.schema.packet.LoginSuccessfulPacket;
import com.adam.adventure.lib.flatbuffer.schema.packet.Packet;
import com.adam.adventure.lib.flatbuffer.schema.packet.PacketType;
import com.adam.adventure.lib.flatbuffer.schema.packet.WorldStatePacket;
import com.adam.adventure.scene.NewSceneEvent;
import com.adam.adventure.scene.SceneManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class NetworkManagerComponent extends EntityComponent {
    private static final int SOCKET_TIMEOUT_MILLIS = 200;
    private static final Logger LOG = LoggerFactory.getLogger(NetworkManagerComponent.class);

    @Inject
    private EventBus eventBus;
    @Inject
    private SceneManager sceneManager;
    @Inject
    private PacketConverter packetConverter;

    private final Supplier<Entity> playerEntitySupplier;
    private final Supplier<Entity> otherPlayerEntitySupplier;
    private DatagramSocket datagramSocket;
    private InetAddress serverAddress;
    private int serverPort;
    private EntityInfo playerEntityInfo;

    private volatile boolean shouldReceivePackets;
    private WorldState activeWorldState;
    private volatile WorldState latestWorldState;

    private boolean awaitingPlayerSpawn;
    private Thread receiveThread;


    /**
     * @param playerEntitySupplier What entity to spawn when successfully logged into server.
     */
    public NetworkManagerComponent(final Supplier<Entity> playerEntitySupplier, final Supplier<Entity> otherPlayerEntitySupplier) {
        this.playerEntitySupplier = playerEntitySupplier;
        this.otherPlayerEntitySupplier = otherPlayerEntitySupplier;
    }

    @Override
    protected void activate() {
        try {
            datagramSocket = new DatagramSocket();
        } catch (final SocketException e) {
            LOG.error("Exception when attempting to create socket!", e);
            eventBus.publishEvent(new WriteUiConsoleErrorEvent("Could not activate network manager!"));
        }

        awaitingPlayerSpawn = true;
        eventBus.register(this);
    }

    @Override
    protected void destroy() {
        LOG.info("Destroying network manager...");
        shouldReceivePackets = false;
        awaitingPlayerSpawn = false;
        if (receiveThread != null) {
            try {
                receiveThread.join(1000L);
            } catch (InterruptedException e) {
                LOG.warn("Interrupted when waiting for receive thread to stop...", e);
                Thread.currentThread().interrupt();
            }
        }

        if (datagramSocket != null) {
            datagramSocket.close();
        }
        LOG.info("Network manager stopped");
    }


    @Override
    protected void update(final float deltaTime) {
        if (activeWorldState == null && latestWorldState != null) {
            //For now only publish initial scene transition
            eventBus.publishEvent(new NewSceneEvent(latestWorldState.getSceneInfo().getSceneName()));
        }

        checkForNewPlayers();
        activeWorldState = latestWorldState;
    }


    @EventSubscribe
    @SuppressWarnings("unused")
    public void onConnect(final RequestConnectionToServerEvent requestConnectionToServerEvent) {
        this.serverPort = requestConnectionToServerEvent.getPort();
        try {
            this.serverAddress = InetAddress.getByName(requestConnectionToServerEvent.getAddressToConnectTo());
            login(requestConnectionToServerEvent.getUsername());
        } catch (final Exception e) {
            eventBus.publishEvent(new WriteUiConsoleErrorEvent("Could not connect to address: " + serverAddress + " with port: " + serverPort));
            LOG.warn("Exception on connect", e);
        }
    }

    @EventSubscribe
    @SuppressWarnings("unused")
    public void onSceneActivated(final SceneActivatedEvent sceneActivatedEvent) {
        if (activeWorldState == null) {
            //We've not set the scene
            return;
        }

        if (awaitingPlayerSpawn) {
            final Optional<EntityInfo> currentPlayer = activeWorldState
                    .getPlayerEntities()
                    .stream()
                    .filter(player -> player.getId().equals(playerEntityInfo.getId()))
                    .findAny();

            if (currentPlayer.isEmpty()) {
                LOG.warn("World state did not return our player id: {}!", playerEntityInfo.getId());
                return;
            }

            final Entity player = playerEntitySupplier.get();
            player.setTransform(currentPlayer.get().getTransform());
            sceneManager.getCurrentScene().addEntity(player);
            awaitingPlayerSpawn = false;
        }
    }


    private void login(final String username) throws IOException {
        sendLoginPacket(username);
        awaitLoginSuccessfulPacket();
        sendClientReadyPacket();
        receivePacketsInBackground();
        LOG.info("Successfully logged in, received player entity info: {}", playerEntityInfo);
    }

    private void sendLoginPacket(final String username) throws IOException {
        LOG.info("Logging into server with address: {}, port: {}, for username: {}", serverAddress, serverPort, username);
        final byte[] loginPacket = packetConverter.buildLoginPacket(username);
        final DatagramPacket packet = new DatagramPacket(loginPacket, loginPacket.length, serverAddress, serverPort);
        datagramSocket.send(packet);
    }

    private void awaitLoginSuccessfulPacket() throws IOException {
        final byte[] buffer = new byte[256];
        final DatagramPacket incomingPacket = new DatagramPacket(buffer, buffer.length);
        datagramSocket.setSoTimeout(SOCKET_TIMEOUT_MILLIS);
        datagramSocket.receive(incomingPacket);

        final LoginSuccessfulPacket loginSuccessfulPacket = packetConverter.getLoginSuccessfulPacket(buffer, incomingPacket.getOffset(), incomingPacket.getLength());
        playerEntityInfo = packetConverter.fromPacketEntityInfo(loginSuccessfulPacket.playerEntity());
        LOG.info("Successfully received login successful packet");
    }

    /**
     * Tells the server we are now ready to start receive world state updates.
     */
    private void sendClientReadyPacket() throws IOException {
        LOG.info("Sending client ready packet");
        final byte[] clientReadyPacket = packetConverter.buildClientReadyPacket(playerEntityInfo);
        final DatagramPacket packet = new DatagramPacket(clientReadyPacket, clientReadyPacket.length, serverAddress, serverPort);
        datagramSocket.send(packet);
    }

    private void receivePacketsInBackground() {
        shouldReceivePackets = true;
        receiveThread = new Thread(new ReceiveRunnable());
        receiveThread.start();
    }

    private void checkForNewPlayers() {
        if (latestWorldState == null) {
            return;
        }

        final Set<UUID> currentPlayerIds;
        if (activeWorldState == null) {
            currentPlayerIds = new HashSet<>();
        } else {
            currentPlayerIds = activeWorldState.getPlayerEntities()
                    .stream()
                    .map(EntityInfo::getId)
                    .collect(Collectors.toSet());
        }

        latestWorldState.getPlayerEntities()
                .stream()
                .filter(player -> player.getId() != playerEntityInfo.getId())
                .filter(player -> !currentPlayerIds.contains(player.getId()))
                .forEach(newPlayer -> {
                    LOG.info("New player joined: {} with username: {}", newPlayer.getAttributes().get("username"), newPlayer.getId());
                    eventBus.publishEvent(new WriteUiConsoleInfoEvent(newPlayer.getAttributes().get("username") + " has joined."));
                    final Entity player = otherPlayerEntitySupplier.get();
                    player.setTransform(newPlayer.getTransform());
                    sceneManager.getCurrentScene().addEntity(player);
                });
    }


    private class ReceiveRunnable implements Runnable {
        @Override
        public void run() {
            final byte[] buffer = new byte[1024];
            while (shouldReceivePackets) {
                try {
                    final DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                    datagramSocket.receive(datagramPacket);

                    final ByteBuffer packetBuffer = ByteBuffer.wrap(datagramPacket.getData(), datagramPacket.getOffset(), datagramPacket.getLength());
                    final Packet packet = Packet.getRootAsPacket(packetBuffer);
                    switch (packet.packetType()) {
                        case PacketType.WorldStatePacket:
                            latestWorldState = packetConverter.fromPacket((WorldStatePacket) packet.packet(new WorldStatePacket()));
                            break;
                        default:
                            LOG.warn("Received unhandled packet type: {}", packet.packetType());
                            break;
                    }

                } catch (final Exception e) {
                    LOG.error("Exception thrown when receiving packet", e);
                }
            }

            LOG.info("Network receive thread stopped");
        }
    }

}
