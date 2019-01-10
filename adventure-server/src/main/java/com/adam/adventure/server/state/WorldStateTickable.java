package com.adam.adventure.server.state;

import com.adam.adventure.domain.SceneInfo;
import com.adam.adventure.event.EventBus;
import com.adam.adventure.server.player.PlayerSessionRegistry;
import com.adam.adventure.server.tick.OutputMessage;
import com.adam.adventure.server.tick.OutputMessageQueue;
import com.adam.adventure.server.tick.Tickable;

import javax.inject.Inject;
import java.net.DatagramSocket;
import java.util.ArrayList;


public class WorldStateTickable implements Tickable {

    private final WorldState worldState;
    private final PlayerSessionRegistry playerSessionRegistry;

    @Inject
    public WorldStateTickable(final EventBus eventBus, final PlayerSessionRegistry playerSessionRegistry) {
        this.worldState = WorldState.builder()
                .sceneInfo(new SceneInfo("Test Scene"))
                .players(new ArrayList<>())
                .build();
        this.playerSessionRegistry = playerSessionRegistry;
        eventBus.register(this);
    }

    public WorldState getWorldState() {
        return worldState;
    }

    @Override
    public void tick(final OutputMessageQueue outputMessageQueue) {
        playerSessionRegistry.forEachActivePlayerSession(playerSession -> {
            outputMessageQueue.addOutputMessage(socket -> {

            });
        });
    }


    private class WorldStateUpdateMessage implements OutputMessage {
        @Override
        public void write(DatagramSocket datagramSocket) {

        }
    }
}
