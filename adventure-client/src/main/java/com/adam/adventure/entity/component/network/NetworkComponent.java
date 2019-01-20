package com.adam.adventure.entity.component.network;

import com.adam.adventure.domain.EntityInfo;
import com.adam.adventure.entity.EntityComponent;

import java.util.UUID;

public abstract class NetworkComponent extends EntityComponent {

    @Override
    protected final void update(final float deltaTime) {
        //Network components receive their update requests form the parent NetworkIdentityComponent.
        //This allows for the update times to be delayed rather than every frame.
    }

    protected abstract void writeUpdates(final UUID entityId, final OutputMessageQueue outputMessageQueue);

    protected abstract void receiveUpdates(final EntityInfo entityInfo);
}
