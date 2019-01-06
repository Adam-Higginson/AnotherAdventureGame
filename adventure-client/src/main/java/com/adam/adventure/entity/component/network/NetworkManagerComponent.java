package com.adam.adventure.entity.component.network;

import com.adam.adventure.client.event.LoginToServerEvent;
import com.adam.adventure.entity.EntityComponent;
import com.adam.adventure.event.ConnectionRequestEvent;
import com.adam.adventure.event.EventBus;
import com.adam.adventure.event.EventSubscribe;
import com.adam.adventure.event.WriteUiConsoleErrorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkManagerComponent extends EntityComponent {

    private static final Logger LOG = LoggerFactory.getLogger(NetworkManagerComponent.class);

    @Inject
    private EventBus eventBus;

    @Override
    protected void activate() {
        eventBus.register(this);
    }

    @EventSubscribe
    public void onConnect(final ConnectionRequestEvent connectionRequestEvent) {
        final String addressToConnectTo = connectionRequestEvent.getAddressToConnectTo();
        try {
            final InetAddress address = InetAddress.getByName(addressToConnectTo);
            final int port = 4445;
            eventBus.publishEvent(new LoginToServerEvent("Test-User", address, port));
        } catch (final UnknownHostException e) {
            eventBus.publishEvent(new WriteUiConsoleErrorEvent("Could not connect to address: " + addressToConnectTo));
            LOG.warn("UnknownHostException on connect", e);
        }
    }
}
