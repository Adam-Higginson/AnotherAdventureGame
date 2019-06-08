package com.adam.adventure.server.receiver.processor;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.EntityFactory;
import com.adam.adventure.lib.flatbuffer.schema.packet.Packet;
import com.adam.adventure.lib.flatbuffer.schema.packet.ServerCommandPacket;
import com.adam.adventure.scene.Scene;
import com.adam.adventure.scene.SceneManager;
import com.adam.adventure.server.entity.component.NetworkIdComponent;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.net.DatagramPacket;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

@Slf4j
public class ServerCommandPacketProcessor implements BiConsumer<DatagramPacket, Packet> {

    private final EntityFactory entityFactory;
    private final SceneManager sceneManager;

    @Inject
    public ServerCommandPacketProcessor(final EntityFactory entityFactory, final SceneManager sceneManager) {
        this.entityFactory = entityFactory;
        this.sceneManager = sceneManager;
    }

    @Override
    public void accept(final DatagramPacket datagramPacket, final Packet packet) {
        final ServerCommandPacket serverCommandPacket = (ServerCommandPacket) packet.packet(new ServerCommandPacket());
        final String command = serverCommandPacket.command();

        if (command == null) {
            LOG.warn("Received null command so ignoring");
            return;
        }

        final String[] commandParts = command.split(" ");
        if (commandParts.length < 2) {
            LOG.warn("Received command: {} but this was less than minimum split of 2 parts", command);
        }

        switch (commandParts[0].toLowerCase()) {
            case "spawn":
                handleSpawnEvent(commandParts);
                break;
        }

        LOG.info("Server processing command: {}", serverCommandPacket.command());
    }

    private void handleSpawnEvent(final String[] command) {
        final Optional<Scene> currentScene = sceneManager.getCurrentScene();
        if (currentScene.isEmpty()) {
            LOG.warn("Attempted spawn command but no current active scene! Doing nothing.");
            return;
        }

        final String entityName = command[1];
        LOG.info("Spawning: {}", entityName);

        // Need some kind of entity dictionary here
        final Entity entity = entityFactory.create(entityName)
                .addComponent(new NetworkIdComponent(UUID.randomUUID()));
        currentScene.get().addEntity(entity);
    }
}
