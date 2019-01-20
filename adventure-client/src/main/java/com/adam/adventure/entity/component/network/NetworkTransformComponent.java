package com.adam.adventure.entity.component.network;

import com.adam.adventure.domain.EntityInfo;
import com.adam.adventure.domain.message.EntityTransformPacketableMessage;
import com.adam.adventure.lib.flatbuffer.schema.converter.PacketConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.UUID;

/**
 * Responsible for managing updating the server about this entity's transform and processing network updates
 * regarding it.
 */
public class NetworkTransformComponent extends NetworkComponent {
    private static final Logger LOG = LoggerFactory.getLogger(NetworkTransformComponent.class);

    @Inject
    private PacketConverter packetConverter;

    private final boolean authoritative;

    /**
     * @param authoritative Whether this component is owned by the client
     */
    public NetworkTransformComponent(final boolean authoritative) {
        this.authoritative = authoritative;
    }


    @Override
    protected void writeUpdates(final UUID entityId,
                                final OutputMessageQueue outputMessageQueue) {
        final EntityTransformPacketableMessage message
                = new EntityTransformPacketableMessage(entityId, getTransformComponent().getTransform());
        outputMessageQueue.add(message);
    }

    @Override
    protected void receiveUpdates(final EntityInfo entityInfo) {
        if (!authoritative) {
            getTransformComponent().getTransform().set(entityInfo.getTransform());
        }
    }
}
