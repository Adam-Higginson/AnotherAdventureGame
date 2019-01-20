package com.adam.adventure.entity.component.network;

import com.adam.adventure.domain.EntityInfo;
import com.adam.adventure.domain.message.EntityTransformPacketableMessage;
import com.adam.adventure.entity.component.event.MovementComponentEvent;
import com.adam.adventure.lib.flatbuffer.schema.converter.PacketConverter;
import org.joml.Matrix4f;
import org.joml.Vector3f;
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

    private Matrix4f lastReceivedTransform;
    private MovementComponentEvent.MovementType lastReceivedMovementType;

    /**
     * @param authoritative Whether this component is owned by the client
     */
    public NetworkTransformComponent(final boolean authoritative) {
        this.authoritative = authoritative;
    }


    @Override
    protected void update(final float deltaTime) {
        if (lastReceivedTransform != null && lastReceivedMovementType != null) {
            getTransformComponent().getTransform().set(lastReceivedTransform);
            if (lastReceivedMovementType != MovementComponentEvent.MovementType.ENTITY_NO_MOVEMENT) {
                LOG.debug("Next movement type: {}", lastReceivedMovementType);
            }
            broadcastComponentEvent(new MovementComponentEvent(lastReceivedMovementType));
        }
    }

    @Override
    protected void writeNetworkUpdates(final UUID entityId,
                                       final OutputMessageQueue outputMessageQueue) {
        final EntityTransformPacketableMessage message
                = new EntityTransformPacketableMessage(entityId, getTransformComponent().getTransform());
        outputMessageQueue.add(message);
    }

    @Override
    protected void receiveNetworkUpdates(final EntityInfo entityInfo) {
        if (!authoritative) {
            if (lastReceivedTransform == null || lastReceivedTransform.equals(entityInfo.getTransform())) {
                //We've not moved so stop the movement event
                lastReceivedMovementType = MovementComponentEvent.MovementType.ENTITY_NO_MOVEMENT;
                lastReceivedTransform = entityInfo.getTransform();
                return;
            }

            final Vector3f lastPosition = lastReceivedTransform.getTranslation(new Vector3f());
            final Vector3f newPosition = entityInfo.getTransform().getTranslation(new Vector3f());
            final Vector3f difference = newPosition.sub(lastPosition);

            if (!difference.equals(new Vector3f(0.f, 0.f, 0.f))) {
                final Vector3f normalised = difference.normalize();
                if (normalised.y == 1.f) {
                    lastReceivedMovementType = MovementComponentEvent.MovementType.ENTITY_MOVE_NORTH;
                } else if (normalised.y == -1.f) {
                    lastReceivedMovementType = MovementComponentEvent.MovementType.ENTITY_MOVE_SOUTH;
                } else if (normalised.x == 1.f) {
                    lastReceivedMovementType = MovementComponentEvent.MovementType.ENTITY_MOVE_EAST;
                } else if (normalised.x == -1.f) {
                    lastReceivedMovementType = MovementComponentEvent.MovementType.ENTITY_MOVE_WEST;
                }
            }

            lastReceivedTransform = entityInfo.getTransform();
        }
    }
}
