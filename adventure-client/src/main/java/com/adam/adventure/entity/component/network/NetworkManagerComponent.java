package com.adam.adventure.entity.component.network;

import com.adam.adventure.domain.PlayerInfo;
import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.EntityComponent;
import com.adam.adventure.event.*;
import com.adam.adventure.lib.flatbuffer.schema.converter.PacketConverter;
import com.adam.adventure.lib.flatbuffer.schema.packet.LoginSuccessfulPacket;
import com.adam.adventure.scene.SceneManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class NetworkManagerComponent extends EntityComponent {
    private static final int SOCKET_TIMEOUT_MILLIS = 200;
    private static final Logger LOG = LoggerFactory.getLogger(NetworkManagerComponent.class);

    @Inject
    private EventBus eventBus;
    @Inject
    private SceneManager sceneManager;
    @Inject
    private PacketConverter packetConverter;

    private final Entity playerEntity;
    private DatagramSocket datagramSocket;
    private InetAddress serverAddress;
    private int serverPort;

    private PlayerInfo playerInfo;

    private final boolean awaitingPlayerSpawn;


    /**
     * @param playerEntity What entity to spawn when successfully logged into server.
     */
    public NetworkManagerComponent(final Entity playerEntity) {
        this.playerEntity = playerEntity;
        this.awaitingPlayerSpawn = true;
    }

    @Override
    protected void activate() {
        try {
            datagramSocket = new DatagramSocket();
        } catch (final SocketException e) {
            LOG.error("Exception when attempting to create socket!", e);
            eventBus.publishEvent(new WriteUiConsoleErrorEvent("Could not activate network manager!"));
        }

        eventBus.register(this);
    }

    @EventSubscribe
    public void onConnect(final RequestConnectionToServerEvent requestConnectionToServerEvent) {
        final String addressToConnectTo = requestConnectionToServerEvent.getAddressToConnectTo();
        this.serverPort = requestConnectionToServerEvent.getPort();
        try {
            this.serverAddress = InetAddress.getByName(requestConnectionToServerEvent.getAddressToConnectTo());
            login("Adam");
            //eventBus.publishEvent(new NewSceneEvent(initialWorldState.getSceneInfo().getSceneName()));
        } catch (final Exception e) {
            eventBus.publishEvent(new WriteUiConsoleErrorEvent("Could not connect to address: " + serverAddress + " with port: " + serverPort));
            LOG.warn("Exception on connect", e);
        }
    }


    private void login(final String username) throws IOException {
        LOG.info("Logging into server with address: {}, port: {}, for username: {}", serverAddress, serverPort, username);
        final byte[] loginPacket = packetConverter.buildLoginPacket(username);
        final DatagramPacket packet = new DatagramPacket(loginPacket, loginPacket.length, serverAddress, serverPort);
        datagramSocket.send(packet);

        awaitLoginSuccessfulPacket();
        LOG.info("Successfully logged in, received player info: {}", playerInfo);
    }

    private void awaitLoginSuccessfulPacket() throws IOException {
        final byte[] buffer = new byte[256];
        final DatagramPacket incomingPacket = new DatagramPacket(buffer, buffer.length);
        datagramSocket.setSoTimeout(SOCKET_TIMEOUT_MILLIS);
        datagramSocket.receive(incomingPacket);

        final LoginSuccessfulPacket loginSuccessfulPacket = packetConverter.getLoginSuccessfulPacket(buffer, incomingPacket.getOffset(), incomingPacket.getLength());
        this.playerInfo = packetConverter.fromPacketPlayerInfo(loginSuccessfulPacket.player());
    }


    @EventSubscribe
    public void onSceneActivated(final SceneActivatedEvent sceneActivatedEvent) {
//        if (initialWorldState == null) {
//            return;
//        }
//
//        final String newSceneName = sceneActivatedEvent.getSceneName();
//        final String sceneName = initialWorldState.getSceneInfo().getSceneName();
//        if (newSceneName.equals(sceneName) && awaitingPlayerSpawn) {
//
//            //playerEntity.setTransform(initialWorldState.getCurrentPlayer().getTransform());
//            sceneManager.getCurrentScene().addEntity(playerEntity);
//            awaitingPlayerSpawn = false;
//        }
    }

}
