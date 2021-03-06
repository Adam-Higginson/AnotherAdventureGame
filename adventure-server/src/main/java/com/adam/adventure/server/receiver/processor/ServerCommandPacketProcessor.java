package com.adam.adventure.server.receiver.processor;

import com.adam.adventure.entity.component.tilemap.TilemapComponent;
import com.adam.adventure.entity.repository.EntityRepository;
import com.adam.adventure.lib.flatbuffer.schema.packet.Packet;
import com.adam.adventure.lib.flatbuffer.schema.packet.ServerCommandPacket;
import com.adam.adventure.scene.Scene;
import com.adam.adventure.scene.SceneManager;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.net.DatagramPacket;
import java.util.Optional;
import java.util.function.BiConsumer;

@Slf4j
public class ServerCommandPacketProcessor implements BiConsumer<DatagramPacket, Packet> {

    private final EntityRepository entityRepository;
    private final SceneManager sceneManager;

    @Inject
    public ServerCommandPacketProcessor(final EntityRepository entityRepository,
                                        final SceneManager sceneManager) {
        this.entityRepository = entityRepository;
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
        if (commandParts.length < 1) {
            LOG.warn("Received command: {} but this was less than minimum split of 1 part", command);
        }

        LOG.info("Server processing command: {}", serverCommandPacket.command());

        switch (commandParts[0].toLowerCase()) {
            case "spawn":
                handleSpawnEvent(commandParts);
                break;
            case "tiledump":
                dumpTileMapInfo();
                break;
        }
    }


    private void handleSpawnEvent(final String[] command) {
        final Optional<Scene> currentScene = sceneManager.getCurrentScene();
        if (currentScene.isEmpty()) {
            LOG.warn("Attempted spawn command but no current active scene! Doing nothing.");
            return;
        }

        final String entityName = command[1];
        entityRepository.buildEntityForName(entityName)
                .ifPresentOrElse(entity -> {
                            LOG.info("Spawning: {}", entityName);
                            currentScene.get().addEntity(entity);
                        },
                        () -> LOG.warn("Entity: {} could not be loaded!", entityName));
    }

    private void dumpTileMapInfo() {
        final Optional<Scene> currentScene = sceneManager.getCurrentScene();
        if (currentScene.isEmpty()) {
            LOG.warn("Attempted to dump tilemap info, but no active scene!");
            return;
        }

        final Optional<TilemapComponent> tilemapComponent = currentScene.get()
                .getEntities()
                .stream()
                .map(entity -> entity.getComponent(TilemapComponent.class))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
        if (tilemapComponent.isEmpty()) {
            LOG.warn("Attempted to dump tilemap info, but no active tilemap!");
            return;
        }

        tilemapComponent.get().dumpTileMapInfo();
    }
}
