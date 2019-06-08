package com.adam.adventure.entity.component.network;

import com.adam.adventure.domain.EntityInfo;
import com.adam.adventure.entity.EntityComponent;

import java.util.UUID;

public abstract class NetworkComponent extends EntityComponent {

    protected abstract void writeNetworkUpdates(final UUID entityId, final OutputMessageQueue outputMessageQueue);

    protected abstract void receiveNetworkUpdates(final EntityInfo entityInfo, long serverTickrate);
}
