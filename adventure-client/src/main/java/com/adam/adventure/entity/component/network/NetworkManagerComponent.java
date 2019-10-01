package com.adam.adventure.entity.component.network;

import com.adam.adventure.domain.EntityInfo;
import com.adam.adventure.domain.WorldState;
import com.adam.adventure.domain.message.PacketableMessage;
import com.adam.adventure.domain.message.ServerCommandPacketableMessage;
import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.EntityComponent;
import com.adam.adventure.entity.component.network.event.ServerCommandEvent;
import com.adam.adventure.entity.repository.EntityRepository;
import com.adam.adventure.event.*;
import com.adam.adventure.lib.flatbuffer.schema.converter.PacketConverter;
import com.adam.adventure.lib.flatbuffer.schema.packet.*;
import com.adam.adventure.scene.NewSceneEvent;
import com.adam.adventure.scene.SceneManager;
import com.google.flatbuffers.FlatBufferBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static com.adam.adventure.event.WriteUiConsoleErrorEvent.consoleErrorEvent;

public class NetworkManagerComponent extends EntityComponent {
    private static final int SOCKET_TIMEOUT_MILLIS = 5000;
    private static final Logger LOG = LoggerFactory.getLogger(NetworkManagerComponent.class);

    @Inject
    private EventBus eventBus;
    @Inject
    private SceneManager sceneManager;
    @Inject
    private PacketConverter packetConverter;
    @Inject
    private PacketTracker packetTracker;
    @Inject
    private EntityRepository entityRepository;

    private final Supplier<Entity> playerEntitySupplier;
    private final Supplier<Entity> otherPlayerEntitySupplier;
    private final Map<UUID, NetworkIdentityComponent> idToNetworkIdentities;
    private final OutputMessageQueue outputMessageQueue;
    private final AtomicBoolean isErrorInConnection = new AtomicBoolean();


    private DatagramSocket datagramSocket;
    private InetAddress serverAddress;
    private int serverPort;
    private EntityInfo playerEntityInfo;
    private volatile boolean shouldReceivePackets;
    private WorldState activeWorldState;
    private volatile WorldState latestWorldState;
    private Thread receiveThread;
    private long serverTickrate;


    /**
     * @param playerEntitySupplier What entity to spawn when successfully logged into server.
     */
    public NetworkManagerComponent(final Supplier<Entity> playerEntitySupplier,
                                   final Supplier<Entity> otherPlayerEntitySupplier) {
        this.playerEntitySupplier = playerEntitySupplier;
        this.otherPlayerEntitySupplier = otherPlayerEntitySupplier;
        this.idToNetworkIdentities = new HashMap<>();
        this.outputMessageQueue = new OutputMessageQueue();
    }

    @Override
    protected void activate() {
        try {
            datagramSocket = new DatagramSocket();
            datagramSocket.setSoTimeout(SOCKET_TIMEOUT_MILLIS);
        } catch (final SocketException e) {
            LOG.error("Exception when attempting to create socket!", e);
            eventBus.publishEvent(new WriteUiConsoleErrorEvent("Could not activate network manager!"));
        }

        eventBus.register(this);
    }

    @Override
    protected void beforeUpdate(final float deltaTime) {
        if (isErrorInConnection.get()) {
            eventBus.publishEvent(consoleErrorEvent("Server timed out"));
            eventBus.publishEvent(new NewSceneEvent("TitleScene"));
        }
    }

    @Override
    protected void afterUpdate(final float deltaTime) {
        if (activeWorldState == null && latestWorldState != null) {
            //For now only publish initial scene transition
            eventBus.publishEvent(new NewSceneEvent(latestWorldState.getSceneInfo().getSceneName()));
        }

        activeWorldState = latestWorldState;
        if (activeWorldState != null) {
            processUpdatedEntities();
        }

        drainOutputMessageQueue();
    }


    @Override
    protected void destroy() {
        LOG.info("Destroying network manager...");
        shouldReceivePackets = false;
        if (receiveThread != null) {
            try {
                receiveThread.join(1000L);
            } catch (final InterruptedException e) {
                LOG.warn("Interrupted when waiting for receive thread to stop...", e);
                Thread.currentThread().interrupt();
            }
        }

        if (datagramSocket != null) {
            datagramSocket.close();
        }

        this.idToNetworkIdentities.clear();
        LOG.info("Network manager stopped");
    }


    private void drainOutputMessageQueue() {
        final List<PacketableMessage<?>> messagesToSend = outputMessageQueue.drain();
        if (messagesToSend.isEmpty()) {
            return;
        }

        final byte[] dataToSend = buildPacketBatchFromMessages(messagesToSend);
        final DatagramPacket datagramPacket = new DatagramPacket(dataToSend, dataToSend.length, serverAddress, serverPort);
        try {
            datagramSocket.send(datagramPacket);
        } catch (final IOException e) {
            LOG.error("Exception when writing packet to server!", e);
        }
    }

    private byte[] buildPacketBatchFromMessages(final List<PacketableMessage<?>> messagesToSend) {
        final FlatBufferBuilder builder = new FlatBufferBuilder();
        final long timestamp = System.currentTimeMillis();
        final int[] packetLocations = messagesToSend
                .stream()
                .mapToInt(message -> message.serialise(builder, packetConverter, packetTracker.getNextPacketId(), timestamp))
                .toArray();

        return buildPacketBatch(builder, packetLocations);
    }

    private byte[] buildPacketBatch(final FlatBufferBuilder builder, final int[] packetLocations) {
        final int packetsVectorLocation = PacketBatch.createPacketsVector(builder, packetLocations);
        PacketBatch.startPacketBatch(builder);
        PacketBatch.addPackets(builder, packetsVectorLocation);
        final int packetBatchId = PacketBatch.endPacketBatch(builder);
        builder.finish(packetBatchId);
        return builder.sizedByteArray();
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
    public void onServerCommandEvent(final ServerCommandEvent serverCommandEvent) {
        outputMessageQueue.add(new ServerCommandPacketableMessage(serverCommandEvent.getServerCommand()));
    }


    private void login(final String username) throws IOException {
        sendLoginPacket(username);
        awaitLoginSuccessfulPacket();
        receivePacketsInBackground();
        sendClientReadyPacket();
        LOG.info("Successfully logged in, received player entity info: {}", playerEntityInfo);
    }

    private void sendLoginPacket(final String username) throws IOException {
        LOG.info("Logging into server with address: {}, port: {}, for username: {}", serverAddress, serverPort, username);
        final FlatBufferBuilder builder = new FlatBufferBuilder();
        final long timestamp = System.currentTimeMillis();
        final int loginPacketLocation = packetConverter.buildLoginPacket(builder, username, packetTracker.getNextPacketId(), timestamp);
        final byte[] loginPacket = buildPacketBatch(builder, new int[]{loginPacketLocation});

        final DatagramPacket packet = new DatagramPacket(loginPacket, loginPacket.length, serverAddress, serverPort);
        datagramSocket.send(packet);
    }

    private void awaitLoginSuccessfulPacket() throws IOException {
        final byte[] buffer = new byte[1024];
        final DatagramPacket incomingPacket = new DatagramPacket(buffer, buffer.length);
        datagramSocket.receive(incomingPacket);

        final LoginSuccessfulPacket loginSuccessfulPacket = packetConverter.getLoginSuccessfulPacket(buffer, incomingPacket.getOffset(), incomingPacket.getLength());
        playerEntityInfo = packetConverter.fromPacketEntityInfo(loginSuccessfulPacket.playerEntity());
        serverTickrate = loginSuccessfulPacket.tickrate();
        LOG.info("Successfully received login successful packet, server tickrate: {}", serverTickrate);
    }

    /**
     * Tells the server we are now ready to start receive world state updates.
     */
    private void sendClientReadyPacket() throws IOException {
        LOG.info("Sending client ready packet");
        final FlatBufferBuilder builder = new FlatBufferBuilder();
        final long timestamp = System.currentTimeMillis();
        final int clientReadyPacketLocation = packetConverter.buildClientReadyPacket(builder, playerEntityInfo, packetTracker.getNextPacketId(), timestamp);
        final byte[] clientReadyPacket = buildPacketBatch(builder, new int[]{clientReadyPacketLocation});
        final DatagramPacket packet = new DatagramPacket(clientReadyPacket, clientReadyPacket.length, serverAddress, serverPort);
        datagramSocket.send(packet);
    }

    private void receivePacketsInBackground() {
        shouldReceivePackets = true;
        receiveThread = new Thread(new ReceiveRunnable());
        receiveThread.start();
    }


    private void processUpdatedEntities() {
        //For now assume all entities in world state have been updated
        activeWorldState.getSceneInfo()
                .getEntities()
                .forEach(entityInfo -> {
                    final NetworkIdentityComponent existingIdentity = idToNetworkIdentities.get(entityInfo.getId());
                    if (existingIdentity == null) {
                        onNewEntityFromServer(entityInfo);
                    } else {
                        existingIdentity.processNetworkUpdates(entityInfo, serverTickrate);
                    }
                });
    }


    private void onNewEntityFromServer(final EntityInfo entityInfo) {
        if (entityInfo.getType() == EntityInfo.EntityType.PLAYER) {
            addNewPlayer(entityInfo);
        } else {
            addNewEntity(entityInfo);
        }
    }

    private void addNewPlayer(final EntityInfo playerEntityInfo) {
        LOG.info("New player joined: {} with username: {}", playerEntityInfo.getAttributes().get("username"), playerEntityInfo.getId());
        eventBus.publishEvent(new WriteUiConsoleInfoEvent(playerEntityInfo.getAttributes().get("username") + " has joined."));

        final Entity player = buildNewPlayerEntity(playerEntityInfo);
        sceneManager.getCurrentScene().ifPresent(scene -> scene.addEntity(player));
    }

    private Entity buildNewPlayerEntity(final EntityInfo newPlayerEntityInfo) {
        final Entity player;
        if (newPlayerEntityInfo.getId().equals(playerEntityInfo.getId())) {
            LOG.info("Spawning player");
            player = playerEntitySupplier.get();
        } else {
            LOG.info("Spawning new player with id: {}", newPlayerEntityInfo.getId());
            player = otherPlayerEntitySupplier.get();
        }

        final NetworkIdentityComponent networkIdentityComponent
                = new NetworkIdentityComponent(newPlayerEntityInfo.getId(), outputMessageQueue);
        idToNetworkIdentities.put(newPlayerEntityInfo.getId(), networkIdentityComponent);
        player.addComponent(networkIdentityComponent);
        return player;
    }

    private void addNewEntity(final EntityInfo entityInfo) {
        eventBus.publishEvent(new WriteUiConsoleInfoEvent("Entity: " + entityInfo.getName() + " spawned."));
        final Entity entity = buildNewEntity(entityInfo);
        sceneManager.getCurrentScene().ifPresent(scene -> scene.addEntity(entity));
    }

    private Entity buildNewEntity(final EntityInfo entityInfo) {
        final Entity entity = entityRepository.buildEntityForName(entityInfo.getName())
                .orElseThrow(() -> new IllegalStateException("Could not create entity: " + entityInfo.getName()
                        + "! Perhaps the server and client versions differ?"));
        final NetworkIdentityComponent networkIdentityComponent = new NetworkIdentityComponent(entityInfo.getId(), outputMessageQueue);
        idToNetworkIdentities.put(entityInfo.getId(), networkIdentityComponent);

        return entity
                .addComponent(new NetworkTransformComponent(false))
                .addComponent(networkIdentityComponent);
    }


    private class ReceiveRunnable implements Runnable {


        @Override
        public void run() {
            final byte[] buffer = new byte[2048];
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

                } catch (final SocketTimeoutException e) {
                    LOG.error("Timeout when waiting for packets", e);
                    shouldReceivePackets = false;
                    isErrorInConnection.set(true);
                }
                catch (final Exception e) {
                    LOG.error("Exception thrown when receiving packet", e);
                }
            }

            LOG.info("Network receive thread stopped");
        }
    }
}
