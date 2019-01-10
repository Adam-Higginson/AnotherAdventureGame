package com.adam.adventure.entity.component.network;

import com.adam.adventure.domain.WorldState;
import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.EntityComponent;
import com.adam.adventure.event.*;
import com.adam.adventure.scene.SceneManager;
import com.adam.adventure.scene.NewSceneEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.net.InetAddress;

public class NetworkManagerComponent extends EntityComponent {

    private static final Logger LOG = LoggerFactory.getLogger(NetworkManagerComponent.class);

    @Inject
    private EventBus eventBus;
    @Inject
    private SceneManager sceneManager;

    //Created when connected
    private NetworkProcessor networkProcessor;
    private final Entity playerEntity;
    private WorldState initialWorldState;
    private boolean awaitingPlayerSpawn;


    /**
     * @param playerEntity What entity to spawn when successfully logged into server.
     */
    public NetworkManagerComponent(final Entity playerEntity) {
        this.playerEntity = playerEntity;
        this.awaitingPlayerSpawn = true;
    }

    @Override
    protected void activate() {
        eventBus.register(this);
    }

    @EventSubscribe
    public void onConnect(final RequestConnectionToServerEvent requestConnectionToServerEvent) {
        final String addressToConnectTo = requestConnectionToServerEvent.getAddressToConnectTo();
        int port = requestConnectionToServerEvent.getPort();
        try {
            final InetAddress address = InetAddress.getByName(requestConnectionToServerEvent.getAddressToConnectTo());
            this.networkProcessor = NetworkProcessor.login("Test-User", address, port);
            this.initialWorldState = this.networkProcessor.getInitialWorldState();
            eventBus.publishEvent(new NewSceneEvent(initialWorldState.getSceneInfo().getSceneName()));
        } catch (final Exception e) {
            eventBus.publishEvent(new WriteUiConsoleErrorEvent("Could not connect to address: " + addressToConnectTo + " with port: " + port));
            LOG.warn("Exception on connect", e);
        }
    }


    @EventSubscribe
    public void onSceneActivated(final SceneActivatedEvent sceneActivatedEvent) {
        if (initialWorldState == null) {
            return;
        }

        String newSceneName = sceneActivatedEvent.getSceneName();
        String sceneName = initialWorldState.getSceneInfo().getSceneName();
        if (newSceneName.equals(sceneName) && awaitingPlayerSpawn) {
            //playerEntity.setTransform(initialWorldState.getCurrentPlayer().getTransform());
            sceneManager.getCurrentScene().addEntity(playerEntity);
            awaitingPlayerSpawn = false;
        }
    }

}
