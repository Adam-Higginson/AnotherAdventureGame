package com.adam.adventure.entity.component.network;

import com.adam.adventure.domain.EntityInfo;
import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.EntityComponent;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Each {@link Entity} in the system is given an identity when registered on a network. Each update event
 * from the server can then refer to that entity to process events. This component handles all network updates
 * and will delegate them to all other components attached to this entity.
 */
class NetworkIdentityComponent extends EntityComponent {

    private final UUID id;
    private final OutputMessageQueue outputMessageQueue;

    private List<NetworkComponent> networkComponents;

    NetworkIdentityComponent(final UUID id,
                             final OutputMessageQueue outputMessageQueue) {
        this.id = id;
        this.outputMessageQueue = outputMessageQueue;
    }

    @Override
    protected void activate() {
        this.networkComponents = getAllComponents().stream()
                .filter(component -> NetworkComponent.class.isAssignableFrom(component.getClass()))
                .map(component -> (NetworkComponent) component)
                .collect(Collectors.toList());

    }

    //Called by network manager when a new entity info is received
    void processNetworkUpdates(final EntityInfo entityInfo) {
        networkComponents.forEach(networkComponent -> networkComponent.receiveNetworkUpdates(entityInfo));
    }

    @Override
    protected void update(final float deltaTime) {
        //Here we could check that the update time hasn't been exceeded...
        //Hmmm, should we group together here?
        networkComponents.forEach(networkComponent -> networkComponent.writeNetworkUpdates(id, outputMessageQueue));
    }


    @Override
    protected void onNewComponentAdded(final EntityComponent component) {
        if (component instanceof NetworkComponent) {
            networkComponents.add((NetworkComponent) component);
        }
    }


    public UUID getId() {
        return id;
    }
}
