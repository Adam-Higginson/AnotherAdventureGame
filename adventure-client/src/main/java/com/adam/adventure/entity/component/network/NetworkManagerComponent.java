package com.adam.adventure.entity.component.network;

import com.adam.adventure.client.event.LoginToServerEvent;
import com.adam.adventure.domain.WorldState;
import com.adam.adventure.entity.EntityComponent;
import com.adam.adventure.event.RequestConnectionToServerEvent;
import com.adam.adventure.event.EventBus;
import com.adam.adventure.event.EventSubscribe;
import com.adam.adventure.event.WriteUiConsoleErrorEvent;
import com.adam.adventure.scene.event.NewSceneEvent;
import com.adam.adventure.scene.event.SceneTransitionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.net.InetAddress;

public class NetworkManagerComponent extends EntityComponent {

    private static final Logger LOG = LoggerFactory.getLogger(NetworkManagerComponent.class);

    @Inject
    private EventBus eventBus;

    private NetworkProcessor networkProcessor;

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
            final WorldState initialWorldState = this.networkProcessor.getInitialWorldState();
            eventBus.publishEvent(new NewSceneEvent(initialWorldState.getSceneInfo().getSceneName()));
        } catch (final Exception e) {
            eventBus.publishEvent(new WriteUiConsoleErrorEvent("Could not connect to address: " + addressToConnectTo + " with port: " + port));
            LOG.warn("Exception on connect", e);
        }
    }


}
