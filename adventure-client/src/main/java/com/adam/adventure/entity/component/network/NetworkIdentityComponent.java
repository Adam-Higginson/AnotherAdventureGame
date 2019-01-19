package com.adam.adventure.entity.component.network;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.EntityComponent;

import java.util.UUID;

/**
 * Each {@link Entity} in the system is given an identity when registered on a network. Each update event
 * from the server can then refer to that entity to process events. This component handles all network updates
 * and will delegate them to all other components attached to this entity.
 */
public class NetworkIdentityComponent extends EntityComponent {

    private final UUID id;


    public NetworkIdentityComponent(final UUID id) {
        this.id = id;
    }
}
